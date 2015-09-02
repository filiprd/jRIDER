package es.upm.fi.oeg.rider.domain.recommendation;

import java.util.LinkedList;

import Jama.Matrix;
import es.upm.fi.oeg.rider.dataservice.DataService;
import es.upm.fi.oeg.rider.domain.ComparisonMatrix;
import es.upm.fi.oeg.rider.domain.ComparisonService;
import es.upm.fi.oeg.rider.domain.SupermatrixService;
import es.upm.fi.oeg.rider.domain.mapping.MappingItem;
import es.upm.fi.oeg.rider.domain.mapping.MatrixMapping;
import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityMeasure;


public class Comparison {

	/**
	 * Returns the better alternative among the two w.r.t. given measure
	 * @param alternative1
	 * @param alternative2
	 * @param measure
	 * @return
	 */
	public Alternative getBetterAlternative(Alternative alternative1, Alternative alternative2, 
			QualityMeasure measure){
		return getBetter(alternative1, alternative2,
				ComparisonService.compareAlternatives(alternative1, alternative2, measure));
	}
	
	public Alternative getBetterAlternative(Alternative alternative1, Alternative alternative2, 
			QualityMeasure measure, double threshold){
		Requirement r = new Requirement(measure, String.valueOf(threshold));
		return getBetter(alternative1, alternative2,
				ComparisonService.compareAlternatives(alternative1, alternative2, r));
	}
	
	public Alternative getBetterAlternative(Alternative alternative1, Alternative alternative2, 
			QualityMeasure measure, String threshold){
		Requirement r = new Requirement(measure, threshold);
		return getBetter(alternative1, alternative2,
				ComparisonService.compareAlternatives(alternative1, alternative2, r));
	}
	
	public Alternative getBetterAlternative(Alternative alternative1, Alternative alternative2, 
			Requirement requirement){
		return getBetter(alternative1, alternative2,
				ComparisonService.compareAlternatives(alternative1, alternative2, requirement));
	}
	
	public void orderAlternatives(LinkedList<Alternative> alternatives, 
			QualityMeasure measure){
		DataService service = new DataService();
		Matrix comparison = SupermatrixService.compareAlternatives(measure, alternatives, service);
		orderAlternatives(alternatives,comparison);
		
	}
	
	public void orderAlternatives(LinkedList<Alternative> alternatives, 
			QualityMeasure measure, String threshold){
		DataService service = new DataService();
		Matrix comparison = SupermatrixService.compareAlternatives(new Requirement(measure, threshold), 
				alternatives, service);
		orderAlternatives(alternatives,comparison);
		
	}
	
	public void orderAlternatives(LinkedList<Alternative> alternatives, 
			Requirement requirement){
		DataService service = new DataService();
		Matrix comparison = SupermatrixService.compareAlternatives(requirement, 
				alternatives, service);
		orderAlternatives(alternatives,comparison);
		
	}
	
	
	private void orderAlternatives(
			LinkedList<Alternative> alternatives, Matrix comparison) {
		for (int i = 0; i < alternatives.size()-1; i++) {
			for (int j = i+1; j < alternatives.size(); j++) {
				if(comparison.get(i, 0) < comparison.get(j, 0)){
					Alternative temp = alternatives.get(i);
					alternatives.set(i, alternatives.get(j));
					alternatives.set(j, temp);					
				}
			}
		}
	}

	
	private Alternative getBetter(Alternative alternative1, Alternative alternative2, double comparison){
		if (comparison == 1)
			return null;
		if (comparison>1)
			return alternative1;
		return alternative2;
	}
	
	
}
