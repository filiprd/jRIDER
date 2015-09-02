package es.upm.fi.oeg.rider.domain;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Properties;

import es.upm.fi.oeg.rider.domain.mapping.MatrixMapping;
import Jama.Matrix;

/**
 * This class represents the weighted supermatrix in the ANP. 
 * @author Filip Radulovic
 *
 */
public class WeightedSupermatrix extends Matrix implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -3462552665343575093L;
	
	/**
	 * A mapping of matrix elements (rows and columns) to alternatives or criteria
	 */
	private MatrixMapping mapping;
	
	/**
	 * Construct an m-by-n matrix of zeros
	 * 
	 * @param mapping
	 * 			Matrix mapping
	 * 
	 * @param m
	 *            Number of rows
	 * @param n
	 *            Number of columns
	 */

	public WeightedSupermatrix(MatrixMapping mapping, int m, int n) {		
		super(m,n);
		if(m!=n)
			throw new RuntimeException("Number of rows has to be the same as number of columns");
		this.mapping = mapping;
	}

	/**
	 * Construct an m-by-n constant WeightedSupermatrix
	 * 
	 * @param mapping
	 * 			Matrix mapping
	 * 
	 * @param m
	 *            Number of rows
	 * @param n
	 *            Number of columns
	 * @param s
	 *            Fill the matrix with this scalar value
	 */

	public WeightedSupermatrix(MatrixMapping mapping, int m, int n, double s) {
		super(m,n,s);
		if(m!=n)
			throw new RuntimeException("Number of rows has to be the same as number of columns");
		if(mapping.getMappingItems().size() != m)
			throw new RuntimeException("Number of items in the matrix mapping must be the same as the size of the matrix");
		this.mapping = mapping;
	}

	/**
	 * Construct an WeightedSupermatrix from a 2-D array
	 * 
	 * @param mapping
	 * 			Matrix mapping
	 * 
	 * @param A
	 *            Two-dimensional array of doubles
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 * @see #constructWithCopy
	 */

	public WeightedSupermatrix(MatrixMapping mapping, double[][] A) {
		super(A);
		if(A.length!=A[0].length)
			throw new RuntimeException("Number of rows has to be the same as number of columns");
		if(mapping.getMappingItems().size() != A.length)
			throw new RuntimeException("Number of items in the matrix mapping must be the same as the size of the matrix");
		this.mapping = mapping;
	}

	/**
	 * Construct an WeightedSupermatrix quickly without checking arguments.
	 * 
	 * @param mapping
	 * 			Matrix mapping
	 * 
	 * @param A
	 *            Two-dimensional array of doubles
	 * @param m
	 *            Number of rows
	 * @param n
	 *            Number of columns
	 */

	public WeightedSupermatrix(MatrixMapping mapping, double[][] A, int m, int n) {
		super(A,m,n);
		if(m!=n)
			throw new RuntimeException("Number of rows has to be the same as number of columns");
		if(mapping.getMappingItems().size() != A.length)
			throw new RuntimeException("Number of items in the matrix mapping must be the same as the size of the matrix");
		this.mapping = mapping;
	}

	/**
	 * Construct an WeightedSupermatrix from a one-dimensional packed array
	 * 
	 * @param mapping
	 * 			Matrix mapping
	 * 
	 * @param vals
	 *            One-dimensional array of doubles, packed by columns (ala
	 *            Fortran)
	 * @param m
	 *            Number of rows
	 * @exception IllegalArgumentException
	 *                Array length must be a multiple of m
	 */

	public WeightedSupermatrix(MatrixMapping mapping, double vals[], int m) {
		super(vals,m);
		if(vals.length/m != m)
			throw new RuntimeException("Number of rows has to be the same as number of columns");
		if(mapping.getMappingItems().size() != m)
			throw new RuntimeException("Number of items in the matrix mapping must be the same as the size of the matrix");
		this.mapping = mapping;
	}
	
	
	/**
	 * Construct a weighted supermatrix by weighting the supermatrix with the corresponding clustermatrix
	 * 
	 * @param supermatrix
	 * 					Supermatrix to be weighted
	 * @param clusterMatrix
	 * 					Cluster matrix
	 * @param networkStructure
	 * 					Mapping of the elements in the supermatrix to the elements in the cluster matrix. The <i>key</i> of each property
	 * 				represents an Id of an element in the supermatrix, while the <i>value</i> of the <i>key</i> is an Id of an element
	 * 				in the cluster matrix.
	 */
	public WeightedSupermatrix(ANPMatrix supermatrix, ANPMatrix clusterMatrix, Properties networkStructure){
		super(supermatrix.getRowDimension(), supermatrix.getColumnDimension(), 0.0);
		this.mapping = supermatrix.getMapping();
		for (int i = 0; i < getRowDimension(); i++) {
			String rowElementId = supermatrix.getMapping().getCriterionId(i);
			for (int j = 0; j < getColumnDimension(); j++) {
				if(supermatrix.get(i,j) != 0){
					String columnElementId = supermatrix.getMapping().getCriterionId(j);
					set(i, j, supermatrix.get(i, j) * getClusterMultiplier(clusterMatrix, 
							networkStructure.getProperty(rowElementId), networkStructure.getProperty(columnElementId)));
				}
			}
		}
	}
	
	
	/**
	 * Returns the element in the cluster matrix form the row and column that correspond to specific row an column Ids
	 * 
	 * @param clusterMatrix
	 * 					The cluster matrix to extract an element from
	 * @param rowElementId
	 * 					The Id of the element in the row
	 * @param columnElementId
	 * 					The Id of the element in the column
	 * @return
	 * 					The element from the cluster matrix
	 */
	private double getClusterMultiplier(ANPMatrix clusterMatrix, String rowElementId, String columnElementId){
		return clusterMatrix.get(clusterMatrix.getMapping().getRowNumber(rowElementId), 
				clusterMatrix.getMapping().getRowNumber(columnElementId));
	}
	
	
	/**
	 * Returns the mapping of the WeightedSupermatrix
	 * 
	 * @return WeightedSupermatrix mapping
	 */
	public MatrixMapping getMapping() {
		return mapping;
	}
	
	
	/**
	 * Makes a deep copy of a matrix while preserving the initial mapping
	 * 
	 */
	@Override
	public WeightedSupermatrix copy(){
		WeightedSupermatrix m = null;
		try {
			m = new WeightedSupermatrix((MatrixMapping) mapping.clone(), getArray());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return m;
	}
	
	
	/**
	 * Linear algebraic matrix multiplication, A * B. This method preserves the initial mapping
	 *  
	 */
	@Override
	public WeightedSupermatrix times(Matrix B){
		WeightedSupermatrix m = new WeightedSupermatrix(this.mapping, super.times(B).getArray());
		return m;
	}
	
//	/**
//	 * Returns the WeightedSupermatrix put to the power of <i>n</i>. If the matrix converges before the <i>n-th</i> power,
//	 * the convergent matrix is returned
//	 * 
//	 * @param n The power to raise the matrix on
//	 * @return Convergent matrix, or the ANP matrix on <i>n-th</i> power
//	 */
//	protected WeightedSupermatrix calculateMatrixPower(int n) {
//		WeightedSupermatrix mat = copy();
//		for (int i = 0; i < n - 1; i++) {
//			mat = mat.times(this);
//			if (mat.isConvergent()) {
//				System.err.println("The matrix converged at " + i + ". step");
//				return mat;
//			}
//		}
//		return mat;
//	}
	
	/**
	 * Checks if the matrix is convergent, i.e. if all columns are identical.
	 * This method uses 3 decimals to compare values in the matrix
	 * @return Returns <i>true</i> if the matrix is convergent, <i>false</i> otherwise
	 */
	public boolean isConvergent() {
		return isConvergent(3);
	}
	
	/**
	 * Checks if the matrix is convergent, i.e. if all columns are identical
	 * 
	 * @param numberOfDecimals The number of decimal to check the equality of matrix elements
	 * @return Returns <i>true</i> if the matrix is convergent, <i>false</i> otherwise
	 */
	public boolean isConvergent(int numberOfDecimals){
		for (int i = 0; i < getRowDimension(); i++) {
			for (int j = 0; j < getColumnDimension() - 1; j++) {
				DecimalFormat format = new DecimalFormat();
				format.setMinimumIntegerDigits(1);
				format.setMaximumFractionDigits(numberOfDecimals);
				format.setMinimumFractionDigits(numberOfDecimals);
				double d1 = get(i, j);
				double d2 = get(i, j + 1);
				if (!format.format(d1).equalsIgnoreCase(format.format(d2)))
					if(d1 != 0 && d2 != 0)
						return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Checks if the matrix is stochastic, i.e. if the sum of every column is one
	 * 
	 * @return Returns <i>true</i> if the matrix is stochastic, <i>false</i> otherwise
	 */
	public boolean isStochastic() {
		boolean bool = true;
		for (int i = 0; i < getColumnDimension(); i++) {
			double sum = 0;
			for (int j = 0; j < getRowDimension(); j++) {
				sum += get(j, i);
			}
			if ((sum <= 0.99) || (sum >= 1.01)) {
				if(sum != 0){
					System.err.println(i + 1 + ". column sum " + sum);
					bool = false;
				}
				System.err.println(i + 1 + ". column sum " + sum);
			}
		}
		return bool;
	}
	
	
	/**
	 * Compares the elements of this WeightedSupermatrix to the elements in a specified matrix
	 * 
	 * @param matrix The matrix to compare this matrix against
	 * @return Returns <i>true</i> if all the corresponding elements in both matrices are equal, <i>false</i> otherwise
	 */
	public boolean compare(Matrix matrix) {
		if (this.getRowDimension() != matrix.getRowDimension())
			return false;
		if (this.getColumnDimension() != matrix.getColumnDimension())
			return false;

		DecimalFormat format = new DecimalFormat();
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(5);
		format.setMinimumFractionDigits(5);
		for (int i = 0; i < getColumnDimension(); i++) {
			for (int j = 0; j < getRowDimension(); j++) {
				if (!format.format(get(j, i)).equalsIgnoreCase(
						format.format(matrix.get(j, i))))
					return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Get a submatrix.
	 * 
	 * @param i
	 *            Initial row index
	 * @param j
	 *            Final row index
	 * @param col
	 *            Array of column indices.
	 * @return A(i0:i1,c(:))
	 * @exception ArrayIndexOutOfBoundsException
	 *                Submatrix indices
	 */
	public WeightedSupermatrix getMatrix(int i, int j, int[] col){
		WeightedSupermatrix m = new WeightedSupermatrix(this.mapping, super.getMatrix(i, j, col).getArray());
		return m;
		
	}
}
