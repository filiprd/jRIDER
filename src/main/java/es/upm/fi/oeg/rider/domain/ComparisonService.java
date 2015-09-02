package es.upm.fi.oeg.rider.domain;

import es.upm.fi.oeg.rider.comparisons.CharacteristicsComparison;
import es.upm.fi.oeg.rider.comparisons.interfaces.AlternativesComparison;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityMeasure;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityValue;
import es.upm.fi.oeg.rider.domain.recommendation.Alternative;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;

public class ComparisonService {

	/**
	 * Compares two alternatives w.r.t. certain quality measure
	 * @param alternative1
	 * @param alternative2
	 * @param measure
	 * @return
	 */
	public static double compareAlternatives(Alternative alternative1, Alternative alternative2,
			QualityMeasure measure) {
		
		QualityValue value1 = alternative1.getQualityValue(measure.getUri().toString());
		QualityValue value2 = alternative2.getQualityValue(measure.getUri().toString());
		
		try {
			
			AlternativesComparison comparator = (AlternativesComparison)
				Class.forName("es.upm.fi.oeg.rider.comparisons." + measure.getScale().getClass().getSimpleName()
						+ "Comparison").newInstance();
			
			double result = comparator.simpleComparison(value1, value2, null);
			
			if(result<0){
				System.err.println("RESULT OF THE COMPARISON IS LESS THAN ZERO");
			}
			
			return result;
			
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
			
		
		return -1;
	}
	
	
	/**
	 * Compares two alternatives w.r.t. certain requirement
	 * @param alternative1
	 * @param alternative2
	 * @param requirement
	 * @return
	 */
	public static double compareAlternatives(Alternative alternative1, Alternative alternative2,
			Requirement requirement) {
		return compareAlternatives(alternative1,alternative2,requirement.getMeasure());		
	}
	
	
	// compares the contribution of two criteria w.r.t an alternative
	public static double compareCharacteristics(Alternative alternative,
			Requirement requirement1, Requirement requirement2) {
		
		QualityValue value1 = alternative.getQualityValue(requirement1.getMeasure().getUri().toString());
		QualityValue value2 = alternative.getQualityValue(requirement2.getMeasure().getUri().toString());
		
		return CharacteristicsComparison.compare(value1,requirement1,
				value2,requirement2);

	}
	
	

}
