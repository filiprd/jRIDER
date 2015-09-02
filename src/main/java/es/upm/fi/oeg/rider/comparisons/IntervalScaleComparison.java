package es.upm.fi.oeg.rider.comparisons;

import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.IntervalScale;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityValue;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;

public class IntervalScaleComparison extends RatioOrIntervalScaleComparison {

	public IntervalScaleComparison(){		
		super();
	}
	
	public double scaleDistanceComparison(QualityValue value1, QualityValue value2, Requirement requirement){
		IntervalScale scale = IntervalScale.class.cast(value1.getForMeasure().getScale());
		double scaleResultsDifference = scale.getUpperBoundry() - scale.getLowerBoundry();
		return scaleDistanceComparison(value1, value2, requirement, scaleResultsDifference);
	}
}
