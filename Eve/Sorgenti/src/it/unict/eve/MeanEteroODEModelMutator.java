package it.unict.eve;

public class MeanEteroODEModelMutator extends EteroODEModelMutator
{

	public MeanEteroODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x, double y)
	{
		return (x + y) / 2;
	}

}
