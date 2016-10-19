package it.unict.eve;


public class SquareSingleODEModelMutator extends SingleODEModelMutator
{
	public SquareSingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x)
	{
		return Math.pow(x, 2);
	}
}