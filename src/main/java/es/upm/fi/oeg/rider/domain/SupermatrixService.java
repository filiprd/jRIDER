package es.upm.fi.oeg.rider.domain;

import java.util.LinkedList;

import Jama.Matrix;
import es.upm.fi.oeg.rider.dataservice.DataService;
import es.upm.fi.oeg.rider.domain.mapping.MappingItem;
import es.upm.fi.oeg.rider.domain.mapping.MatrixMapping;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.IntervalScale;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityMeasure;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.RankingFunction;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.RatioScale;
import es.upm.fi.oeg.rider.domain.recommendation.Alternative;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;

public class SupermatrixService {


//	public static DataService service = new DataService();
	
	// fills the supermatrix with alternatives comparisons
	public static ANPMatrix fillSupermatrixWithAlternatives(ANPMatrix supermatrix, 
			LinkedList<Requirement> requirements, LinkedList<Alternative> alternatives,
			DataService service, boolean onlyRequirement) {
		if(onlyRequirement)
			return fillSupermatrixWithAlternativesOnlyRequirementsComparison(supermatrix, requirements, alternatives, service);
		else
			return fillSupermatrixWithAlternativesALLComparisons(supermatrix, requirements, alternatives, service);
	}

	
	// fills the supermatrix with alternatives comparisons
	public static ANPMatrix fillSupermatrixWithAlternativesOnlyRequirementsComparison(ANPMatrix supermatrix, 
			LinkedList<Requirement> requirements, LinkedList<Alternative> alternatives,
			DataService service) {
						
		int k = supermatrix.getRowDimension();
		ANPMatrix extendedSupermatrix = supermatrix.extend(alternatives);
		
			
		//--------------------------------
		// adds comparisons of alternatives w.r.t. characteristics
		//--------------------------------
		
		for (Requirement requirement : requirements) {
			Matrix mat = compareAlternatives(requirement,alternatives, service);	
			
			// columnIndex to put the comparison
			int columnIndex = extendedSupermatrix.getMapping().
				getRowNumber(requirement.getMeasure().getUri().toString());
			extendedSupermatrix.setMatrixColumn(k, k+alternatives.size()-1, columnIndex, mat);			
		}
		
		
		//--------------------------------
		// adds comparisons of characteristics w.r.t. alternatives
		//--------------------------------
		
		LinkedList<String> alreadyCompared = new LinkedList<String>();
		for (int i = 0; i < requirements.size(); i++) {
			Requirement r = requirements.get(i);
			LinkedList<Requirement> cluster = new LinkedList<Requirement>();
			cluster.add(r);
			if(alreadyCompared.contains(r.getMeasure().getQualityCharacteristic().getUri().toString()))
				continue;
			alreadyCompared.add(r.getMeasure().getQualityCharacteristic().getUri().toString());
			for (int j = i+1; j < requirements.size(); j++) {
				if(r.getMeasure().getQualityCharacteristic().getUri().
						equals(requirements.get(j).getMeasure().getQualityCharacteristic().getUri()))
					cluster.add(requirements.get(j));
			}
			if(cluster.size() == 1){
				Matrix ones = new Matrix(1,alternatives.size(),1);
				extendedSupermatrix.setMatrixRow(extendedSupermatrix.getMapping().getRowNumber(cluster.get(0).
						getMeasure().getUri().toString()), k, k+alternatives.size()-1, ones);
			}
			else{				
				for (Alternative alternative : alternatives) {			
					Matrix mat = compareCharacteristics(cluster,alternative, service);						
					extendedSupermatrix.setMatrixColumn(getPositionsForClusterRequirements(extendedSupermatrix, cluster),
							extendedSupermatrix.getMapping().getRowNumber(alternative.getId()),mat);					
				}
			}
		}	
					
		return extendedSupermatrix;
		
	}


