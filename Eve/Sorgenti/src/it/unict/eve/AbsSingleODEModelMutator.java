package it.unict.eve;

public class AbsSingleODEModelMutator extends SingleODEModelMutator
{
	public AbsSingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x)
	{
		return Math.abs(x);
	}
}