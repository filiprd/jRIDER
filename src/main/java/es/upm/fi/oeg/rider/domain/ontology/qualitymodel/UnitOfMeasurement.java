package es.upm.fi.oeg.rider.domain.ontology.qualitymodel;

import es.upm.fi.oeg.rider.domain.ontology.Constants;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace(Constants.MUO)
@RdfType("UnitOfMeasurement")
public class UnitOfMeasurement {

	private String symbol;
	
	public UnitOfMeasurement() {
	}

	public UnitOfMeasurement(String symbol) {
		this.symbol = symbol;
	}

	@RdfProperty(Constants.MUO + "prefSymbol")
	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	
}
