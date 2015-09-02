package es.upm.fi.oeg.rider.domain.ontology.qualitymodel;

import es.upm.fi.oeg.rider.domain.ontology.Constants;
import es.upm.fi.oeg.rider.domain.ontology.general.Resource;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace(Constants.QUALITY_MODEL_NS)
@RdfType("OrdinalScaleItem")
public class OrdinalScaleItem extends Resource{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8812992452336402575L;

	private String name;
	
	private int ranking;
	
	public OrdinalScaleItem(){
		super();
	}
	
	public OrdinalScaleItem(String name, int ranking){
		super();
		this.name = name;
		this.ranking = ranking;
	}

	@RdfProperty("http://purl.org/dc/terms/title")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@RdfProperty(Constants.QUALITY_MODEL_NS + "hasRanking")
	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}
	
	
}
