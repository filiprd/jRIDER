package es.upm.fi.oeg.rider.domain.ontology.qualitymodel;

import java.util.Collection;
import java.util.LinkedList;

import es.upm.fi.oeg.rider.domain.ontology.Constants;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace(Constants.QUALITY_MODEL_NS)
@RdfType("NominalScale")
public class NominalScale extends Scale {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5643081803478058809L;
	
	private Collection<String> labels;
	
	public NominalScale(){
		super();
		this.labels = new LinkedList<String>();
	}

	@RdfProperty(Constants.QUALITY_MODEL_NS + "hasLabel")
	public Collection<String> getLabels() {
		return labels;
	}

	public void setLabels(Collection<String> labels) {
		this.labels = labels;
	}
	
	public void addLebael(String label){
		if (null != labels && (!getLabels().contains(label)))
			getLabels().add(label);
	}	
	
	
}
