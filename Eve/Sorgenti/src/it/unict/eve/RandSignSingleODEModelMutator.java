package it.unict.eve;


public class RandSignSingleODEModelMutator extends SingleODEModelMutator
{
	private double mutationRate;
	
	public RandSignSingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source, double mutationRatio)
	{
		super(source);
		if( mutationRatio<0 || mutationRatio>1 ) throw new IllegalArgumentException("mutationRate must be 0<= x =<1");
		this.mutationRate = mutationRatio;
	}
	
	public RandSignSingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		this(source, 0.5);
	}

	@Override
	protected double op(double x)
	{
		if( mutationRate <= Math.random() ) return x;
		else return -x;
	}
}