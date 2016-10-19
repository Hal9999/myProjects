package it.unict.eve;

import java.io.*;
import java.util.*;

public class Matrix implements Serializable
{
	public final static MatrixDistanceCalculator totalErrorMatrixDistanceCalculator = new TotalErrorMatrixDistanceCalculator();
	public final static MatrixDistanceCalculator meanErrorMatrixDistanceCalculator = new MeanErrorMatrixDistanceCalculator();
	public final static MatrixDistanceCalculator maximumErrorMatrixDistanceCalculator = new MaximumErrorMatrixDistanceCalculator();
	public final static MatrixDistanceCalculator meanSquareErrorMatrixDistanceCalculator = new MeanSquareErrorMatrixDistanceCalculator();
	public final static MatrixDistanceCalculator rootMeanSquareErrorMatrixDistanceCalculator = new RootMeanSquareErrorMatrixDistanceCalculator();
	
	public static interface MatrixDistanceCalculator
	{
		public double distance(Matrix a, Matrix b);
                public String toString();
	}
	public static class TotalErrorMatrixDistanceCalculator implements MatrixDistanceCalculator
	{
		@Override
		public double distance(Matrix a, Matrix b)
		{
			if( !a.isIsoDimensional(b) ) throw new IllegalArgumentException("matrices not isodimensional");
			
			double err = 0;
			for(int i=0; i<a.matrix.length; i++)
				for(int j=0; j<a.matrix[0].length; j++)
					err += Math.abs( a.matrix[i][j] - b.matrix[i][j] );
			return err;
		}
                
                @Override
                public String toString()
                {
                    return "Total Error";
                }
	}
	public static class MeanErrorMatrixDistanceCalculator extends TotalErrorMatrixDistanceCalculator implements MatrixDistanceCalculator
	{
		@Override
		public double distance(Matrix a, Matrix b)
		{
			return super.distance(a, b)/(a.matrix.length * b.matrix[0].length);
		}
                
                @Override
                public String toString()
                {
                    return "Mean Error";
                }
	}
	public static class MaximumErrorMatrixDistanceCalculator implements MatrixDistanceCalculator
	{
		@Override
		public double distance(Matrix a, Matrix b)
		{
			if( !a.isIsoDimensional(b) ) throw new IllegalArgumentException("matrices not isodimensional");
			
			double err = 0;
			double tmp;
			
			for(int i=0; i<a.matrix.length; i++)
				for(int j=0; j<a.matrix[0].length; j++)
					if( (tmp = Math.abs( a.matrix[i][j] - b.matrix[i][j] )) > err ) err = tmp;
			return err;
		}
                
                @Override
                public String toString()
                {
                    return "Maximum Error";
                }
	}
	public static class MeanSquareErrorMatrixDistanceCalculator implements MatrixDistanceCalculator
	{
		@Override
		public double distance(Matrix a, Matrix b)
		{
			if( !a.isIsoDimensional(b) ) throw new IllegalArgumentException("matrices not isodimensional");
			
			double err = 0;
			for(int i=0; i<a.matrix.length; i++)
				for(int j=0; j<a.matrix[0].length; j++)
					err += Math.pow(a.matrix[i][j] - b.matrix[i][j], 2);
			return err/(a.matrix.length * b.matrix[0].length);
		}
                
                @Override
                public String toString()
                {
                    return "MSE Error";
                }
	}
	public static class RootMeanSquareErrorMatrixDistanceCalculator extends MeanSquareErrorMatrixDistanceCalculator implements MatrixDistanceCalculator
	{
		@Override
		public double distance(Matrix a, Matrix b)
		{
			return Math.sqrt(super.distance(a, b));
		}
                
                @Override
                public String toString()
                {
                    return "RMSE Error";
                }
	}
	
	
	private static final long serialVersionUID = 9023999689048514295L;
	public static enum MatrixType{ColumnMatrix, RowMatrix};
	public static enum AppendMode{ColumnMode, RowMode};
	public static enum DoubleEqualityErrorAdmissibility{ NONE, THOUSANDTH, MILLIONTH, BILLIONTH };
	public static enum DistanceType{ TotalError, MaximumError, MeanError, MeanSquareError, RootMeanSquareError };

	protected double[][] matrix;
	protected double admittableError = 0.000001;
	protected DistanceType distanceType = DistanceType.TotalError;
	
	

