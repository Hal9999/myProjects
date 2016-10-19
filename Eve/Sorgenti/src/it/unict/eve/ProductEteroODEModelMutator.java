package it.unict.eve;

public class ProductEteroODEModelMutator extends EteroODEModelMutator
{
	public ProductEteroODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x, double y)
	{
		return x * y;
	}
}