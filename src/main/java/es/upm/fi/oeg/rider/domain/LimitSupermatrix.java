package es.upm.fi.oeg.rider.domain;

import java.text.DecimalFormat;

import Jama.Matrix;
import es.upm.fi.oeg.rider.domain.mapping.MatrixMapping;

/**
 * This class represents a limit supermatrix, i.e. a converged matrix which elements represent weights of the criteria,
 * and score of the alternatives obtained for a given weighted supermatrix
 * @author Filip Radulovic
 *
 */
public class LimitSupermatrix extends Matrix{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4693145442368313991L;
	
	/**
	 * A mapping of matrix elements (rows and columns) to alternatives or criteria
	 */
	private MatrixMapping mapping;
		
	
	protected LimitSupermatrix(MatrixMapping mapping, double[][] A) {		
		super(A);
		this.mapping = mapping;
	}
	
	
	/**
	 * Returns the importance of the criterion or score of the alternative in the limit supermatrix
	 * 
	 * @param elementId 
	 * 					An Id of the criterion or alternative 
	 * @return 
	 * 			Importance of the criterion or score of the alternative in the limit supermatrix
	 */
	public double getResult(String elementId){
		DecimalFormat format = new DecimalFormat();
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(3);
		format.setMinimumFractionDigits(3);
		int row = getMapping().getRowNumber(elementId);		
		for (int i = 0; i < getColumnDimension(); i++) {			
			if(get(row,i) != 0){				
				return Double.parseDouble(format.format(get(row,i)));
			}					
		}
		return 0;
	}
	
	
	/**
	 * Returns the mapping of the ANPMatrix
	 * 
	 * @return ANPMatrix mapping
	 */
	public MatrixMapping getMapping() {
		return mapping;
	}
}
