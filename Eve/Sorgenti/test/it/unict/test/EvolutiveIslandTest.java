package it.unict.test;

import it.unict.eve.*;
import it.unict.eve.EvolutiveIsland.Solution;
import it.unict.eve.EvolutiveIsland.Statistics;

import org.junit.Before;
import org.junit.Test;

public class EvolutiveIslandTest
{
	private Environment environment;

	@Before
	public void setUp() throws Exception
	{
		Profile[] dati = new Profile[5];
			dati[0] = new Profile("Alfa" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[1] = new Profile("Beta" , new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[4] = new Profile("Iota" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
		
		this.environment = new Environment( new Experiment("Esperimento K", dati), Matrix.maximumErrorMatrixDistanceCalculator );
	}

	@Test
	public void test()
	{
		System.out.println("---------- test >>>>>>>>>>");
		
		EvolutiveIsland island = new EvolutiveIsland(environment, null);
		
		island.go();
		
		long time = System.currentTimeMillis();
		for(int i=0; i<10; i++)
		{
			try{Thread.sleep(10000);} catch(InterruptedException e){e.printStackTrace(); System.exit(-1);}
			
			Solution solution = island.getLastBestSolution();
			if( solution != null ) System.out.println( solution );
			else System.out.println("First complete generation not run yet");
			
			Statistics statistics = island.getLastGenerationStatistics();
			if( statistics != null )
			{
				System.out.println( statistics );
				System.out.println();
			}
			else System.out.println("First complete generation not run yet");
			
			System.out.println("passed " + (System.currentTimeMillis() - time)/(float)1000 + " s");
		}
		
		System.out.println("stopping");
		try { island.stop(); } catch (InterruptedException e){e.printStackTrace(); System.exit(-1);}
		System.out.println("stopped");
		
		System.out.println("<<<<<<<<<< test ----------");
	}

}
