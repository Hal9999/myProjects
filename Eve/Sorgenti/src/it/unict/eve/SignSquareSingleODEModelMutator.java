package it.unict.eve;


public class SignSquareSingleODEModelMutator extends SingleODEModelMutator
{
	public SignSquareSingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}
	
	@Override
	protected double op(double x)
	{
		return Math.signum(x) * Math.pow(x, 2);
	}

}