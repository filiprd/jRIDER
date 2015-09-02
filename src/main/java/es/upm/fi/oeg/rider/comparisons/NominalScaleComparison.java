package es.upm.fi.oeg.rider.comparisons;

import es.upm.fi.oeg.rider.comparisons.interfaces.AlternativesComparison;
import es.upm.fi.oeg.rider.comparisons.interfaces.Satisfiable;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityValue;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;


public class NominalScaleComparison implements AlternativesComparison,Satisfiable{


	public double compare(QualityValue value1, QualityValue value2, Requirement requirement) {
		String result1 = value1.getValue().toString();
		String result2 = value2.getValue().toString();
		String threshold = requirement.getThreshold();
		
		if(result1.equalsIgnoreCase(threshold) && !result2.equalsIgnoreCase(threshold))
			return 9;
		
		if(!result1.equalsIgnoreCase(threshold) && result2.equalsIgnoreCase(threshold))
			return 0.11;
		
		return 1;
	}

	@Override
	public boolean satisfiesRequirement(QualityValue value,
			Requirement requirement) {

		String result = value.getValue().toString();
		String threshold = requirement.getThreshold();
		if(result.equalsIgnoreCase(threshold))
			return true;
		return false;
	}

	@Override
	public double simpleComparison(QualityValue value1, QualityValue value2,
			Requirement requirement) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double maxDistanceComparison(QualityValue value1,
			QualityValue value2, Requirement requirement,
			double maxResultDifference) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double scaleDistanceComparison(QualityValue value1,
			QualityValue value2, Requirement requirement) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double averageDistanceComparison(QualityValue value1,
			QualityValue value2, Requirement requirement,
			double averageResultDifference) {
		// TODO Auto-generated method stub
		return 0;
	}

}
