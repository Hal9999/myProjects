package it.unict.eve;

public class LimitedDivisionEteroODEModelMutator extends EteroODEModelMutator
{

	public LimitedDivisionEteroODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x, double y)
	{
		double q = 0;
		
		if( y==0 ) y = Math.random();
		
		q = x / y;
		
		if( -10000 < q && q < 10000 ) return q;
		else return Math.random();
	}
}
