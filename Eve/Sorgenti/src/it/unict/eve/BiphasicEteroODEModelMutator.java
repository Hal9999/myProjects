package it.unict.eve;

public class BiphasicEteroODEModelMutator extends EteroODEModelMutator
{
	public BiphasicEteroODEModelMutator(ProbabilisticObjectSource<ODEModel> source)
	{
		super(source);
	}

	public ODEModel evolve()
	{
		
		ODEModel father = this.source.getByProbability();
		ODEModel mother = this.source.getByProbability();
		ODEModel child = new ODEModel( father.newLikeThis() );
		
		int C = father.nCols();
		int R = father.nRows();

		int limit = Math.round((float)Math.ceil( R * C * Math.random() ));
		
		for(int i=0; i<R; i++)
			for(int j=0; j<C; j++)
				if( limit-->0 )
					child.matrix[i][j] = father.matrix[i][j];
				else
					child.matrix[i][j] = mother.matrix[i][j];
		
		return child;
	}
	
	protected double op(double x, double y)
	{
		return 0;
	}
}