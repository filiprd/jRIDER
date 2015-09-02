package es.upm.fi.oeg.rider.comparisons;



import java.util.Collection;

import es.upm.fi.oeg.rider.comparisons.interfaces.AlternativesComparison;
import es.upm.fi.oeg.rider.comparisons.interfaces.Satisfiable;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.OrdinalScale;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.OrdinalScaleItem;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityValue;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;


public class OrdinalScaleComparison implements  AlternativesComparison, Satisfiable {


	public double compare(QualityValue value1, QualityValue value2, Requirement requirement) {
		Collection<OrdinalScaleItem> values = (OrdinalScale.class.cast(requirement
				.getMeasure().getScale()).getOrdinalScaleItems());
		String result1 = (String)value1.getValue();
		String result2 = (String)value2.getValue();
		String threshold = requirement.getThreshold();
		int resultRanking1 = 0;
		int resultRanking2 = 0;
		int thresholdRanking = 0;
		
		for (OrdinalScaleItem ordinalScaleItem : values) {
			if(ordinalScaleItem.getName().equalsIgnoreCase(threshold))
				thresholdRanking = ordinalScaleItem.getRanking();
			if(ordinalScaleItem.getName().equalsIgnoreCase(result1))
				resultRanking1 = ordinalScaleItem.getRanking();
			if(ordinalScaleItem.getName().equalsIgnoreCase(result2))
				resultRanking2 = ordinalScaleItem.getRanking();
		}
		
		if(resultRanking1 <= thresholdRanking && thresholdRanking < resultRanking2)
			return 9;
		
		if(resultRanking2 <= thresholdRanking && thresholdRanking < resultRanking1)
			return 0.11;
		
		if(resultRanking1 > thresholdRanking && resultRanking2 > thresholdRanking && resultRanking1 > resultRanking2)
			return 0.2;
		if(resultRanking1 > thresholdRanking && resultRanking2 > thresholdRanking && resultRanking2 > resultRanking1)
			return 5;
		
		if(resultRanking1 < thresholdRanking && resultRanking2 < thresholdRanking && resultRanking1 > resultRanking2)
			return 0.2;
		if(resultRanking1 < thresholdRanking && resultRanking2 < thresholdRanking && resultRanking2 > resultRanking1)
			return 5;
		
		if(result1 == result2)
			return 1;
		
		return -1;
	}

	@Override
	public boolean satisfiesRequirement(QualityValue value,
			Requirement requirement) {

		Collection<OrdinalScaleItem> values = (OrdinalScale.class.cast(requirement
				.getMeasure().getScale()).getOrdinalScaleItems());
		String result = (String)value.getValue();
		String threshold = requirement.getThreshold();
		int resultRanking = 0;
		int thresholdRanking = 0;
		
		for (OrdinalScaleItem ordinalScaleItem : values) {
			if(ordinalScaleItem.getName().equalsIgnoreCase(threshold))
				thresholdRanking = ordinalScaleItem.getRanking();
			if(ordinalScaleItem.getName().equalsIgnoreCase(result))
				resultRanking = ordinalScaleItem.getRanking();
		}
		
		if(resultRanking>thresholdRanking)
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
