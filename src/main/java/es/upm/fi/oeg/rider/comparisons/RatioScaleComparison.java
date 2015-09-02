package es.upm.fi.oeg.rider.comparisons;

import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityValue;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;

public class RatioScaleComparison extends RatioOrIntervalScaleComparison{

	public RatioScaleComparison(){		
		super();
	}
	
	
	public double scaleDistanceComparison(QualityValue value1, QualityValue value2, Requirement requirement){
		return scaleDistanceComparison(value1, value2, requirement, 100);
	}
}
