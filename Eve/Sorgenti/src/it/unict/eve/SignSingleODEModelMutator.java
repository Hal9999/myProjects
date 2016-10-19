package it.unict.eve;


public class SignSingleODEModelMutator extends SingleODEModelMutator
{
	public SignSingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x)
	{
		return -x;
	}
}