	// compares the contribution of characteristics w.r.t an alternative and returns weights
	public static Matrix compareCharacteristics(LinkedList<Requirement> requirements,
			Alternative alternative, DataService service) {

		int size = requirements.size();
		ComparisonMatrix comparison = new ComparisonMatrix(size,size);
		
		for (int i = 0; i < size; i++) {
			for (int j = i; j < size; j++) {
				if(i == j){
					comparison.set(i, j, 1);
					continue;
				}
				double result = ComparisonService.compareCharacteristics(alternative,
						requirements.get(i), requirements.get(j));
				comparison.set(i, j, result);
				comparison.set(j, i, 1/result);
			}
		}

		Matrix weights = comparison.getWeights();
		
		return weights;
	}

	// compares alternatives w.r.t characteristic and returns weights
	public static Matrix compareAlternatives(Requirement requirement,
			LinkedList<Alternative> alternatives, DataService service) {
		
		int size = alternatives.size();
		ComparisonMatrix comparison = new ComparisonMatrix(size,size);
		
		for (int i = 0; i < size; i++) {
			for (int j = i; j < size; j++) {
				if(i == j){
					comparison.set(i, j, 1);
					continue;
				}
				double result = ComparisonService.compareAlternatives(alternatives.get(i), 
						alternatives.get(j),requirement);
				
				comparison.set(i, j, result);
				comparison.set(j, i, 1/result);
			}
		}
		
		Matrix weights = comparison.getWeights();		
		return weights;
	}
	
	// compares alternatives w.r.t characteristic and returns weights
	public static Matrix compareAlternatives(QualityMeasure measure,
			LinkedList<Alternative> alternatives, DataService service) {

		int size = alternatives.size();
		ComparisonMatrix comparison = new ComparisonMatrix(size, size);

		for (int i = 0; i < size; i++) {
			for (int j = i; j < size; j++) {
				if (i == j) {
					comparison.set(i, j, 1);
					continue;
				}
				double result = ComparisonService.compareAlternatives(
						alternatives.get(i), alternatives.get(j), measure);

				comparison.set(i, j, result);
				comparison.set(j, i, 1 / result);
			}
		}

		Matrix weights = comparison.getWeights();
		return weights;
	}
		
	
	
