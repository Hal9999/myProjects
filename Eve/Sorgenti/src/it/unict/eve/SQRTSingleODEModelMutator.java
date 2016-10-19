package it.unict.eve;


public class SQRTSingleODEModelMutator extends SingleODEModelMutator
{
	public SQRTSingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	@Override
	protected double op(double x)
	{
		if( x < 0 ) return - Math.sqrt( Math.abs(x) );
		else return Math.sqrt( Math.abs(x) );
	}
}