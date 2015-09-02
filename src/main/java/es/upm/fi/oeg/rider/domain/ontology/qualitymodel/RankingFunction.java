package es.upm.fi.oeg.rider.domain.ontology.qualitymodel;

import es.upm.fi.oeg.rider.domain.ontology.Constants;
import es.upm.fi.oeg.rider.domain.ontology.general.Resource;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

@Namespace(Constants.QUALITY_MODEL_NS)
@RdfType("RankingFunction")
public class RankingFunction extends Resource{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8233825043596068584L;

	public static RankingFunction HIGHER_BEST = new RankingFunction(Constants.QUALITY_MODEL_NS + "HigherBest");
	
	public static RankingFunction LOWER_BEST = new RankingFunction(Constants.QUALITY_MODEL_NS + "LowerBest");
	
	public RankingFunction(){}
	
	public RankingFunction(String uri){
		super(uri);
	}
}
