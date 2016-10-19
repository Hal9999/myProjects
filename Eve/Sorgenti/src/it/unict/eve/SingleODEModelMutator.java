package it.unict.eve;

public abstract class SingleODEModelMutator extends ODEModelMutator
{
	public SingleODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}
	
	public ODEModel evolve()
	{
		ODEModel father = this.source.getByProbability();
		ODEModel child = new ODEModel( father.newLikeThis() );
		
		int C = father.nCols();
		int R = father.nRows();

		for(int i=0; i<R; i++)
			for(int j=0; j<C; j++)
				child.matrix[i][j] = op( father.matrix[i][j] );
		
		return child;
	}
	
	protected abstract double op(double x);
}