	// returns the array of row indexes that corresponds to the list of requirements
	private static int[] getPositionsForClusterRequirements(ANPMatrix supermatrix,
			LinkedList<Requirement> cluster) {
		int[] indexes = new int[cluster.size()];
		int k = 0;
		for (Requirement requirement : cluster) {
			indexes[k++] = supermatrix.getMapping().getRowNumber(requirement.getMeasure().
					getUri().toString());
		}
		return indexes;
	}
	
	
	public static LinkedList<String> getRequirementsCriteriaUris(LinkedList<Requirement> requirements){
		LinkedList<String> requirementsCharacteristicsUris = new  LinkedList<String>();
		for (Requirement req : requirements) {
			String uri = req.getMeasure().getQualityCharacteristic().getUri().toString();
			if(!requirementsCharacteristicsUris.contains(uri))
				requirementsCharacteristicsUris.add(uri);
		}
		return requirementsCharacteristicsUris;
	}
	
	
	public static LinkedList<String> getCriteriaUris(MatrixMapping 
			supermatrixMapping, DataService service){		
		LinkedList<String> requirementsCharacteristicsUris = new  LinkedList<String>();
		for (MappingItem item : supermatrixMapping.getMappingItems()) {
			if(item.getCriterionId().contains("Alternatives"))
				continue;
			String uri = service.getCharacteristicUriOfMeasure(
					item.getCriterionId());
			if(!requirementsCharacteristicsUris.contains(uri))
				requirementsCharacteristicsUris.add(uri);
		}
		return requirementsCharacteristicsUris;
	}

	
	/**
	 * Fills the supermatrix with alternatives, which are compared not only to requirements but to
	 * all measures in the supermatrix
	 * @param supermatrix
	 * @param requirements
	 * @param alternatives
	 * @param service
	 * @return
	 */
	public static ANPMatrix fillSupermatrixWithAlternativesALLComparisons(ANPMatrix supermatrix, 
			LinkedList<Requirement> requirements, LinkedList<Alternative> alternatives,
			DataService service) {
						
		int k = supermatrix.getRowDimension();
		ANPMatrix extendedSupermatrix = supermatrix.extend(alternatives);
						
		for (int i = 0; i<k; i++) {
			String measureUri = extendedSupermatrix.getMapping().getCriterionId(i);
			Requirement requirement = getRequirement(requirements, measureUri, service);
			AlternativesFactory.addMeasureToAlternatives(alternatives,measureUri, service);
			Matrix mat = compareAlternatives(requirement,alternatives, service);	
						
			// columnIndex to put the comparison
			int columnIndex = extendedSupermatrix.getMapping().
				getRowNumber(requirement.getMeasure().getUri().toString());
			extendedSupermatrix.setMatrixColumn(k, k+alternatives.size()-1, columnIndex, mat);			
		}
		
		
		//--------------------------------
		// adds comparisons of characteristics w.r.t. alternatives
		//--------------------------------
		
		
		LinkedList<String> alreadyCompared = new LinkedList<String>();
		for (int i = 0; i < k; i++) {
			String measureUri = extendedSupermatrix.getMapping().getCriterionId(i);
			Requirement r = getRequirement(requirements, measureUri, service);
			LinkedList<Requirement> cluster = new LinkedList<Requirement>();
			cluster.add(r);
			if(alreadyCompared.contains(r.getMeasure().getQualityCharacteristic().getUri().toString()))
				continue;
			alreadyCompared.add(r.getMeasure().getQualityCharacteristic().getUri().toString());
			for (int j = i+1; j < k; j++) {
				String measureUri2 = extendedSupermatrix.getMapping().getCriterionId(j);
				Requirement r2 = getRequirement(requirements, measureUri2, service);
				if(r.getMeasure().getQualityCharacteristic().getUri().
						equals(r2.getMeasure().getQualityCharacteristic().getUri()))
					cluster.add(r2);
			}
			if(cluster.size() == 1){
				Matrix ones = new Matrix(1,alternatives.size(),1);
				extendedSupermatrix.setMatrixRow(extendedSupermatrix.getMapping().getRowNumber(cluster.get(0).
						getMeasure().getUri().toString()), k, k+alternatives.size()-1, ones);
			}
			else{				
				for (Alternative alternative : alternatives) {			
					Matrix mat = compareCharacteristics(cluster,alternative, service);	
					
					extendedSupermatrix.setMatrixColumn(getPositionsForClusterRequirements(extendedSupermatrix, cluster),
							extendedSupermatrix.getMapping().getRowNumber(alternative.getId()),mat);					
				}
			}
		}					
		
		return extendedSupermatrix;
		
	}
	
	
	/**
	 * Returns the requirement that is related to a given measure. If the measure is not in the requirements list
	 * a new requirement is created for that measure with the threshold equals to the best value, 
	 * so that a comparison can be performed.
	 * @param requirements
	 * @param measureUri
	 * @param service
	 * @return
	 */
	private static Requirement getRequirement(LinkedList<Requirement> requirements, String measureUri, DataService service){
		for (Requirement requirement : requirements) {
			if(requirement.getMeasure().getUri().toString().equals(measureUri))
				return requirement;
		}

		QualityMeasure measure = service.getQualityMeasureObject(measureUri);
		if(measure.getScale().getClass().getSimpleName().equalsIgnoreCase("RatioScale")){
			RatioScale scale = (RatioScale) measure.getScale();
			String threshold = "";
			if(scale.getRankingFunction().equals(RankingFunction.HIGHER_BEST))
				threshold = "1";
			if(scale.getRankingFunction().equals(RankingFunction.LOWER_BEST))
				threshold = "0";
			return new Requirement(measure,threshold);						
		}
		if(measure.getScale().getClass().getSimpleName().equalsIgnoreCase("IntervalScale")){
			IntervalScale scale = (IntervalScale) measure.getScale();
			String threshold = "";
			if(scale.getRankingFunction().equals(RankingFunction.HIGHER_BEST))
				threshold = String.valueOf(scale.getUpperBoundry());
			if(scale.getRankingFunction().equals(RankingFunction.LOWER_BEST))
				threshold = String.valueOf(scale.getLowerBoundry());
			return new Requirement(measure,threshold);								
		}
		return null;
	}
}
