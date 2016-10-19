package it.unict.test;

import it.unict.eve.EvolutiveIsland.Parameters;
import it.unict.eve.*;

import org.junit.Before;
import org.junit.Test;

public class EvolutiveWorldTest
{
	private Experiment experiment;
	private EvolutiveIsland XEIsland, TEIsland, RMSEIsland, MxEIsland;
	
	private static class MixedErrorMatrixDistanceCalculator implements Matrix.MatrixDistanceCalculator
	{
		@Override
		public double distance(Matrix a, Matrix b)
		{
			return Matrix.maximumErrorMatrixDistanceCalculator.distance(a, b) +
				   Matrix.totalErrorMatrixDistanceCalculator.distance(a, b) +
				   Matrix.rootMeanSquareErrorMatrixDistanceCalculator.distance(a, b);
		}
	}
	
	@Before
	public void setUp() throws Exception
	{
		Profile[] dati = new Profile[5];
			dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		
		experiment = new Experiment("Esperimento K", dati);

		Environment XEEnvironment = new Environment( experiment, Matrix.maximumErrorMatrixDistanceCalculator );
		Environment TEEnvironment = new Environment( experiment, Matrix.totalErrorMatrixDistanceCalculator );
		Environment RMSEEnvironment = new Environment( experiment, Matrix.rootMeanSquareErrorMatrixDistanceCalculator );
		Matrix.MatrixDistanceCalculator mixedErrorMatrixDistanceCalculator = new MixedErrorMatrixDistanceCalculator();
		Environment MxEEnvironment = new Environment( experiment, mixedErrorMatrixDistanceCalculator );
		
		Parameters parameters = new EvolutiveIsland.Parameters();
		parameters.islandSize = 10000;
		parameters.generationSize = 1000;
		parameters.disasterPeriod = 100;
		
		XEIsland = new EvolutiveIsland(XEEnvironment, parameters);
		TEIsland = new EvolutiveIsland(TEEnvironment, parameters);
		RMSEIsland = new EvolutiveIsland(RMSEEnvironment, parameters);
		MxEIsland = new EvolutiveIsland(MxEEnvironment, parameters);
	}

	@Test
	public void test()
	{
		EvolutiveWorld world = new EvolutiveWorld();
		
		world.addIsland(XEIsland);
		world.addIsland(TEIsland);
		world.addIsland(RMSEIsland);
		world.addIsland(MxEIsland);
		
		world.go();
		
		long time = System.currentTimeMillis();
		for(int i=0; i<10; i++)
		{
			try{Thread.sleep(10000);} catch(InterruptedException e){e.printStackTrace(); System.exit(-1);}
			
			EvolutiveIsland.Solution solution = null;
			EvolutiveIsland.Statistics statistics = null;
			System.out.println("_________________World Statistics______________________");
			System.out.println(world.getStatistics());
			System.out.println("_________________XEIsland Statistics___________________");
			if( (statistics = XEIsland.getLastGenerationStatistics()) != null ) System.out.println(statistics);
			if( (solution = XEIsland.getLastBestSolution()) != null ) System.out.println(solution);
			System.out.println("_________________TEIsland Statistics___________________");
			if( (statistics = TEIsland.getLastGenerationStatistics()) != null ) System.out.println(statistics);
			if( (solution = TEIsland.getLastBestSolution()) != null ) System.out.println(solution);
			System.out.println("_________________RMSEIsland Statistics_________________");
			if( (statistics = RMSEIsland.getLastGenerationStatistics()) != null ) System.out.println(statistics);
			if( (solution = RMSEIsland.getLastBestSolution()) != null ) System.out.println(solution);
			System.out.println("_________________MxEIsland Statistics__________________");
			if( (statistics = MxEIsland.getLastGenerationStatistics()) != null ) System.out.println(statistics);
			if( (solution = MxEIsland.getLastBestSolution()) != null ) System.out.println(solution);
			System.out.println("_______________________________________________________");
				
			System.out.println("\npassed " + (System.currentTimeMillis() - time)/(float)1000 + " s");
		}
		
		System.out.println("stopping");
		try { world.stop(); } catch (InterruptedException e){e.printStackTrace(); System.exit(-1);}
		System.out.println("stopped");
	}
}
