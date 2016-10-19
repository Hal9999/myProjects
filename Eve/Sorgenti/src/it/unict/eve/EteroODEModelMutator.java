package it.unict.eve;

public abstract class EteroODEModelMutator extends ODEModelMutator
{
	public EteroODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}
	
	public ODEModel evolve()
	{
		ODEModel father = this.source.getByProbability();
		ODEModel mother = this.source.getByProbability();
		ODEModel child = new ODEModel( father.newLikeThis() ); //la matrice da newLikeThis viene integrata dell'ODEModel
		
		int C = father.nCols();
		int R = father.nRows();
		
		for(int i=0; i<R; i++)
			for(int j=0; j<C; j++)
				child.matrix[i][j] = op( father.matrix[i][j], mother.matrix[i][j] );
		
		return child;
	}
	
	protected abstract double op(double x, double y);
}