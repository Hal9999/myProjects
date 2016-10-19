package it.unict.eve;

public abstract class ODEModelMutator implements MutatorMethod<ODEModel>
{
	protected ProbabilisticObjectSource<ODEModel> source;
	
	public ODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		if(source == null) throw new NullPointerException("source cannot be null");
		
		this.source = source;
	}

	public abstract ODEModel evolve();
}