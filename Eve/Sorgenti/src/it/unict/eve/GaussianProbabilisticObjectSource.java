package it.unict.eve;

import java.util.Map.Entry;

public class GaussianProbabilisticObjectSource<O> extends AbstractMapProbabilisticObjectSource<O>
{
	protected double mean, variance;
	
	public GaussianProbabilisticObjectSource() { this(0, 1); }
	
	public GaussianProbabilisticObjectSource(double m, double v)
	{
		if( m < 0 || v <= 0 ) throw new IllegalArgumentException("mean and variance must be positive numbers");
		this.mean = m;
		this.variance = v;
	}
	
	public void setVariance(double v)
	{
		if( v <= 0 ) throw new IllegalArgumentException("variance must be positive number");
		this.variance = v;
	}
	
	public void setMean(double m)
	{
		if( m < 0 ) throw new IllegalArgumentException("mean must be a non negative number");
		this.mean = m;
	}
	
	public void setAdaptiveVariance(double range, int sigmas, double fraction)
	{
		if( range <= 0 || sigmas <= 0 || fraction <=0 )
			throw new IllegalArgumentException("range, sigmas and fraction must be positive numbers");
		this.setVariance( Math.pow( (range/fraction)/sigmas, 2) );
	}
	
	//generatore di gaussiane assolute: attenzione alle parentesi
	protected double getRandom() { return Math.abs(randomGenerator.nextGaussian()*Math.sqrt(variance)) + mean; }

	public int retainFirsts(int n)
	{
		int nDel = this.map.size() - n; //n° di elementi da eliminare
		
		int deleted = 0;
		for(int i=0; i<nDel; i++, deleted++)
			this.map.pollLastEntry();
		
		return deleted;
	}
	
	public Entry<Double, O> lastEntry() { return this.map.lastEntry(); }
	
	public Entry<Double, O> firstEntry() { return this.map.firstEntry(); }

	public double getMean() { return mean; }

	public double getVariance() { return variance; }
	
	public String toString()
	{
		String text = "GPOS size = " + map.size() + ", mean = " + this.mean + ", variance = " + this.variance + "\n";
		text += super.toString();
		return text;
	}
}