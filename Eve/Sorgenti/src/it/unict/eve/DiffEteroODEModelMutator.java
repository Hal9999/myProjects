package it.unict.eve;


public class DiffEteroODEModelMutator extends EteroODEModelMutator
{

	public DiffEteroODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x, double y)
	{
		return x - y;
	}
}