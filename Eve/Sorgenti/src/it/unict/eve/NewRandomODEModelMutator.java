package it.unict.eve;

public class NewRandomODEModelMutator extends SingleODEModelMutator
{
	public NewRandomODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x)
	{
		return Math.random()*10-5;
	}
}