	/**
	 * Crea una matrice impilando o affiancando un array di Matrici.
	 * La modalità di append è specificata tramite valore enum.
	 * @param marr array di Matrici da concatenare
	 * @param mode modalità di concatenazione
	 * @return una nuova matrice che ha origine da quelle dell'array per concatenazione
	 * @throws IllegalArgumentException se le matrici hanno dimensioni tali per cui 
	 */
	public static Matrix newFromMatrixArray(Matrix[] marr, AppendMode mode)
	{
		int len = 0;
			for(int i=0; i<marr.length; i++) len += marr[i].matrix.length; //len è il totale delle righe
			
		double[][] data = new double[len][];
		for(int i=0, k=0; i<marr.length; i++)
			for(int j=0; j<marr[i].matrix.length; j++)
				data[k++] = marr[i].matrix[j];
		//qui data contiene tutte le righe di tutte le matrici contenute in marr
				
		switch(mode)
		{
			case ColumnMode:
			{
				if( !isValidArray(data) ) throw new IllegalArgumentException("array is jagged"); //controlla che l'array di double non sia irregolare
				Matrix ret = new Matrix();
				ret.matrix = transpose(data); //traspone, copia i dati e si disaccoppia da quelli originali
				return ret;
			}
			case RowMode: { return new Matrix(data); } //controlla, copia i dati e si disaccoppia da quelli originali
		}
		throw new IllegalStateException("should not reach this point");
	}
	
	public Matrix(File file) throws IOException
	{
		LinkedList<double[]> doubleList = new LinkedList<double[]>();
		BufferedReader reader = new BufferedReader( new FileReader(file) );

		{
			String line; String[] ss; double[] dd;
			while( (line = reader.readLine()) != null )
			{
				if( line.isEmpty() ) continue;
				ss = line.split("\\s+");
				doubleList.add( dd=new double[ss.length] );
				for(int i=0; i<ss.length; i++)
					dd[i] = Double.parseDouble(ss[i]);
			}
			reader.close();
		}
		this.matrix = new double[doubleList.size()][];
		{
			int i = 0;
			for(double[] dd: doubleList)
				this.matrix[i++] = dd;
		}
		if( !isValidArray(this.matrix) ) throw new IllegalArgumentException("array is jagged");
	}
	
	public Matrix(double[][] matrix)
	{
		if( !isValidArray(matrix) ) throw new IllegalArgumentException("array is jagged"); //controlla che l'array di double non sia irregolare
		this.matrix = copyArray(matrix); //copia i dati e si disaccoppia da quelli originali
	}
	
	public Matrix(Matrix m)
	{
		if( m == null ) throw new NullPointerException("m cannot be null");
		
		this.matrix = copyArray( m.matrix );
	}
	
	public Matrix(double[] arr, MatrixType type)
	{
		//è certamente un array valido perchè è regolare (lato 1)
		double[][] matrix = new double[][]{arr};
		
		switch(type)
		{
			case RowMatrix:    { this.matrix = copyArray(matrix); } break;
			case ColumnMatrix: { this.matrix = transpose(matrix); } break;
		}
	}
	
	/**
	 * Crea una matrice RxC.
	 * @param R numero di righe
	 * @param C numero di colonne
	 */
	public Matrix(int R, int C)
	{
		this.matrix = new double[R][C];
	}
	
	/**
	 * Crea una matrice quadrata.
	 * @param N numero di righe e di colonne
	 */
	public Matrix(int N)
	{
		this(N, N);
	}
	
	protected Matrix()
	{}
	
	public void toFile(File file) throws IOException
	{
		BufferedWriter writer = new BufferedWriter( new FileWriter(file) );
		
		for(int i=0; i<this.matrix.length; i++)
		{
			for(int j=0; j<this.matrix[0].length-1; j++) writer.write( this.matrix[i][j] + "\t" );
			writer.write( this.matrix[i][this.matrix[0].length-1] + "" );
			if( i != this.matrix.length-1 ) writer.newLine();
		}
		writer.close();
	}
	
	public Matrix newLikeThis()
	{
		return new Matrix(this.matrix.length, this.matrix[0].length);
	}
	
	/**
	 * Attenzione! fill() modifica questo oggetto! 
	 * @param v
	 * @return
	 */
	public Matrix fill(double v)
	{
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				this.matrix[i][j] = v;
		return this;
	}
	
	/**
	 * Attenzione! fill() modifica questo oggetto!
	 * @param r
	 * @return
	 */
	public Matrix fill(Random r)
	{
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				this.matrix[i][j] = r.nextDouble() * 2 - 1;
		return this;
	}
	
	public int nRows()
	{
		return this.matrix.length;
	}

	public int nCols()
	{
		return this.matrix[0].length;
	}
	
	public double get(int i, int j)
	{
		return matrix[i][j];
	}
	
	public void set(int i, int j, double v)
	{
		this.matrix[i][j] = v;
	}
	
	public Matrix getColumnMatrix(int c)
	{
		Matrix column = new Matrix();
		column.matrix = new double[this.matrix.length][1];
		for(int i=0; i<this.matrix.length; i++)
			column.matrix[i][0] = this.matrix[i][c];
		return column;
	}
	
