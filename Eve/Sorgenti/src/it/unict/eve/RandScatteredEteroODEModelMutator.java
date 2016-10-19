package it.unict.eve;

public class RandScatteredEteroODEModelMutator extends EteroODEModelMutator
{
	public RandScatteredEteroODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x, double y)
	{
		if( Math.random() < 0.5) return x;
		else return y;
	}
}