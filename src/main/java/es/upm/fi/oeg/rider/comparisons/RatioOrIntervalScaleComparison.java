package es.upm.fi.oeg.rider.comparisons;

import java.text.NumberFormat;
import java.util.Locale;

import es.upm.fi.oeg.rider.comparisons.interfaces.AlternativesComparison;
import es.upm.fi.oeg.rider.comparisons.interfaces.Satisfiable;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.IntervalScale;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityValue;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.RankingFunction;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.RatioScale;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;

public abstract class RatioOrIntervalScaleComparison implements AlternativesComparison, Satisfiable {

	NumberFormat format = NumberFormat.getInstance(Locale.UK);
	
	public RatioOrIntervalScaleComparison(){		
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
	}
		

	@Override
	public boolean satisfiesRequirement(QualityValue value,
			Requirement requirement) {

		double result = Double.parseDouble(value.getValue().toString());
		double threshold = Double.parseDouble(requirement.getThreshold());
		
		RankingFunction rankingFunction;
		try {
			rankingFunction = RatioScale.class.cast(value.getForMeasure().getScale()).getRankingFunction();
		} catch (Exception e) {
			rankingFunction = IntervalScale.class.cast(value.getForMeasure().getScale()).getRankingFunction();
		}
		
		if(rankingFunction.equals(RankingFunction.HIGHER_BEST))
			if(result > threshold)
				return true;
			else
				return false;
		
		if(rankingFunction.equals(RankingFunction.LOWER_BEST))
			if(result > threshold)
				return false;
			else
				return true;
		
		return false;

	}

	@Override
	public double simpleComparison(QualityValue value1, QualityValue value2,
			Requirement requirement) {

		double result1 = Double.parseDouble(value1.getValue().toString());
		double result2 = Double.parseDouble(value2.getValue().toString());
		

		RankingFunction rankingFunction;
		try {
			rankingFunction = RatioScale.class.cast(value1.getForMeasure().getScale()).getRankingFunction();
		} catch (Exception e) {
			rankingFunction = IntervalScale.class.cast(value1.getForMeasure().getScale()).getRankingFunction();
		}
		
		if(result1 == result2)
			return 1;
		
		if(requirement == null)
			return simpleComparison(result1, result2, rankingFunction);
		
		double threshold = Double.parseDouble(requirement.getThreshold());
		
		if(rankingFunction.equals(RankingFunction.HIGHER_BEST)){
			
				if(result1 >= threshold && threshold > result2)
					return 9;
				
				if(result2 >= threshold && threshold > result1)
					return 0.11;
				
				if(result1 > result2)
					return 4;
				if(result2 > result1)
					return 0.25;			
				
		}
		
		if(rankingFunction.equals(RankingFunction.LOWER_BEST)){
			
			if(result1 <= threshold && threshold < result2)
				return 9;
			
			if(result2 <= threshold && threshold < result1)
				return 0.11;
			
			if(result1 > result2)
				return 0.25;
			if(result2 > result1)
				return 4;

		}
		
		return -1;
	}

	private double simpleComparison(double result1, double result2, RankingFunction rankingFunction){
		
		if(rankingFunction.equals(RankingFunction.HIGHER_BEST)){
			if(result1 > result2)
				return 9;
			return 0.11;
		}
		
		if(rankingFunction.equals(RankingFunction.LOWER_BEST)){
			if(result1 < result2)
				return 9;
			return 0.11;
		}
		
		return -1;
	}
	
