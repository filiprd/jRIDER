package es.upm.fi.oeg.rider.comparisons.interfaces;

import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityValue;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;

public interface AlternativesComparison {

	/**
	 * Compares two quality values using the simple comparison algorithm.
	 * @param value1 Value to compare the second value to
	 * @param value2 Value to compare
	 * @param requirement Requirement to be satisfied (<i>null is permitted</i>)
	 * @return Comparison of two quality values, a natural number on Saaty's scale.
	 */
	public double simpleComparison(QualityValue value1, QualityValue value2, Requirement requirement);
	
	/**
	 * Compares two quality values using based on therir distance and maximum distance in the evalueation dataset
	 * @param value1 Value to compare the second value to
	 * @param value2 Value to compare
	 * @param requirement Requirement to be satisfied (<i>null is permitted</i>)
	 * @param maxResultDifference The maximum difference between values in the evaluation dataset 
	 * @return Comparison of two quality values, a natural number on Saaty's scale.
	 */
	public double maxDistanceComparison(QualityValue value1, QualityValue value2, Requirement requirement, double maxResultDifference);
		
	/**
	 * 
	 * @param value1 Value to compare the second value to
	 * @param value2 Value to compare
	 * @param requirement Requirement to be satisfied (<i>null is permitted</i>)
	 * @param scaleResultsDifference The difference between highest and lowes possible values in the scale
	 * @return
	 */
	public double scaleDistanceComparison(QualityValue value1, QualityValue value2, Requirement requirement);

	public double averageDistanceComparison(QualityValue value1, QualityValue value2, Requirement requirement, double averageResultDifference);
		
//	public double averageComparison(QualityValue value1, QualityValue value2, Requirement requirement);
	
	
}
