package es.upm.fi.oeg.rider.domain.ontology.qualitymodel;

import java.util.Collection;
import java.util.LinkedList;

import es.upm.fi.oeg.rider.domain.ontology.Constants;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace(Constants.QUALITY_MODEL_NS)
@RdfType("OrdinalScale")
public class OrdinalScale extends Scale {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7908889264131760704L;
	
	private Collection<OrdinalScaleItem> ordinalScaleItems;
	
	public OrdinalScale(){
		super();
		ordinalScaleItems = new LinkedList<OrdinalScaleItem>();
	}

	@RdfProperty(Constants.QUALITY_MODEL_NS + "hasOrdinalScaleItem")
	public Collection<OrdinalScaleItem> getOrdinalScaleItems() {
		return ordinalScaleItems;
	}

	public void setOrdinalScaleItems(Collection<OrdinalScaleItem> ordinalScaleItems) {
		this.ordinalScaleItems = ordinalScaleItems;
	}
	
	public void addOrdinalScaleItem(OrdinalScaleItem item){
		if (null != ordinalScaleItems && (!getOrdinalScaleItems().contains(item)))
			getOrdinalScaleItems().add(item);
	}	
	
}
