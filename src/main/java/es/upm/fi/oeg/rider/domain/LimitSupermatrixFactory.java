package es.upm.fi.oeg.rider.domain;

import java.util.LinkedList;

/**
 * This class provides methods for the convergence of a matrix
 * 
 * @author Filip Radulovic
 *
 */
public class LimitSupermatrixFactory {
	
	/**
	 * The weighted supermatrix to be used for ANP processing, i.e., calculation of the limit supermatrix
	 */
	private WeightedSupermatrix weightedMatrix;	
	

	/**
	 * Constructs a LimitSupermatrixFactory object to use for the calculation of the limit supermatrix
	 * 
	 * @param matrix The weighted supermatrix to process
	 */
	public LimitSupermatrixFactory(WeightedSupermatrix matrix) {
		if(!isWeightedSupermatrix(matrix))
			throw new RuntimeException("You have to provide a weighted supermatrix");
		this.weightedMatrix = matrix;
	}


	/**
	 * Returns a new matrix with normalized columns, i.e. a matrix in which the sum
	 * in every column equals 1.
	 * 
	 * @return Normalized matrix
	 */
	private WeightedSupermatrix normalizeColumns(WeightedSupermatrix w) {
		// create a new matrix object to fill
		double[][] mat = new double[w.getRowDimension()][w.getColumnDimension()];
		// iterate over every column
		for (int i = 0; i < w.getColumnDimension(); i++) {
			double columnSum = 0;
			// for a given column iterate over all rows to calculate column sum
			for (int j = 0; j < w.getRowDimension(); j++) {
				columnSum += w.get(j, i);
			}
			// iterate over all rows in a column to divide each cell value with
			// column sum
			for (int j = 0; j < w.getRowDimension(); j++) {
				if (columnSum != 0)
					mat[j][i] = w.get(j, i) / columnSum;
			}
		}
		WeightedSupermatrix m = new WeightedSupermatrix(weightedMatrix.getMapping(),mat);
		return m;
	}
	
	
	/**
	 * Calculates the limit supermatrix based on the calculus algorithm, in
	 * which the convergence is checked by normalizing every power of a
	 * supermatrix
	 * 
	 * @return Returns the limit supermatrix
	 */
	private LimitSupermatrix calculusTypeLimitMatrixI() {
		int[] noSinks = removeSinkIndexes(weightedMatrix);
		WeightedSupermatrix mat = weightedMatrix.copy();
		for (int i = 0; i < 10001; i++) {
			mat = mat.times(weightedMatrix);
			WeightedSupermatrix c = mat.copy();
			WeightedSupermatrix nc = normalizeAndConverge(c, noSinks);
			if (nc != null) {
				System.err.println("The matrix converged at " + i + ". step");
				return new LimitSupermatrix(nc.getMapping(),nc.getArray());
			}
		}
		System.err.println("The matrix did not fully converge!");
		return new LimitSupermatrix(mat.getMapping(),mat.getArray());
	}

	/**
	 * Normalizes the matrix and checks if it converges
	 * 
	 * @param mat
	 *            matrix to be normalized
	 * @param indexes
	 *            of zero columns to be excluded from checking
	 * @return
	 * 			Normalized and convergent matrix
	 */
	private WeightedSupermatrix normalizeAndConverge(WeightedSupermatrix mat, int[] columns) {
		WeightedSupermatrix reduced = mat.getMatrix(0, mat.getRowDimension() - 1, columns);
//		Matrix reduced = mat.getMatrix(columns, columns);
		if (normalizeColumns(reduced).isConvergent())
			return normalizeColumns(reduced);
		return null;
	}

	/**
	 * Returns the indexes of zero columns
	 * 
	 * @return An array of column indexes in which all values are zero
	 */
	private int[] removeSinkIndexes(WeightedSupermatrix mat) {
		LinkedList<Integer> zeroColumns = new LinkedList<Integer>();
		for (int i = 0; i < mat.getColumnDimension(); i++) {
			double sum = 0;
			for (int j = 0; j < mat.getRowDimension(); j++) {
				sum += mat.get(j, i);
			}
			if (sum == 0) {
				zeroColumns.add(i);
			}
		}

		int[] columns = new int[mat.getColumnDimension() - zeroColumns.size()];
		int counter = 0;
		for (int i = 0; i < mat.getColumnDimension(); i++) {
			if (zeroColumns.contains(i))
				continue;
			else
				columns[counter++] = i;
		}
		return columns;
	}

	/**
	 * Calculates the limit supermatrix based on the calculus algorithm, in
	 * which it searches for loops
	 * 
	 * @return The limit supermatrix
	 */
	private LimitSupermatrix calculusTypeLimitMatrixII() {
		WeightedSupermatrix mat = weightedMatrix.copy();
		WeightedSupermatrix start = mat.times(mat);
		WeightedSupermatrix temp;
		WeightedSupermatrix next = null;

		for (int i = 0; i < 1001; i++) {
			next = start.times(mat);

			if (start.compare(next)) {
				System.err.println("Loop found at " + i);
				return normalizeAndAverage(start, next);
			}
			temp = next;
			start = temp;
		}
		
		return calculusTypeLimitMatrixI();
	}
	
	/**
	 * Returns the limit supermatrix
	 * @return The limit supermatrix
	 */
	public LimitSupermatrix getLimitSuperMatrix(){
		return calculusTypeLimitMatrixII();
	}
	

	/**
	 * Normalizes and averages two matrices
	 * 
	 * @param start
	 *            matrix to be normalized
	 * @param next
	 *            matrix to be normalized
	 * @return
	 * 			Normalized and averaged matrix
	 */
	private LimitSupermatrix normalizeAndAverage(WeightedSupermatrix start, WeightedSupermatrix next) {
		WeightedSupermatrix mat1 = normalizeColumns(start);
		WeightedSupermatrix mat2 = normalizeColumns(next);
		LimitSupermatrix limit = new LimitSupermatrix(weightedMatrix.getMapping(),start.getArray());
		for (int i = 0; i < mat1.getColumnDimension(); i++) {
			for (int j = 0; j < mat1.getRowDimension(); j++) {
				limit.set(j, i, (mat1.get(j, i) + mat2.get(j, i)) / 2);
			}
		}
		return limit;
	}
	
	
	/**
	 * Checks if the WeightedSupermatrix is actually a weighted supermatrix
	 * @return Returns <i>true</i> if the matrix is weighted supermatrix, <i>false</i> otherwise
	 */
	private boolean isWeightedSupermatrix(WeightedSupermatrix matrix){
		boolean bool = true;
		for (int i = 0; i < matrix.getColumnDimension(); i++) {
			double sum = 0;
			for (int j = 0; j < matrix.getRowDimension(); j++) {
				sum += matrix.get(j, i);
			}
			if ((sum <= 0.99) || (sum >= 1.01)) {
				if(sum != 0){
					bool = false;
				}
			}
		}
		return bool;
	}

}
