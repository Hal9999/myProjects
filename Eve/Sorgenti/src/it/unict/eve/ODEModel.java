package it.unict.eve;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class ODEModel extends Matrix
{
	private static final long serialVersionUID = -5656502746281721885L;
	protected Matrix ematrix = null;
	protected Matrix calculated;
	private static AtomicLong staticSerial = new AtomicLong(0);
	private long objectSerial;

	public ODEModel(double[][] coefficients)
	{
		super(coefficients);
		if( !this.isSquare() ) throw new IllegalArgumentException("coefficients must be squared");
		objectSerial = staticSerial.getAndIncrement();
	}
	
	public ODEModel(Matrix coefficients)
	{
		if( !coefficients.isSquare() ) throw new IllegalArgumentException("coefficients must be squared");
		this.matrix = coefficients.matrix;
		objectSerial = staticSerial.getAndIncrement();
	}
	
	public ODEModel(int N)
	{
		super(N);
		objectSerial = staticSerial.getAndIncrement();
	}
	
	public Matrix calculate(Matrix init, double Ts, int nSamples)
	{
		if( this.ematrix == null ) this.ematrix = getEMatrix();
		if( !init.isColumnMatrix() ) throw new IllegalArgumentException("init must be a column Matrix");
		if( this.matrix.length != init.matrix.length ) throw new IllegalArgumentException("init dimension not compatible");

		int N = this.matrix.length;
		Matrix res = new Matrix(N, nSamples).fill(0);
		
		//prima colonna di res: copia della colonna initVals
		for(int i=0; i<N; i++) res.matrix[i][0] = init.matrix[i][0];
		
		//calcolo le restanti nSamples-1 colonne di res
		for(int i=1; i<nSamples; i++)
		{
			//prodotto vettoriale tra coefficienti(this) e colonna precedente(res): 
			for(int j=0; j<N; j++) //per ogni elemento colonnare della colonna i di res
				for(int k=0; k<N; k++)
					res.matrix[j][i] += ematrix.matrix[j][k] * res.matrix[k][i-1];
					
			//produce per Ts e somma alla colonna precedente
			for(int j=0; j<N; j++)
				res.matrix[j][i] = res.matrix[j][i] * Ts + res.matrix[j][i-1];
		}
		this.calculated = res;
		return res;
	}
	
	public Matrix getLastCalculation()
	{
		if( calculated == null ) throw new IllegalStateException("calculate not executed yet");
		
		return calculated;
	}
	
	protected Matrix getEMatrix()
	{
		Matrix m = this.newLikeThis().fill(0);
		
		int C = m.matrix[0].length;
		int R = m.matrix.length;
		
		for(int i=0; i<R; i++)
			for(int j=0; j<C; j++)
				if(i <= j) m.matrix[i][j] = this.matrix[i][j];
				else m.matrix[i][j] = -this.matrix[j][i];
		return m;
	}
	
	public Matrix getModel()
	{
		if( this.ematrix == null ) this.ematrix = getEMatrix();
		return this.ematrix;
	}
	
	public String toString(String[] names)
	{
		if( this.ematrix == null ) this.ematrix = getEMatrix();
		if( ematrix.nRows() != names.length ) throw new IllegalArgumentException("order length and model size must be equal");
		
		String txt = "ODEModel n" + objectSerial + "\n";
		for(int i=0; i<names.length-1; i++)
			txt += names[i] + "\t\t" + ematrix.toString(i) + "\n";
		txt += names[names.length-1] + "\t\t" + ematrix.toString(names.length-1);
		return txt;
	}
        
        public void toFile(File file, String[] names) throws IOException
	{
            BufferedWriter writer = new BufferedWriter( new FileWriter(file) );

            if( this.ematrix == null ) this.ematrix = getEMatrix();
            if( ematrix.nRows() != names.length ) throw new IllegalArgumentException("order length and model size must be equal");
            
            for(int i=0; i<this.ematrix.matrix.length; i++)
            {
                writer.write(names[i] + "\t");
                for(int j=0; j<this.ematrix.matrix[0].length-1; j++)
                    writer.write( this.ematrix.matrix[i][j] + "\t" );
                writer.write( this.ematrix.matrix[i][this.ematrix.matrix[0].length-1] + "" );
                if( i != this.ematrix.matrix.length-1 ) writer.newLine();
            }
            
            writer.close();
        }
        
        public void toSifFile(File file, String[] names, double threshold) throws IOException
	{
            BufferedWriter writer = new BufferedWriter( new FileWriter(file) );

            if( this.ematrix == null ) this.ematrix = getEMatrix();
            if( ematrix.nRows() != names.length ) throw new IllegalArgumentException("order length and model size must be equal");

            double[][] a = this.ematrix.matrix;
            double t = Math.abs(threshold);
            
            for(int i=0; i<a.length; i++)
                for(int j=0; j<a[0].length; j++)
                    if( a[i][j] >= t )
                    {
                        writer.write("\"" + names[i] + "\"\tpp\t\"" + names[j] + "\"\t=\t" + a[i][j]);
                        writer.newLine();
                    }
            
            writer.close();
        }
	
	public boolean containsNotAReal()
	{
		for(int i=0; i<this.matrix.length; i++)
			for(int j=0; j<this.matrix[0].length; j++)
				if( Double.isNaN(this.matrix[i][j]) || Double.isInfinite(this.matrix[i][j]) ) return true;
		return false;
	}
	
	public String toString()
	{
		if( this.ematrix == null ) this.ematrix = getEMatrix();
		
		String txt = "ODEModel n°" + objectSerial + "\n";
		for(int i=0; i<ematrix.nRows()-1; i++)
			txt += ematrix.toString(i, 0.0001) + "\n";
		txt += ematrix.toString(ematrix.nRows()-1);
		return txt;
	}
        
        public static double[] errorsFromResiduals(Matrix residuals)
        {
            double[] res = new double[residuals.matrix[0].length];
            
            for(int i=0; i<res.length; i++)
            {
                res[i] = 0;
                for(int j=0; j<residuals.matrix.length; j++)
                    res[i] += residuals.matrix[j][i];
            }
            
            return res;
        }
}