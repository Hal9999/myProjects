package it.unict.eve;

public class SumEteroODEModelMutator extends EteroODEModelMutator
{
	public SumEteroODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x, double y)
	{
		return x + y;
	}
}