package it.unict.eve;


public class RandMutationSingleODEModelMutator extends SingleODEModelMutator
{
	private double mutationRate;
	
	public RandMutationSingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source, double mutationRate)
	{
		super(source);
		if( mutationRate<0 || mutationRate>1 ) throw new IllegalArgumentException("mutationRate must be 0<= x =<1");
		this.mutationRate = mutationRate;
	}
	
	public RandMutationSingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		this(source, 0.5);
	}

	@Override
	protected double op(double x)
	{
		if( Math.random() <= mutationRate ) return  (x + (10.0 * Math.random()) - 5);
		else return x;
	}
}