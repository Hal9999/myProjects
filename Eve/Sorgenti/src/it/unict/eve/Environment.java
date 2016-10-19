package it.unict.eve;

import it.unict.eve.Matrix.MatrixDistanceCalculator;

import java.io.Serializable;
import java.util.Random;

public class Environment implements Serializable
{
	private static final long serialVersionUID = -827316964631595565L;
	protected Matrix sperimentalData;
	protected double Ts;
	protected Matrix init;
	protected int nSamples;
	private MatrixDistanceCalculator distanceCalculator;
	private String[] names;
	
	public Environment(Experiment experiment, MatrixDistanceCalculator distanceCalculator)
	{
		if( experiment == null ) throw new NullPointerException("sperimentalData cannot be null");
		
		this.sperimentalData = experiment.toMatrix();
		this.Ts = experiment.getSamplingTime();
		this.init = sperimentalData.getColumnMatrix(0);
		this.nSamples = sperimentalData.nCols();
		this.distanceCalculator = distanceCalculator;
		this.names = experiment.getProfilesNames();
	}
	
	/**
	 * @deprecated
	 * @param sperimentalData
	 * @param Ts
	 * @param distanceCalculator
	 */
	public Environment(Matrix sperimentalData, double Ts, MatrixDistanceCalculator distanceCalculator)
	{
		if( sperimentalData == null ) throw new NullPointerException("sperimentalData cannot be null");
		if( Ts <= 0 ) throw new IllegalArgumentException("Ts must be positive!");
		
		this.sperimentalData = sperimentalData;
		this.Ts = Ts;
		this.init = sperimentalData.getColumnMatrix(0);
		this.nSamples = sperimentalData.nCols();
		this.distanceCalculator = distanceCalculator;
	}
	
	public double live(ODEModel individual)
	{
		if( individual.containsNotAReal() ) return 1000000000;
		else return sperimentalData.distance( individual.calculate(init, Ts, nSamples), this.distanceCalculator );
	}
	
	public void setDistanceCalculator(MatrixDistanceCalculator distanceCalculator)
	{
		this.distanceCalculator = distanceCalculator;
	}
	
	public ODEModel randomODEModel()
	{
		return new ODEModel( new Matrix(init.nRows()).fill(new Random()) );
	}
	
	public Matrix getResiduals(ODEModel model)
	{
		return model.getLastCalculation().minus(sperimentalData).abs();
	}
	
	public String[] getNames()
	{
		return this.names;
	}
}