	public Matrix getRowMatrix(int r)
	{
		return new Matrix(this.matrix[r], MatrixType.RowMatrix);
	}
	
	public Matrix getColumnMatrix()
	{
		if( this.matrix[0].length == 1 ) return this.getColumnMatrix(0);
		else throw new IllegalArgumentException("cannot call this method on this object: matrix must be column type");
	}
	
	public Matrix getRowMatrix()
	{
		if( this.matrix.length == 1 ) return this.getRowMatrix(0);
		else throw new IllegalArgumentException("cannot call this method on this object: matrix must be row type");
	}
	
	public double[] getColumn(int c)
	{
		double[] col = new double[this.matrix.length];
		for(int i=0; i<this.matrix.length; i++)
			col[i] = this.matrix[i][c];
		return col;
	}
	
	public double[] getRow(int r)
	{
		return this.matrix[r].clone();
	}
	
	public double[] getColumn()
	{
		if( this.matrix[0].length == 1 ) return this.getColumn(0);
		else throw new IllegalArgumentException("cannot call this method on this object: matrix must be column type");
	}
	
	public double[] getRow()
	{
		if( this.matrix.length == 1 ) return this.getRow(0);
		else throw new IllegalArgumentException("cannot call this method on this object: matrix must be row type");
	}

	public double[][] getBackingArray()
	{
		return this.matrix;
	}
	
	public double[][] getBackingArrayCopy()
	{
		return copyArray(this.matrix);
	}

	public double maximum()
	{
		double M = this.matrix[0][0];
		
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				if( this.matrix[i][j] > M ) M = this.matrix[i][j];
		return M;
	}
	
	public double minimum()
	{
		double m = this.matrix[0][0];
		
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				if( this.matrix[i][j] < m ) m = this.matrix[i][j];	
		return m;
	}
	
	public Matrix transpose()
	{
		Matrix transposed = new Matrix(this.matrix[0].length, this.matrix.length);
		
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				transposed.matrix[j][i] = this.matrix[i][j];
		return transposed;
	}

	public Matrix abs()
	{
		Matrix abssed = newLikeThis();
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				abssed.matrix[i][j] = Math.abs( this.matrix[i][j] );
		
		return abssed;
	}
	
	public Matrix plus(Matrix o)
	{
		if( !this.isIsoDimensional(o) ) throw new IllegalArgumentException("Matrix dimensions are not compatible");
		
		Matrix summa = newLikeThis();
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				summa.matrix[i][j] = this.matrix[i][j] + o.matrix[i][j];
		return summa;
	}
	
	public Matrix plus(double v)
	{
		Matrix summa = newLikeThis();
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				summa.matrix[i][j] = this.matrix[i][j] + v;
		return summa;
	}
	
	public Matrix minus(Matrix o)
	{
		if( !this.isIsoDimensional(o) ) throw new IllegalArgumentException("Matrix dimensions are not compatible");
		
		Matrix diff = newLikeThis();
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				diff.matrix[i][j] = this.matrix[i][j] - o.matrix[i][j];
		return diff;
	}
	
	public Matrix minus(double v)
	{
		return plus(-v);
	}
	
	public Matrix scalarMultiplication(double x)
	{	
		Matrix prod = newLikeThis();
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				prod.matrix[i][j] = this.matrix[i][j] * x;
		return prod;
	}
	
	public Matrix scalarDivision(double x)
	{	
		return scalarMultiplication(1/x);
	}
	
	public Matrix multiply(Matrix o)
	{
		if( this.matrix[0].length != o.matrix.length ) throw new IllegalArgumentException("...");
		
		Matrix res = new Matrix(this.matrix.length, o.matrix[0].length).fill(0);
		for(int i=0; i<res.matrix.length; i++)
			for(int j=0; j<res.matrix[0].length; j++)
				for(int k=0; k<this.matrix[0].length; k++)
					res.matrix[i][j] += this.matrix[i][k]*o.matrix[k][j];
		return res;
	}
	
	public double totalSum()
	{
		double sum = 0;
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				sum += this.matrix[i][j];
		return sum;
	}
	
	public double mean()
	{
		return totalSum() / ( matrix.length * matrix[0].length );
	}
	
	public double columnMean(int c)
	{
		double sum = 0.0;
		for(int i=0; i<this.matrix.length; i++) sum += this.matrix[i][c];
		return sum / this.matrix.length;
	}
	
	public double rowMean(int r)
	{
		double sum = 0.0;
		for(int i=0; i<this.matrix[0].length; i++) sum += this.matrix[r][i];
		return sum / this.matrix[0].length;
	}
	
