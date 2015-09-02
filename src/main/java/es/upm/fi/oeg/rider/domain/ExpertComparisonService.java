package es.upm.fi.oeg.rider.domain;

import java.util.LinkedList;

import Jama.Matrix;
import es.upm.fi.oeg.rider.dataservice.DataService;
import es.upm.fi.oeg.rider.domain.mapping.MappingItem;
import es.upm.fi.oeg.rider.domain.mapping.MatrixMapping;

public class ExpertComparisonService {
	
	
	public void setSupermatrixComparison(ANPMatrix supermatrix, String controlCriterion,
			LinkedList<ComparisonMatrix> supermatrixComparisons){
		
		MatrixMapping supermatrixMapping = supermatrix.getMapping();		
		for (ComparisonMatrix comparisonMatrix : supermatrixComparisons) {			
			if(comparisonMatrix.getId().equals(controlCriterion)){
				int k = 0;
				LinkedList<Integer> indexes = new LinkedList<Integer>();
				MatrixMapping comparisonMatrixMapping = comparisonMatrix.getMapping();
				LinkedList<MappingItem> weightsMappingItems = new LinkedList<MappingItem>();
				for (int i = 0; i < comparisonMatrix.getRowDimension(); i++) {
					if(isCriterionPresent(supermatrixMapping, comparisonMatrixMapping.getCriterionId(i))
							&& !indexes.contains(i)){
						indexes.add(i);
						weightsMappingItems.add(new MappingItem(k++, comparisonMatrixMapping.getCriterionId(i)));
					}
				}
				if(indexes.size() > 1){
					int columnInSupermatrix = supermatrixMapping.getRowNumber(controlCriterion);
					MatrixMapping weightsMapping = new MatrixMapping(controlCriterion, weightsMappingItems);
					int[] in = getIndexes(indexes);
					ComparisonMatrix subComparison = new ComparisonMatrix(controlCriterion, weightsMapping, 
							comparisonMatrix.getMatrix(in, in).getArray());
					Matrix weights = subComparison.getWeights();
					for (int i = 0; i < weights.getRowDimension(); i++) {
						int rowInSupermatrix = supermatrix.getMapping().getRowNumber(
								weightsMapping.getCriterionId(i));
						supermatrix.set(rowInSupermatrix, columnInSupermatrix, weights.get(i, 0));
					}
				}
			}
		}
		
	}

	
	private boolean isCriterionPresent(MatrixMapping supermatrixMapping,
			String criterionId) {
		LinkedList<MappingItem> mappingItems = supermatrixMapping.getMappingItems();
		for (MappingItem mappingItem : mappingItems) {
			if(mappingItem.getCriterionId().equals(criterionId))
				return true;
		}
		return false;
	}

	public void setClusterMatrixComparison(ANPMatrix clusterMatrix, LinkedList<String> criterionUris, String controlCriterion,
			boolean compareOnlyRequirements, LinkedList<String> requirementsCharacteristicsUris,
			ANPMatrix supermatrix, DataService service, LinkedList<ComparisonMatrix> clusterComparisons){
		
		// extracts the comparison matrix related to a given control criterion
		ComparisonMatrix comparisonMatrix = null;
		for (ComparisonMatrix clusterComparsion : clusterComparisons) {
			if(clusterComparsion.getId().equals(controlCriterion)){
				comparisonMatrix = clusterComparsion;
				break;
			}			
		}
		
		// extracts only those comparisons relevant to a given list of characteristics		
		LinkedList<Integer> indexes = new LinkedList<Integer>();
		int k = 0;
		LinkedList<MappingItem> weightsMappingItems = new LinkedList<MappingItem>();
		for (String characteristicUri : criterionUris) {
			int column = comparisonMatrix.getMapping().getRowNumber(characteristicUri);				
			if(!indexes.contains(column) && column != -1 && existDependence(supermatrix, characteristicUri, controlCriterion, service)){
				if(!controlCriterion.equals("Alternatives")){
					if(compareOnlyRequirements && characteristicUri.equals("Alternatives") && 
							!requirementsCharacteristicsUris.contains(controlCriterion))
						continue;
					indexes.add(column);
					weightsMappingItems.add(new MappingItem(k++, characteristicUri));
				}	
				else{
					if(compareOnlyRequirements && !requirementsCharacteristicsUris.contains(characteristicUri))
						continue;
					indexes.add(column);
					weightsMappingItems.add(new MappingItem(k++, characteristicUri));
				}
			}
		}
		MatrixMapping weightsMapping = new MatrixMapping("ClusterMatrixMapping", weightsMappingItems);
				
		
		//rearranging java objects
		int[] in = new int[indexes.size()];
		int dex = 0;			
		for (int i : indexes) {
			in[dex++] = i;
		}
		
		
		LinkedList<MappingItem> subMatrixMappingItems = new LinkedList<MappingItem>();
		for (int i : in) {
			subMatrixMappingItems.add(new MappingItem(i, weightsMapping.getCriterionId(i)));
		}
		MatrixMapping submatrixMapping = new MatrixMapping("subMatrixMapping", subMatrixMappingItems);
				
		Matrix weights = new ComparisonMatrix("comparisonMatrix", submatrixMapping,
				comparisonMatrix.getMatrix(in, in).getArray()).getWeights();

		
		//put weights in appropriate positions in cluster
		int columnInCluster = clusterMatrix.getMapping().getRowNumber(controlCriterion);
		for (int i = 0; i < weights.getRowDimension(); i++) {	
			int rowInCluster = clusterMatrix.getMapping().getRowNumber(weightsMapping.getCriterionId(i));
			clusterMatrix.set(rowInCluster, columnInCluster, weights.get(i, 0));
		}	
		
	}
	
	
	
	/**
	 * For a given characteristics from a cluster matrix, checks if there are elements in a given supermatrix
	 * which are dependent on each other
	 * @param supermatrix
	 * @param rowCharacteristic
	 * @param columnCharacteristic
	 * @param service
	 * @return <i>True</i> if dependence exist, <i>false</i> otherwise
	 */
	private boolean existDependence(ANPMatrix supermatrix, String rowCharacteristic, String 
			columnCharacteristic, DataService service){
		for (int i = 0; i < supermatrix.getRowDimension(); i++) {
			String rowMeasureUri = supermatrix.getMapping().getCriterionId(i);
			if(service.getCharacteristicUriOfMeasure(rowMeasureUri).equals(rowCharacteristic)){
				for (int j = 0; j < supermatrix.getColumnDimension(); j++) {
					String columnMeasureUri = supermatrix.getMapping().getCriterionId(j);
					if(service.getCharacteristicUriOfMeasure(columnMeasureUri).equals(columnCharacteristic)){
						if(supermatrix.get(i, j) != 0)
							return true;
					}
				}
			}
			
		}
		return false;
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
	
	
	
	
}
