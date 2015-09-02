package es.upm.fi.oeg.rider.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.LinkedList;

import es.upm.fi.oeg.rider.dataservice.DataService;
import es.upm.fi.oeg.rider.domain.mapping.MappingItem;
import es.upm.fi.oeg.rider.domain.mapping.MatrixMapping;
import es.upm.fi.oeg.rider.domain.recommendation.Alternative;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;

public class ANPMatrixFactory {

	private ANPMatrix supermatrix;
	
	private ANPMatrix clusterMatrix;
	
	private ANPMatrix dependenceMatrix;

	private LinkedList<Requirement> requirements;
	
	private LinkedList<ComparisonMatrix> supermatrixComparisons;

	private LinkedList<ComparisonMatrix> clusterMatrixComparisons;
	
	private LinkedList<Alternative> alternatives;
	
	private boolean compareOnlyRequirements;
	
	private DataService service;

	public ANPMatrixFactory(ANPMatrix dependenceMatrix, LinkedList<Requirement> requirements, LinkedList<ComparisonMatrix> supermatrixComparisons,
			LinkedList<ComparisonMatrix> clusterMatrixComparisons, boolean compareOnlyRequirements,
			DataService service) {
		this.requirements = requirements;
		this.supermatrixComparisons = supermatrixComparisons;
		this.clusterMatrixComparisons = clusterMatrixComparisons;
		this.compareOnlyRequirements = compareOnlyRequirements;
		this.service = service;
		this.dependenceMatrix = dependenceMatrix;	
		createSupermatrices();
	}

