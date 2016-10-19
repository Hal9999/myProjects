package it.unict.eve;

public class TransposeSingleODEModelMutator extends SingleODEModelMutator
{
	public TransposeSingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}
	
	public ODEModel evolve()
	{
		ODEModel father = this.source.getByProbability();
		ODEModel child = new ODEModel( father.transpose() );

		return child;
	}
	
	@Override
	protected double op(double x)
	{
		return 0;
	}
}