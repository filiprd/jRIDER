package es.upm.fi.oeg.rider.domain.mapping;

import java.io.Serializable;

/**
 * An item in the matrix mapping. It contains the criterion Id that an entry in the
 * matrix represents, and its position in the matrix
 * @author Filip
 *
 */
public class MappingItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 838475341076157960L;
	
	/**
	 * Row number of the criterion
	 */
	private int rowNumber;
	
	/**
	 * Id of the criterion 
	 */
	private String criterionId;

	
	/**
	 * Creates a mapping item
	 * @param rowNumber
	 * 					The row number of the criterion in the mapping item
	 * @param criterionId
	 * 					An Id of the criterion in the mapping item
	 */
	public MappingItem(int rowNumber, String criterionId) {
		if(rowNumber < 0)
			throw new IllegalArgumentException("Row number cennot be negative");
		this.rowNumber = rowNumber;
		if(criterionId == null)
			throw new IllegalArgumentException("Criterion Id cannot be null");
		this.criterionId = criterionId;
	}

	/**
	 * Returns the row number of this mapping item
	 * @return Row number of the mapping item
	 */
	public int getRowNumber() {
		return rowNumber;
	}

	/**
	 * Returns the criterion Id of this mapping item
	 * @return Criterion Id of the mapping item
	 */
	public String getCriterionId() {
		return criterionId;
	}

	
	public String toString(){
		return "Row: " + this.rowNumber + " criteria: " + this.getCriterionId();
	}
	
	public boolean equals(Object o){
		MappingItem item = (MappingItem)o;
		if(this.getRowNumber() == item.getRowNumber() && this.getCriterionId().equals(item.getCriterionId()))
			return true;
		return false;
	}
	
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
}