	public ANPMatrixFactory(ANPMatrix dependenceMatrix,LinkedList<Requirement> requirements, String supermatrixComparisonsFile,
			String clusterComparisonsFile, boolean compareOnlyRequirements, DataService service) 
			throws FileNotFoundException, IOException, ClassNotFoundException {
		
		this.requirements = requirements;
		this.compareOnlyRequirements = compareOnlyRequirements;
		this.supermatrixComparisons = loadComparisonMatrix(supermatrixComparisonsFile);
		this.clusterMatrixComparisons = loadComparisonMatrix(clusterComparisonsFile);
		this.service = service;
		this.dependenceMatrix = dependenceMatrix;
		createSupermatrices();
	}
	
	
	private LinkedList<ComparisonMatrix> loadComparisonMatrix(String filePath)
			throws FileNotFoundException, IOException, ClassNotFoundException { 
		URL url = Thread.currentThread().getContextClassLoader()
				.getResource(filePath);
		String path = url.getFile();
		// remove white spaces encoded with %20
		path = path.replaceAll("%20", " ");

		File dataFile = new File(path);

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile));
		LinkedList<ComparisonMatrix> mm;
		try {
			mm = (LinkedList<ComparisonMatrix>) ois.readObject();
		} catch (ClassCastException e) {
			throw new RuntimeException("Provided file does not contain serialized list of " +
					"comparison matrices");
		}
		ois.close();
		return mm;

	}
	
	private void createSupermatrices(){
		
		//creates supermatrix from requirements containing only criteria 
		MatrixMapping dependenceMatrixMapping = this.dependenceMatrix.getMapping();
		
		// extracting elements from dependence Matrix
		int indexer = 0;
		LinkedList<MappingItem> supermatrixMappingItems = new LinkedList<MappingItem>();				
		LinkedList<Integer> criteriaIndexes = new LinkedList<Integer>();				
		for (Requirement item : requirements) {
			int column = dependenceMatrixMapping.getRowNumber(item.getMeasure().getUri().toString());
			if(!criteriaIndexes.contains(column)){
				criteriaIndexes.add(column);
				supermatrixMappingItems.add(new MappingItem(indexer++, dependenceMatrixMapping.getCriterionId(column)));
			}
			
			// adds all characteristics that influence the one in the 'column' (requirement)
			for (int i = 0; i < dependenceMatrix.getRowDimension(); i++) {
				if(dependenceMatrix.get(i, column) != 0){
//					System.out.println("Row "+i);
					if(!criteriaIndexes.contains(i)){
						criteriaIndexes.add(i);
						supermatrixMappingItems.add(new MappingItem(indexer++, dependenceMatrixMapping.getCriterionId(i)));
					}
				}					
			}
		}
		
		int[] in = getIndexes(criteriaIndexes);

		// creates supermatrix 
		MatrixMapping supermatrixMapping = new MatrixMapping("supermatrixMapping", supermatrixMappingItems);
		ANPMatrix supermatrixOnlyCtiteria = new ANPMatrix(supermatrixMapping, 
				dependenceMatrix.getMatrix(in, in).getArray());
		
		
		fillSupermatrixWithComparisons(supermatrixOnlyCtiteria);
		
		this.alternatives = AlternativesFactory.createAlternativesList(this.requirements, service);
		this.supermatrix = SupermatrixService.fillSupermatrixWithAlternatives(
				supermatrixOnlyCtiteria, this.requirements, this.alternatives, service, this.compareOnlyRequirements);
		initializeClusterMatrix(service);
	}
	
	
	
	private void fillSupermatrixWithComparisons(ANPMatrix supermatrixOnlyCtiteria){
		
		ExpertComparisonService expertClusterComparisons = new ExpertComparisonService();
		for (int i = 0; i < supermatrixOnlyCtiteria.getColumnDimension(); i++) {
			String columnCriterion = supermatrixOnlyCtiteria.getMapping().getCriterionId(i);
			expertClusterComparisons.setSupermatrixComparison(supermatrixOnlyCtiteria, columnCriterion,
					supermatrixComparisons);			
		}
		
	}
	
	private void initializeClusterMatrix(DataService service){
		
		ExpertComparisonService expertClusterComparisons = new ExpertComparisonService();
		
		// Uris of characteristics related to measures from supermatrix
		LinkedList<String> characteristicsUris = 
			SupermatrixService.getCriteriaUris(supermatrix.getMapping(), service);
		characteristicsUris.add("Alternatives");
		
		// Uris characteristics related to measures from requirements
		LinkedList<String> requirementsCharacteristicsUris = 
			SupermatrixService.getRequirementsCriteriaUris(requirements);
		
		//create cluster mapping
		int k = 0;
		LinkedList<MappingItem> clusterMappingItems = new LinkedList<MappingItem>();
		for (String characteristicUri : characteristicsUris) {			
			clusterMappingItems.add(new MappingItem(k++, characteristicUri));
		}
		MatrixMapping clusterMapping = new MatrixMapping("clusterMatrixMapping", clusterMappingItems);
		k--;
		
		// creates a cluster matrix object
		this.clusterMatrix = new ANPMatrix(clusterMapping,k+1, k+1);
		
		// fills the cluster matrix with comparisons
		for (int i = 0; i < this.clusterMatrix.getColumnDimension(); i++) {
			expertClusterComparisons.setClusterMatrixComparison(this.clusterMatrix, characteristicsUris, 
					clusterMapping.getCriterionId(i),
					compareOnlyRequirements, requirementsCharacteristicsUris,
					this.supermatrix, service, this.clusterMatrixComparisons);
		}
		
	}
		
	/**
	 * Converts a list of integers into the array of integers
	 * @param clusterIndexes
	 * @return
	 */
	private int[] getIndexes(LinkedList<Integer> clusterIndexes) {
		int[] in = new int[clusterIndexes.size()];
		int dex = 0;		
		for (int i : clusterIndexes) {
			in[dex++] = i;
		}
		return in;
	}
	
	public LinkedList<Alternative> getAlternatives() {
		return alternatives;
	}

	public ANPMatrix getSupermatrix() {
		return supermatrix;
	}

	public ANPMatrix getClusterMatrix() {
		return clusterMatrix;
	}

	public LinkedList<Requirement> getRequirements() {
		return requirements;
	}

	public boolean isCompareOnlyRequirements() {
		return compareOnlyRequirements;
	}
	
}
