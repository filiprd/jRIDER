package es.upm.fi.oeg.rider.domain;

import java.io.Serializable;
import java.util.LinkedList;
import Jama.Matrix;
import es.upm.fi.oeg.rider.domain.mapping.MappingItem;
import es.upm.fi.oeg.rider.domain.mapping.MatrixMapping;
import es.upm.fi.oeg.rider.domain.recommendation.Alternative;

/**
 * This class represents the matrix in the ANP, which can be supermatrix or
 * cluster matrix. It provides the basic functionalities needed for the ANP
 * method.
 * 
 * @author Filip Radulovic
 * 
 */
public class ANPMatrix extends Matrix implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1124882852782118717L;

	/**
	 * A mapping of matrix elements (rows and columns) to alternatives or
	 * criteria
	 */
	private MatrixMapping mapping;

	/**
	 * Construct an m-by-n matrix of zeros
	 * 
	 * @param mapping
	 *            Matrix mapping
	 * 
	 * @param m
	 *            Number of rows
	 * @param n
	 *            Number of columns
	 */

	public ANPMatrix(MatrixMapping mapping, int m, int n) {
		super(m, n);
		if (m != n)
			throw new RuntimeException(
					"Number of rows has to be the same as number of columns");
		if (mapping.getMappingItems().size() != m)
			throw new RuntimeException(
					"Number of items in the matrix mapping must be the same as the size of the matrix");
		this.mapping = mapping;
	}

	/**
	 * Construct an m-by-n constant ANPMatrix
	 * 
	 * @param mapping
	 *            Matrix mapping
	 * 
	 * @param m
	 *            Number of rows
	 * @param n
	 *            Number of columns
	 * @param s
	 *            Fill the matrix with this scalar value
	 */

	public ANPMatrix(MatrixMapping mapping, int m, int n, double s) {
		super(m, n, s);
		if (m != n)
			throw new RuntimeException(
					"Number of rows has to be the same as number of columns");
		if (mapping.getMappingItems().size() != m)
			throw new RuntimeException(
					"Number of items in the matrix mapping must be the same as the size of the matrix");
		this.mapping = mapping;
	}

	/**
	 * Construct an ANPMatrix from a 2-D array
	 * 
	 * @param mapping
	 *            Matrix mapping
	 * 
	 * @param A
	 *            Two-dimensional array of doubles
	 * @exception IllegalArgumentException
	 *                All rows must have the same length
	 * @see #constructWithCopy
	 */

	public ANPMatrix(MatrixMapping mapping, double[][] A) {
		super(A);
		if (A.length != A[0].length)
			throw new RuntimeException(
					"Number of rows has to be the same as number of columns");
		if (mapping.getMappingItems().size() != A.length)
			throw new RuntimeException(
					"Number of items in the matrix mapping must be the same as the size of the matrix");
		this.mapping = mapping;
	}

	/**
	 * Construct an ANPMatrix quickly without checking arguments.
	 * 
	 * @param mapping
	 *            Matrix mapping
	 * 
	 * @param A
	 *            Two-dimensional array of doubles
	 * @param m
	 *            Number of rows
	 * @param n
	 *            Number of columns
	 */

	public ANPMatrix(MatrixMapping mapping, double[][] A, int m, int n) {
		super(A, m, n);
		if (m != n)
			throw new RuntimeException(
					"Number of rows has to be the same as number of columns");
		if (mapping.getMappingItems().size() != A.length)
			throw new RuntimeException(
					"Number of items in the matrix mapping must be the same as the size of the matrix");
		this.mapping = mapping;
	}

	/**
	 * Construct an ANPMatrix from a one-dimensional packed array
	 * 
	 * @param mapping
	 *            Matrix mapping
	 * 
	 * @param vals
	 *            One-dimensional array of doubles, packed by columns (ala
	 *            Fortran)
	 * @param m
	 *            Number of rows
	 * @exception IllegalArgumentException
	 *                Array length must be a multiple of m
	 */

	public ANPMatrix(MatrixMapping mapping, double vals[], int m) {
		super(vals, m);
		if (vals.length / m != m)
			throw new RuntimeException(
					"Number of rows has to be the same as number of columns");
		if (mapping.getMappingItems().size() != m)
			throw new RuntimeException(
					"Number of items in the matrix mapping must be the same as the size of the matrix");
		this.mapping = mapping;
	}

	/**
	 * Returns the mapping of the ANPMatrix
	 * 
	 * @return ANPMatrix mapping
	 */
	public MatrixMapping getMapping() {
		return mapping;
	}

	/**
	 * Sets a submatrix
	 * 
	 * @param firstRowToFill
	 *            Initial row index
	 * @param lastRowToFill
	 *            Final row index
	 * @param columnToFill
	 *            Column index
	 * @param matrix
	 *            Matrix to insert
	 */
	public void setMatrixColumn(int firstRowToFill, int lastRowToFill,
			int columnToFill, Matrix matrix) {
		setMatrix(firstRowToFill, lastRowToFill, columnToFill, columnToFill,
				matrix);
	}

	/**
	 * Sets a submatrix
	 * 
	 * @param requirementsPositions
	 *            Array of row indices.
	 * @param columnIndex
	 *            Column index
	 * @param mat
	 *            Matrix to insert
	 */
	public void setMatrixColumn(int[] requirementsPositions, int columnIndex,
			Matrix mat) {
		int[] positions = { columnIndex };
		setMatrix(requirementsPositions, positions, mat);
	}

	/**
	 * Sets a submatrix
	 * 
	 * @param rowToFill
	 *            Row index
	 * @param firstColumnToFill
	 *            Initial column index
	 * @param LastColumnToFill
	 *            Final column index
	 * @param matrix
	 *            Matrix to insert
	 */
	public void setMatrixRow(int rowToFill, int firstColumnToFill,
			int LastColumnToFill, Matrix matrix) {
		setMatrix(rowToFill, rowToFill, firstColumnToFill, LastColumnToFill,
				matrix);
	}

	/**
	 * Fills the ANP matrix with weights obtained in the comparison represented
	 * by <i>comparisonMatrix</i>
	 * 
	 * @param comparisonMatrix
	 *            The comparison matrix that contains comparisons of desired
	 *            elements, from which the weights are obtained
	 * @return <i>True</i> if the comparison is successfully inserted,
	 *         <i>false</i> otherwise.
	 */
	public boolean insertComparison(ComparisonMatrix comparisonMatrix) {
		String controlCriterion = comparisonMatrix.getId();
		int columnIndex = getMapping().getRowNumber(controlCriterion);
		if (columnIndex == -1)
			return false;

		boolean atLeastOneComparisonInserted = false;
		for (int i = 0; i < comparisonMatrix.getRowDimension(); i++) {
			String criterionId = comparisonMatrix.getMapping()
					.getCriterionId(i);
			double weight = comparisonMatrix.getWeightForElement(criterionId);
			int rowIndex = getMapping().getRowNumber(criterionId);
			if (rowIndex != -1) {
				this.set(rowIndex, columnIndex, weight);
				atLeastOneComparisonInserted = true;
			}

		}
		return atLeastOneComparisonInserted;
	}

	protected ANPMatrix extend(LinkedList<Alternative> alternatives) {
		if (alternatives == null || alternatives.size() == 0)
			throw new RuntimeException(
					"The list of alternatives is empty or null");
		int elementsToAdd = alternatives.size();
		// creates new array for the new matrix that will contain the old one +
		// alternatives
		double[][] extended = new double[getRowDimension() + elementsToAdd][getColumnDimension()
				+ elementsToAdd];
		// fills the new array will values from the old matrix, leaving entries
		// for the alternatives to be zeros
		for (int i = 0; i < getRowDimension(); i++) {
			for (int j = 0; j < getColumnDimension(); j++) {
				extended[i][j] = getArray()[i][j];
			}
		}

		
		// maps alternatives
		LinkedList<MappingItem> extendedMatrixMappingItems = getMapping().getMappingItems();
		int toMap = getRowDimension();
		for (Alternative alternative : alternatives) {
			extendedMatrixMappingItems.add(
					new MappingItem(toMap++, alternative.getId()));
		}

		return new ANPMatrix(new MatrixMapping(getMapping().getId(), 
				extendedMatrixMappingItems), extended);
	}

}
