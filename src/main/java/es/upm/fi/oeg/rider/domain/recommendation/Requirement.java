package es.upm.fi.oeg.rider.domain.recommendation;

import java.io.Serializable;

import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityMeasure;

public class Requirement implements Serializable{
	
	private QualityMeasure measure;
	
	private String threshold;
	
	public Requirement() {		
	}
	
	public Requirement(QualityMeasure measure) {
		this.measure = measure;
	}

	public Requirement(QualityMeasure measure, String threshold) {
		this.measure = measure;
		this.threshold = threshold;
	}

	public QualityMeasure getMeasure() {
		return measure;
	}

	public void setMeasure(QualityMeasure measure) {
		this.measure = measure;
	}


	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	@Override
	public String toString() {
		return "Requirement [measure=" + measure.getName() + ", threshold="
				+ threshold;
	}
}
