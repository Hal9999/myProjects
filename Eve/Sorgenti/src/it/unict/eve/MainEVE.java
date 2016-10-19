package it.unict.eve;

import it.unict.eve.EvolutiveIsland.Parameters;
import java.io.File;
import java.io.IOException;

public class MainEVE
{
	public static class MixedErrorMatrixDistanceCalculator implements Matrix.MatrixDistanceCalculator
	{
		@Override
		public double distance(Matrix a, Matrix b)
		{
			return (Matrix.maximumErrorMatrixDistanceCalculator.distance(a, b) +
                                Matrix.meanErrorMatrixDistanceCalculator.distance(a, b) +
                                Matrix.rootMeanSquareErrorMatrixDistanceCalculator.distance(a, b));
		}
                
                @Override
                public String toString()
                {
                    return "Mixed Error";
                }
	}
	
	public static void main(String[] args) throws IOException
	{
		if( args.length < 7 )
		{
			System.out.println("Wrong number of parameters: sampleMatrix Transpose(true/false) Names SamplingTime SecondsToRun IslandSize GenerationSize");
			System.exit(-1);
		}
		
		File samplesMatrixFile = new File (args[0]);
		boolean transpose = Boolean.parseBoolean(args[1]);
		File namesFile = new File(args[2]);
		Double samplingTime = Double.valueOf(args[3]);
		int secondsToRun = Integer.parseInt(args[4]);
		int IslandSize = Integer.parseInt(args[5]);
		int GenerationSize = Integer.parseInt(args[6]);

		Matrix samplesMatrix = new Matrix( samplesMatrixFile );
		if( transpose == true ) samplesMatrix = samplesMatrix.transpose();
		String[] names = Experiment.readNamesFromFile( namesFile );
		
		if( names.length != samplesMatrix.nRows() ) throw new IllegalArgumentException("nomi e campioni non compatibili");
		
		Profile[] profiles = new Profile[samplesMatrix.nRows()];
			for(int i=0; i<samplesMatrix.nRows(); i++)
				profiles[i] = new Profile(names[i], samplesMatrix.getRow(i), samplingTime);
		
		System.out.println("Start...");

		Experiment experiment = new Experiment("Experiment", profiles);
		
		//System.out.println(experiment);
		
//		EVO
		System.out.println("EVE is starting...");
		Environment XEEnvironment = new Environment( experiment, Matrix.maximumErrorMatrixDistanceCalculator );
		Environment TEEnvironment = new Environment( experiment, Matrix.totalErrorMatrixDistanceCalculator );
		Environment RMSEEnvironment = new Environment( experiment, Matrix.rootMeanSquareErrorMatrixDistanceCalculator );
		Matrix.MatrixDistanceCalculator mixedErrorMatrixDistanceCalculator = new MixedErrorMatrixDistanceCalculator();
		Environment MxEEnvironment = new Environment( experiment, mixedErrorMatrixDistanceCalculator );
		
		Parameters parameters = new EvolutiveIsland.Parameters();
		parameters.islandSize = IslandSize;
		parameters.generationSize = GenerationSize;
		parameters.disasterPeriod = 100;
		parameters.disasterSurvivorsNumber = 10;
                parameters.migrationStrategy = true;
		
		EvolutiveIsland XEIsland, TEIsland, RMSEIsland, MxEIsland;
		XEIsland = new EvolutiveIsland(XEEnvironment, parameters);
		TEIsland = new EvolutiveIsland(TEEnvironment, parameters);
		RMSEIsland = new EvolutiveIsland(RMSEEnvironment, parameters);
		MxEIsland = new EvolutiveIsland(MxEEnvironment, parameters);
		
		EvolutiveWorld world = new EvolutiveWorld();
		
		world.addIsland(XEIsland);
		world.addIsland(TEIsland);
		world.addIsland(RMSEIsland);
		world.addIsland(MxEIsland);
		
		world.go();
		System.out.println("EVE has started...");
		
		long time = System.currentTimeMillis();
		for(int i=0; i<secondsToRun/10; i++)
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
		
		System.out.println("Eve is stopping...");
		try { world.stop(); } catch (InterruptedException e){e.printStackTrace(); System.exit(-1);}
		System.out.println("Eve has stopped.");
//		EVO
		
		System.out.println("End.");
		System.exit(0);
	}
}