	@Override
	public double maxDistanceComparison(QualityValue value1,
			QualityValue value2, Requirement requirement, double maxResultDifference) {
		
		double result1 = Double.parseDouble(value1.getValue().toString());
		double result2 = Double.parseDouble(value2.getValue().toString());
		

		RankingFunction rankingFunction;
		try {
			rankingFunction = RatioScale.class.cast(value1.getForMeasure().getScale()).getRankingFunction();
		} catch (Exception e) {
			rankingFunction = IntervalScale.class.cast(value1.getForMeasure().getScale()).getRankingFunction();
		}
		
		if(result1 == result2)
			return 1;
		
		if(requirement == null)
			return getComparison(result1, result2, maxResultDifference, rankingFunction,9);
		
		double threshold = Double.parseDouble(requirement.getThreshold());
		
		double threesholdComparison = compareWithThreshold(rankingFunction, result1, result2, threshold);
		
		if(threesholdComparison != -1)
			return threesholdComparison;
		
		return getComparison(result1, result2, maxResultDifference, rankingFunction, 9);
	}
	
		
	protected double scaleDistanceComparison(QualityValue value1, QualityValue value2, Requirement requirement, double scaleResultDifference){
		double result1 = Double.parseDouble(value1.getValue().toString());
		double result2 = Double.parseDouble(value2.getValue().toString());
		

		RankingFunction rankingFunction;
		try {
			rankingFunction = RatioScale.class.cast(value1.getForMeasure().getScale()).getRankingFunction();
		} catch (Exception e) {
			rankingFunction = IntervalScale.class.cast(value1.getForMeasure().getScale()).getRankingFunction();
		}
		
		if(result1 == result2)
			return 1;
		
		if(requirement == null)
			return getComparison(result1, result2, scaleResultDifference, rankingFunction,9);
		
		double threshold = Double.parseDouble(requirement.getThreshold());
		
		double threesholdComparison = compareWithThreshold(rankingFunction, result1, result2, threshold);
		
		if(threesholdComparison != -1)
			return threesholdComparison;
		
		return getComparison(result1, result2, scaleResultDifference, rankingFunction, 9);
	}
	
	public double averageDistanceComparison(QualityValue value1, QualityValue value2, Requirement requirement, double averageResultDifference){
		double result1 = Double.parseDouble(value1.getValue().toString());
		double result2 = Double.parseDouble(value2.getValue().toString());
		

		RankingFunction rankingFunction;
		try {
			rankingFunction = RatioScale.class.cast(value1.getForMeasure().getScale()).getRankingFunction();
		} catch (Exception e) {
			rankingFunction = IntervalScale.class.cast(value1.getForMeasure().getScale()).getRankingFunction();
		}
		
		if(result1 == result2)
			return 1;
		
		if(requirement == null)
			return getComparison(result1, result2, averageResultDifference, rankingFunction,9);
		
		double threshold = Double.parseDouble(requirement.getThreshold());
		
		double threesholdComparison = compareWithThreshold(rankingFunction, result1, result2, threshold);
		
		if(threesholdComparison != -1)
			return threesholdComparison;
		
		return getComparison(result1, result2, averageResultDifference, rankingFunction, 9);
	}
	
	
	/**
	 * Compares two values with respect to requirement. This method only chcecks if there is only
	 * one value that is better than the threshold, and returns a result.
	 * If both values are better/worse than a threshold, -1 is returned.
	 * @param rankingFunction
	 * @param r1
	 * @param r2
	 * @param t
	 * @return
	 */
	private double compareWithThreshold(RankingFunction rankingFunction, double r1, double r2, double t){
		if(rankingFunction.equals(RankingFunction.HIGHER_BEST)){			
			if(r1 >= t && t > r2)
				return 9;			
			if(r2 >= t && t > r1)
				return 0.11;
			
		}
		
		if(rankingFunction.equals(RankingFunction.LOWER_BEST)){				
			if(r1 <= t && t < r2)
				return 9;			
			if(r2 <= t && t < r1)
				return 0.11;
			
		}
		
		return -1;
	}

	
	/**
	 * Compares two results, without considering the threshold.
	 * In this method it is assumed that threshold is not taken into account, or that both values are
	 * better/worse than the threshold
	 * @param r1
	 * @param r2
	 * @param distance
	 * @param rankingFunction
	 * @param outputCardinality
	 * @return
	 */
	private double getComparison(double r1, double r2, double distance, RankingFunction rankingFunction, int outputCardinality){
		if(rankingFunction.equals(RankingFunction.HIGHER_BEST)){			
			if(r1 > r2){
				Double d = Math.rint(outputCardinality*(r1-r2)/distance + 0.5);	
				if(d>9)
					return 9;
				if(d<1)
					return 1;
				return d;
			}
			Double d = Math.rint(outputCardinality*(Math.abs(r1-r2))/distance + 0.5);
			if(d>9)
				d = 0.11;
			if(d<1)
				return 1;
			return Double.parseDouble(format.format(1/d));
		}
		
		
		if(rankingFunction.equals(RankingFunction.LOWER_BEST)){				
			if(r1-r2 > 0){
				Double d = Math.rint(outputCardinality*(r1-r2)/distance + 0.5);
				if(d>9)
					d = 0.11;
				if(d<1)
					return 1;
				return Double.parseDouble(format.format(1/d));
			}
			Double d = Math.rint(outputCardinality*Math.abs(r1-r2)/distance + 0.5);	
			if(d>9)
				return 9;
			if(d<1)
				return 1;
			return d;			
		}
		return -1;
	}

}
