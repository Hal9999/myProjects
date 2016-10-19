package it.unict.eve;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class EvolutiveIsland implements Runnable
{
	private class Generator implements Callable<Integer>
	{
		private final int nChildren;
		private final ProbabilisticObjectSource<ODEModelMutator> generators;
		private final GaussianProbabilisticObjectSource<ODEModel> world;
		private final Environment environment;
		private final double maxWorstFitness;
		
		public Generator( final int nChildren,
						  final ProbabilisticObjectSource<ODEModelMutator> generators,
						  final GaussianProbabilisticObjectSource<ODEModel> world,
						  final Environment environment,
						  final double maxWorstFitness)
		{
			this.nChildren = nChildren;
			this.generators = generators;
			this.world = world;
			this.environment = environment;
			this.maxWorstFitness = maxWorstFitness;
		}
		@Override
		public Integer call() throws Exception
		{
			ODEModel child;
			double fitness;
			for(int i=0; i<nChildren; i++)
			{
				child = generators.getByProbability().evolve();					//generiamo il prossimo bimbo
				fitness = environment.live(child);								//lo facciamo vivere
				if( fitness <= maxWorstFitness ) world.put( fitness, child );	//e lo aggiungiamo al mondo, ma se la fitness è troppo scarsa muore: non lo inseriamo nel mondo :,(
			}
			return null;
		}
	}
	
	public static class Parameters
	{
		public int threadsNumber = 2;
		public int islandSize = 100000;
		public int generationSize = 30000;
		public double maxWorstFitness = 1000000000;
		public boolean disasterStrategy = true;
		public int disasterPeriod = 1000;
		public int disasterSurvivorsNumber = 100;
		public boolean migrationStrategy = true;
		public int emigratesNumber = 1000;
		public double migrationEventProbability = 0.005;
	}
	
	//ambiente dell'isola
	private Environment environment;
	//l'isola ha una popolazione
	private GaussianProbabilisticObjectSource<ODEModel> island = new GaussianProbabilisticObjectSource<ODEModel>(0, 100);
	//impostiamo il dominio degli operatori di mutazione e di incrocio
	private ProbabilisticObjectSource<ODEModelMutator> mutators = new UniformProbabilisticObjectSource<ODEModelMutator>();
	//threadpool per i generatori
	private ExecutorService executor = Executors.newCachedThreadPool();
	//numero di generatori
	private int threadsNumber = 2;
	//generatori
	private Collection<Callable<Integer>> generators = new LinkedList<Callable<Integer>>();
	//thread principale di questa isola
	private Thread islandThread;
	//controllo del thread principale
	private AtomicBoolean stop = new AtomicBoolean(false);
	//dimensione della popolazione isolana
	private int islandMaxSize = 100000;
	//dimensione di ogni nuova generazione
	private int generationSize = 30000;
	//peggiore fitness ammessa
	private double maxWorstFitness = 1000000000;
	//interruttore per la disaster strategy
	private boolean disasterStrategy = true;
	//cadenza generazione dei disastri
	private int disasterPeriod = 1000;
	//numero di sopravvissuti ai disastri
	private int disasterSurvivorsNumber = 100;
	//contatore di eventi disastro
	private int disasters = 0;
	
	//interruttore per la emigration strategy
	private boolean migrationStrategy = false;
	//numero di emigrati per evento migratorio
	private int emigratesNumber = 1000;
	//probabilità di emigrazione per generazione
	private double migrationEventProbability = 0.005;
	//puntatore atomico per l'array che contiene gli immigrati
	private AtomicReference<Collection<ODEModel>> immigrantsBoat = new AtomicReference<Collection<ODEModel>>(null);
	//mondo evolutivo
	private EvolutiveWorld world = null;
	//contatore di eventi migrazione
	private int migrations = 0;
	
	public static class Solution
	{
		public int generationNumber;
		public double fitness;
		public ODEModel model;
		public String[] names;
		public Matrix calculated;
		public Matrix residuals;
		
		public String toString()
		{
			String txt;
			txt  = "Best fitness = " + this.fitness + "\n";
			txt += this.model.toString(/*this.names*/) + "\n";
			txt += "Calculation\n" + this.calculated + "\n";
			txt += "Residuals\n" + this.residuals;
			return txt;
		}
	}
	
	public static class Statistics
	{
		public int generationNumber;
		public double bestFitness;
		public double worstFitness;
		public double islandMean;
		public double islandVariance;
		public int islandSize;
		public int deleted;
		public double totalTime;
		public int totalIndividuals;
		public double meanSpeed;
		public double generationTime;
		public int generationSize;
		public double generationSpeed;
		public int disasters;
		public int migrations;
		public int threads;
		
		public String toString()
		{
			String txt;
			txt  = "generationN\t= " + this.generationNumber + "\n";
			txt += "bestFitness\t= " + this.bestFitness + "\n";
			txt += "worstFitness\t= " + this.worstFitness + "\n";
			txt += "islandMean\t= " + this.islandMean + "\n";
			txt += "islandVariance\t= " + this.islandVariance + "\n";
			txt += "islandSize\t= " + this.islandSize + "\n";
			txt += "deleted\t\t= " + this.deleted + "\n";
			txt += "totalTime\t= " + this.totalTime + "\n";
			txt += "totIndividuals\t= " + this.totalIndividuals + "\n";
			txt += "meanSpeed\t= " + this.meanSpeed + "\n";
			txt += "generationTime\t= " + this.generationTime + "\n";
			txt += "generationSize\t= " + this.generationSize + "\n";
			txt += "generationSpeed\t= " + this.generationSpeed + "\n";
			txt += "disasters\t= " + this.disasters + "\n";
			txt += "migrations\t= " + this.migrations + "\n";
			txt += "threads\t\t= " + this.threads;
			return txt;
		}
	}

	//variabiali per tracciare lo stato dell'esecuzione
	private AtomicReference<Solution> bestSolution = new AtomicReference<EvolutiveIsland.Solution>(null);
	private AtomicReference<Statistics> statistics = new AtomicReference<EvolutiveIsland.Statistics>(null);
	
	
	public EvolutiveIsland( Environment environment,
							Parameters parameters)
	{
		if( parameters != null )
		{
			this.threadsNumber = parameters.threadsNumber;
			this.islandMaxSize = parameters.islandSize;
			this.generationSize = parameters.generationSize;
			this.maxWorstFitness = parameters.maxWorstFitness;
			this.disasterStrategy = parameters.disasterStrategy;
			this.disasterPeriod = parameters.disasterPeriod;
			this.disasterSurvivorsNumber = parameters.disasterSurvivorsNumber;
			this.migrationStrategy = parameters.migrationStrategy;
			this.emigratesNumber = parameters.emigratesNumber;
			this.migrationEventProbability = parameters.migrationEventProbability;
		}
		
		this.environment = environment;

		//creiamo i vari operatori di mutazione con le relative probabilità
		mutators.put( 2.0, new BiphasicEteroODEModelMutator(island));
		mutators.put( 1.0, new DiffEteroODEModelMutator(island));
		mutators.put( 1.0, new LimitedDivisionEteroODEModelMutator(island));
		mutators.put( 2.0, new MeanEteroODEModelMutator(island));
		mutators.put( 1.0, new ProductEteroODEModelMutator(island));
		mutators.put( 2.0, new RandScatteredEteroODEModelMutator(island));
		mutators.put( 1.0, new SumEteroODEModelMutator(island));
		mutators.put( 1.0, new AbsSingleODEModelMutator(island));
		mutators.put( 2.0, new NewRandomODEModelMutator(island));
		mutators.put( 1.0, new RandMutationSingleODEModelMutator(island, 0.01));
		mutators.put( 1.0, new RandSignSingleODEModelMutator(island));
		mutators.put( 1.0, new SignSingleODEModelMutator(island));
		mutators.put( 1.0, new SignSquareSingleODEModelMutator(island));
		mutators.put( 1.0, new SQRTSingleODEModelMutator(island));
		mutators.put( 1.0, new SquareSingleODEModelMutator(island));
		mutators.put( 1.0, new TransposeSingleODEModelMutator(island));
		mutators.put( 2.0, new NewSparseRandomODEModelMutator(island, 0.05));
		
		for(int i=0; i<threadsNumber; i++)
			generators.add( new Generator( generationSize/threadsNumber,
										   mutators,
										   island,
										   environment,
										   maxWorstFitness));
		
		this.islandThread = new Thread(this);
	}
	
	public void go()
	{
		this.islandThread.start();
	}
	
	public void stop() throws InterruptedException
	{
		this.stop.set(true);
		this.islandThread.join();
	}
	
	public Solution getLastBestSolution()
	{
		return this.bestSolution.get();
	}
	
	public Statistics getLastGenerationStatistics()
	{
		return this.statistics.get();
	}
	
	public int getThreadsNumber()
	{
		return this.threadsNumber;
	}
	
	public void sendEmigrants(Collection<ODEModel> emigrants)
	{
		this.immigrantsBoat.set(emigrants);
	}
	
	public void setWorld(EvolutiveWorld world)
	{
		this.world = world;
		//this.migrationStrategy = true;
	}
	
	public void run()
	{
		double bestfit;
		long startTime = System.currentTimeMillis();

		{
			ODEModel adam = environment.randomODEModel();
				island.put( bestfit = environment.live(adam) , adam);
		}
		
		for( int G = 1; ; G++ )
		{
			long generationStartTime = System.currentTimeMillis();
			
			//qui qualcuno vorrebbe (probabilità) emigrare
			{
				if( world != null && migrationStrategy == true && Math.random() < migrationEventProbability )
				{
					LinkedList<ODEModel> emigrates = new LinkedList<ODEModel>();
					for(int i=0; i<emigratesNumber; i++)
						emigrates.add(island.getByProbability());
					world.migrate(emigrates, this);
					this.migrations++;
				}
			}
			//fine dell'emigrazione
			
			//qui inseriamo gli immigrati, se ce ne sono
			{
				Collection<ODEModel> immigrants = this.immigrantsBoat.getAndSet(null);
				if( immigrants != null )
				{
					for(ODEModel immigrant: immigrants)
					{
						double f = environment.live(immigrant);					//lo facciamo vivere
						if( f <= maxWorstFitness ) island.put( f, immigrant );	//e lo aggiungo all'isola
					}
					//cerco di dare una chance anche agli immigrati tramite la varianza (l'intera frazione in un sigma)
					island.setMean( island.firstEntry().getKey() );
					island.setAdaptiveVariance(island.lastEntry().getKey() - island.firstEntry().getKey(), 1, 1);
				}
			}
			//fine inserimento immigrati
																		 try {
			for(Future<?> f : executor.invokeAll(generators)) f.get(); } catch (Exception e) { e.printStackTrace(); System.exit(-1); }
			
			int deleted = island.retainFirsts(islandMaxSize);
			Entry<Double, ODEModel> first = island.firstEntry();
			Entry<Double, ODEModel> last = island.lastEntry();
			
			island.setMean( first.getKey() );
			island.setAdaptiveVariance(last.getKey() - first.getKey(), 1, 2);
			
			/*inizio codice per le statistiche*/ 
			{
				Statistics newStatistics = new Statistics();
				newStatistics.generationNumber = G;
				newStatistics.bestFitness = first.getKey();
				newStatistics.worstFitness = last.getKey();
				newStatistics.islandMean = island.getMean();
				newStatistics.islandVariance = island.getVariance();
				newStatistics.islandSize = island.size();
				newStatistics.deleted = deleted;
				newStatistics.totalTime = (System.currentTimeMillis() - startTime)/1000;
				newStatistics.totalIndividuals = G * this.generationSize;
				newStatistics.meanSpeed = newStatistics.totalIndividuals / newStatistics.totalTime;
				newStatistics.generationTime = (System.currentTimeMillis() - generationStartTime)/1000D;
				newStatistics.generationSize = this.generationSize;
				newStatistics.generationSpeed = newStatistics.generationSize / newStatistics.generationTime;
				newStatistics.disasters = this.disasters;
				newStatistics.migrations = this.migrations;
				newStatistics.threads = this.threadsNumber;
				this.statistics.set(newStatistics);
				if( first.getKey() < bestfit )
				{
					bestfit = first.getKey();
					Solution newBestSolution = new Solution();
					newBestSolution.generationNumber = G;
					newBestSolution.fitness = first.getKey();
					newBestSolution.model = first.getValue();
					newBestSolution.names = this.environment.getNames();
					newBestSolution.calculated = newBestSolution.model.getLastCalculation();
					newBestSolution.residuals = environment.getResiduals(newBestSolution.model);
					this.bestSolution.set(newBestSolution);
				}
			}
			/*fine codice per le statistiche*/ 

			if( this.stop.get() == true ) break;
			
			if( disasterStrategy == true && G % disasterPeriod == 0 )
			{
				island.retainFirsts(disasterSurvivorsNumber);
				this.disasters++;
				if( world != null && migrationStrategy == true )
				{
					Collection<ODEModel> islandPopulation = island.getAll();
					world.migrate(islandPopulation, this);
					this.migrations++;
				}
			}
		}
	}
}