	public double variance()
	{
		double totalQErr = 0;
		double m = mean();
		
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				totalQErr += Math.pow(this.matrix[i][j] - m, 2);
		return totalQErr/(this.matrix[0].length * this.matrix.length);
	}
	
	public boolean isSquare()
	{
		if( this.matrix.length == this.matrix[0].length ) return true;
		else return false;
	}
	
	public boolean isRowMatrix()
	{
		return this.matrix.length == 1;
	}
	
	public boolean isColumnMatrix()
	{
		return this.matrix[0].length == 1;
	}

	public double getAdmittableError()
	{
		return admittableError;
	}

	public Matrix setAdmittableError(DoubleEqualityErrorAdmissibility admittableError)
	{
		switch(admittableError)
		{
			case NONE: 		 { this.admittableError = 0; } break;
			case THOUSANDTH: { this.admittableError = 0.001; } break;
			case MILLIONTH:  { this.admittableError = 0.000001; } break;
			case BILLIONTH:  { this.admittableError = 0.000000001; } break;
		}
		return this;
	}

	public Matrix setAdmittableError(double admittableError)
	{
		this.admittableError = Math.abs(admittableError);
		return this;
	}
	
	public Matrix setDistanceType(DistanceType dt)
	{
		this.distanceType = dt;
		
		return this;
	}

	public boolean equals(Matrix o)
	{
		if( !this.isIsoDimensional(o) ) return false;
		
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				if( Math.abs(this.matrix[i][j] - o.matrix[i][j]) > this.admittableError ) return false;
		
		return true;
	}

	public double distance(Matrix o)
	{
		return distance(o, this.distanceType);
	}
	
	public double distance(Matrix o, DistanceType type)
	{
		switch(type)
		{
			case TotalError: { return totalErrorMatrixDistanceCalculator.distance(this, o); }
			case MaximumError: { return maximumErrorMatrixDistanceCalculator.distance(this, o); }
			case MeanError: { return meanErrorMatrixDistanceCalculator.distance(this, o); }
			case MeanSquareError: { return meanSquareErrorMatrixDistanceCalculator.distance(this, o); }
			case RootMeanSquareError: { return rootMeanSquareErrorMatrixDistanceCalculator.distance(this, o); }
		}
		throw new IllegalStateException("should not reach this point");
	}
	
	public double distance(Matrix o, MatrixDistanceCalculator method)
	{
		return method.distance(this, o);
	}
	
	public String toString()
	{
		String text = "";
		for(int i=0; i<this.matrix.length; i++)
		{
			for(int j=0; j<this.matrix[0].length-1; j++) text += this.matrix[i][j] + "\t";
			text += this.matrix[i][this.matrix[0].length-1] + ( i==this.matrix.length-1 ? "" : "\n"); //se è l'ultima riga non manda a capo
		}
		return text;
	}
	
	public String toString(int i)
	{
		String text = "";
		for(int j=0; j<this.matrix[0].length-1; j++) text += this.matrix[i][j] + "\t";
		text += this.matrix[i][this.matrix[0].length-1];
		return text;
	}

	public String toString(int i, double threshold)
	{
		String text = "";
		for(int j=0; j<this.matrix[0].length-1; j++) text += (Math.abs(this.matrix[i][j]) >= threshold ? this.matrix[i][j] : 0) + "\t";
		text += (Math.abs(this.matrix[i][this.matrix[0].length-1]) >= threshold ? this.matrix[i][this.matrix[0].length-1] : 0);
		return text;
	}
	
	public boolean isIsoDimensional(Matrix o)
	{
		return ( this.matrix.length == o.matrix.length && this.matrix[0].length == o.matrix[0].length );
	}
	
	public boolean areEqualWithinLimit(double a, double b)
	{
		return Math.abs(a - b) <= Math.abs(this.admittableError);
	}
	
	public static boolean areEqualWithinLimit(double a, double b, double err)
	{
		return Math.abs(a - b) <= Math.abs(err);
	}
	
	private static boolean isValidArray(double[][] array)
	{
		int R = array.length;
		int C = array[0].length;
		
		for(int i=0; i<R; i++)
			if( array[i].length != C ) return false;
		
		return true;
	}
	
	private static double[][] transpose(double[][] array)
	{
		double[][] transposed = new double[array[0].length][array.length];
		
		for(int i=0; i<array.length; i++)
			for(int j=0; j<array[0].length; j++)
				transposed[j][i] = array[i][j];
		return transposed;
	}

	private static double[][] copyArray(double[][] array)
	{
		double[][] copied = new double[array.length][];
		for(int i=0; i<array.length; i++) copied[i] = array[i].clone();
		return copied;
	}
}