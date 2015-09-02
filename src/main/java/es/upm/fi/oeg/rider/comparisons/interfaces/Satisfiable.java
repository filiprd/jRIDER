package es.upm.fi.oeg.rider.comparisons.interfaces;

import es.upm.fi.oeg.rider.domain.ontology.qualitymodel.QualityValue;
import es.upm.fi.oeg.rider.domain.recommendation.Requirement;


public interface Satisfiable {

	/**
	 * Checks if the quality value satisfies the specified requirement
	 * @param value Quality value to check upon
	 * @param requirement Requirement to be satisfied
	 * @return <i>True</i> if the requirement is satisfied, <i>false</i> otherwise.
	 */
	public boolean satisfiesRequirement(QualityValue value,	Requirement requirement);
}
