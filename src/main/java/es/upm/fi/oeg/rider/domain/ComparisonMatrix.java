package es.upm.fi.oeg.rider.domain;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;

import es.upm.fi.oeg.rider.domain.mapping.MatrixMapping;
import Jama.Matrix;

/**
 * This class represents comparison of alternatives or criteria with respect to some other alternative or criterion. <br/>
 * Each element in the matrix represents the comparison of an alternative/criterion related to 
 * a matrix row, and the alternative/criterion related to a matrix column. For example, element <i>{i,j}</i>
 * represents to which extent alternative/criterion in the <i>i-th</i> row is better to the alternative/criterion
 * in the <i>j-th</i> column.
 * 
 * @author Filip Radulovic
 *
 */
public class ComparisonMatrix extends Matrix implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1080955153149839683L;
	
	
	/**
	 * Id of the comparison matrix. This Id represents an alternative or criteria with respect
	 * to which the comparison is made
	 */
	private String id;
	
	
	/**
	 * A mapping of matrix elements (rows and columns) to alternatives or criteria compared in the comparison matrix
	 */
	private MatrixMapping mapping;
	
	
	protected ComparisonMatrix(int m, int n){
		super(m,n);
	}
	
	
	/**
	 * Construct an m-by-n matrix of zeros
	 * 
	 * @param id
	 * 			An Id of the Comparison matrix
	 * 
	 * @param mapping
	 * 			Matrix mapping
	 * 
	 * @param m
	 *            Number of rows
	 * @param n
	 *            Number of columns
	 */

	public ComparisonMatrix(String id, MatrixMapping mapping, int m, int n) {
		super(m,n);
		if(m!=n)
			throw new RuntimeException("Number of rows has to be the same as number of columns");
		if(mapping.getMappingItems().size() != m)
			throw new RuntimeException("Number of items in the matrix mapping must be the same as the size of the matrix");
		this.id = id;
		this.mapping = mapping;
	}

	
	/**
	 * Construct an m-by-n constant matrix
	 *  
	 * @param id
	 * 			An Id of the Comparison matrix
	 * 
	 * @param mapping
	 * 			Matrix mapping
	 * 
	 * @param m
	 *            Number of rows
	 * @param n
	 *            Number of columns
	 * @param s
	 *            Fill the matrix with this scalar value.
	 */	
	public ComparisonMatrix(String id, MatrixMapping mapping, int m, int n, double s) {
		super(m,n,s);
		if(m!=n)
			throw new RuntimeException("Number of rows has to be the same as number of columns");
		if(mapping.getMappingItems().size() != m)
			throw new RuntimeException("Number of items in the matrix mapping must be the same as the size of the matrix");
		this.id = id;
		this.mapping = mapping;
	}

	
	/**
	 * Construct a matrix from a 2-D array
	 *  
	 * @param id
	 * 			An Id of the Comparison matrix
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
	public ComparisonMatrix(String id, MatrixMapping mapping, double[][] A) {
		super(A);
		if(A.length!=A[0].length)
			throw new RuntimeException("Number of rows has to be the same as number of columns");
		if(mapping.getMappingItems().size() != A.length)
			throw new RuntimeException("Number of items in the matrix mapping must be the same as the size of the matrix");
		this.id = id;
		this.mapping = mapping;
	}

	
	/**
	 * Construct a matrix quickly without checking arguments
	 *  
	 * @param id
	 * 			An Id of the Comparison matrix
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
	public ComparisonMatrix(String id, MatrixMapping mapping, double[][] A, int m, int n) {
		super(A,m,n);
		if(m!=n)
			throw new RuntimeException("Number of rows has to be the same as number of columns");
		if(mapping.getMappingItems().size() != A.length)
			throw new RuntimeException("Number of items in the matrix mapping must be the same as the size of the matrix");
		this.id = id;
		this.mapping = mapping;
	}

	
	/**
	 * Construct a matrix from a one-dimensional packed array.
	 * Values from the array are filed in the matrix as columns
	 *  
	 * @param id
	 * 			An Id of the Comparison matrix
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
	public ComparisonMatrix(String id, MatrixMapping mapping, double vals[], int m) {
		super(vals,m);
		if(vals.length/m != m)
			throw new RuntimeException("Number of rows has to be the same as number of columns");
		if(mapping.getMappingItems().size() != m)
			throw new RuntimeException("Number of items in the matrix mapping must be the same as the size of the matrix");
		this.id = id;
		this.mapping = mapping;
	}
	
	
	/**
	 * Makes a deep copy of a comparison matrix while preserving the initial mapping
	 * 
	 */
	@Override
	public ComparisonMatrix copy(){
		ComparisonMatrix m = null;
		try {
			m = new ComparisonMatrix(this.id, (MatrixMapping) mapping.clone() ,getArray());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return m;
	}
	

	/**
	 * Returns a new matrix with normalized columns, i.e. a matrix in which the sum
	 * in every column is 1.
	 * 
	 * @return Normalized matrix
	 */
	public ComparisonMatrix normalizeColumns() {
		// create a new matrix object to fill
		double[][] mat = new double[getRowDimension()][getColumnDimension()];
		// iterate over every column
		for (int i = 0; i < getColumnDimension(); i++) {
			double columnSum = 0;
			// for a given column iterate over all rows to calculate column sum
			for (int j = 0; j < getRowDimension(); j++) {
				columnSum += get(j, i);
			}
			// iterate over all rows in a column to divide each cell value with
			// column sum
			for (int j = 0; j < getRowDimension(); j++) {
				if (columnSum != 0)
					mat[j][i] = get(j, i) / columnSum;
			}
		}
		ComparisonMatrix m = new ComparisonMatrix(this.id, this.mapping, mat);
		m.mapping = this.getMapping();
		return m;
	}
	
	
	/**
	 * Calculates weights (importance) for the elements
	 * in a pairwise comparison represented by the comparison matrix
	 * 
	 * @return A one column matrix which entries represent obtained weights
	 */
	public Matrix getWeights() {
		int rowDimension = getRowDimension();
		// first, the columns are normalized
		Matrix normalized = normalizeColumns();
		// the vector which represents weights
		double[] eigenVector = new double[rowDimension];
		for (int i = 0; i < normalized.getRowDimension(); i++) {
			double rowSum = 0;
			for (int j = 0; j < normalized.getColumnDimension(); j++) {
				rowSum += normalized.get(i, j);
			}
			eigenVector[i] = rowSum / rowDimension;
		}
		return new Matrix(eigenVector, rowDimension);
	}
	
	
	/**
	 * Checks if the matrix is stochastic, i.e. if the sum of every column is one
	 * 
	 * @return True if the matrix is stochastic, false otherwise
	 */
	public boolean isStochastic() {
		boolean bool = true;
		for (int i = 0; i < getColumnDimension(); i++) {
			double sum = 0;
			for (int j = 0; j < getRowDimension(); j++) {
				sum += get(j, i);
			}
			if ((sum <= 0.99) || (sum >= 1.01)) {
					System.err.println(i + 1 + ". column sum " + sum);
					bool = false;
			}
		}
		return bool;
	}
	
	
	/**
	 * Checks if the comparison represented by the comparison matrix is consistent
	 * 
	 * @return Returns <i>true</i> if the comparison is consistent, <i>false</i> otherwise
	 */
	public boolean isConsistent() {
		DecimalFormat format = new DecimalFormat();
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		String cr = format.format(getConsistencyIndex()
				/ getRandomConsistencyIndex());
		double d = Double.parseDouble(cr);
		if(d<0.1)
			return true;
		return false;
	}

	
	/**
	 * Returns the consistency index (CI) of the comparison matrix
	 * 
	 * @return Consistency index of the comparison matrix
	 */
	private double getConsistencyIndex() {
		double matrixSize = getRowDimension();
		return (getMaximumEigenValue() - matrixSize) / (matrixSize - 1);
	}

	
	/**
	 * Returns the random consistency index (RCI) of the comparison matrix
	 * 
	 * @return Random consistency index of the comparison matrix
	 */
	private double getRandomConsistencyIndex() {
		switch (getRowDimension()) {
		case 2:
			return 0;
		case 3:
			return 0.58;
		case 4:
			return 0.9;
		case 5:
			return 1.12;
		case 6:
			return 1.24;
		case 7:
			return 1.32;
		case 8:
			return 1.41;
		case 9:
			return 1.45;
		case 10:
			return 1.49;
		case 11:
			return 1.51;
		}
		return 0;
	}
	
	
	/**
	 * Returns the maximum eigenvalue of the matrix
	 * 
	 * @return Maximum eigenvalue of the matrix
	 */
	private double getMaximumEigenValue() {
		Matrix mat = getDiagonalEigenValueMatrix();
		double maxEigenValue = -10;
		for (int i = 0; i < mat.getRowDimension(); i++) {
			if (mat.get(i, i) > maxEigenValue)
				maxEigenValue = mat.get(i, i);
		}
		return maxEigenValue;
	}
	
	
	/**
	 * Returns the Diagonal eigenvalue Matrix
	 * 
	 * @return Diagonal eigenvalue matrix
	 */
	private Matrix getDiagonalEigenValueMatrix() {
		return eig().getD();
	}

	
	/**
	 * Returns the id of the comparison matrix
	 * 
	 * @return The id of the comparison matrix
	 */
	public String getId() {
		return id;
	}

		
	/**
	 * Returns the mapping of the comparison matrix
	 * 
	 * @return Comparison matrix mapping
	 */
	public MatrixMapping getMapping() {
		return mapping;
	}

	
	/**
	 * Inserts the value assigned to the comparison of an element in the specific row to an element
	 * in the specific column.
	 * 
	 * @param rowItemId
	 * 					An Id of the row element
	 * @param columnItemId
	 * 					An Id of the column element
	 * @param comparisonValue
	 * 					value of the comparison
	 * @return
	 * 			<i>True</i>, if the value is inserted successfully, <i>false</i> otherwise.
	 * 			If any of the provided Ids are not found in the matrix, the method will return <i>false</i>
	 */
	public boolean insertComparison(String rowItemId, String columnItemId, double comparisonValue){
		int rowPosition = this.mapping.getRowNumber(rowItemId);
		int columnPosition = this.mapping.getRowNumber(columnItemId);
		if(rowPosition == -1 || columnPosition == -1)
			return false;
		set(rowPosition, columnPosition, comparisonValue);
		return true;
	}
	
	
	/**
	 * Returns the weight of an element obtained in the comparison represented by this matrix
	 * 
	 * @param elementId
	 * 					An Id of the element 
	 * @return
	 * 			Weight of the element with the Id <i>elementId</i>
	 */
	public double getWeightForElement(String elementId){
		DecimalFormat format = new DecimalFormat();
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(3);
		format.setMinimumFractionDigits(3);
		return Double.parseDouble(format.format(getWeights().get(getMapping().getRowNumber(elementId), 0)));
	}

	
	/**
	 * Loads matrix from a text file.
	 * @param id An Id of the Comparison matrix
	 * @param mapping Matrix mapping
	 * @param file A text file to read the matrix from
	 * @param separator A character or a set of characters that separates elements in one row of the matrix
	 * @return Comparison matrix loaded from a text file
	 * @throws IOException
	 */
	public static ComparisonMatrix loadMatrixFromTextFile(String id, MatrixMapping mapping, 
			String file, String separator) throws IOException{
		ComparisonMatrix comparisonMatrix = null;
		BufferedReader fis = null;
		try {
			fis = new BufferedReader(new FileReader(new File(file)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Specified file is not found!");			
		}

		boolean end = false;
		int row = 0;
		while (!end) {
			String line = fis.readLine();
			if (line == null)
				end = true;
			else {
				if (comparisonMatrix == null) {
					String[] entries = line.split(separator);
					comparisonMatrix = new ComparisonMatrix(id, mapping, entries.length, entries.length);
					for (int i = 0; i < entries.length; i++) {
						comparisonMatrix.set(row, i, Double.parseDouble(entries[i]));
					}
					row++;
				} else {
					String[] entries = line.split(separator);
					for (int i = 0; i < entries.length; i++) {
						comparisonMatrix.set(row, i, Double.parseDouble(entries[i]));
					}
					row++;
				}
			}
		}
		fis.close();
		return comparisonMatrix;
	}
}
