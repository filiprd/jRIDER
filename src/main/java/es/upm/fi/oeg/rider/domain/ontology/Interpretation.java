package es.upm.fi.oeg.rider.domain.ontology;

import es.upm.fi.oeg.rider.domain.ontology.general.Resource;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

@Namespace(Constants.SEALS_METADATA_NS)
@RdfType("Interpretation")
public class Interpretation extends Resource{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9067307475950395128L;

	public Interpretation(String uri){
		super(uri);
	}

}
