package it.unict.eve;

public class NewSparseRandomODEModelMutator extends SingleODEModelMutator
{
	private double p = 0.1;
	
	public NewSparseRandomODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}
	
	public NewSparseRandomODEModelMutator(ProbabilisticObjectSource<ODEModel> source, double p)
	{
		super(source);
		if( 0 < p && p < 1 );
		else throw new IllegalArgumentException("p is a probability");
		this.p = p;
	}

	@Override
	protected double op(double x)
	{
		if( Math.random() < p) return Math.random();
		else return 0;
	}
}
