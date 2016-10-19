package it.unict.eve;

import java.util.*;

public class EvolutiveWorld
{
	public static class Statistics
	{
		public int islandsN = 0;
		public int worldSize = 0;
		public int passedGenerations = 0;
		public int deleted = 0;
		public double totalTime = 0;
		public int totalIndividuals = 0;
		public double meanSpeed = 0;
		public int disasters = 0;
		public int migrations = 0;
		public int threads = 0;
		
		public String toString()
		{
			String txt;
			txt  = "islandsN\t= " + this.islandsN + "\n";
			txt += "worldSize\t= " + this.worldSize + "\n";
			txt += "doneGenerations\t= " + this.passedGenerations + "\n";
			txt += "deleted\t\t= " + this.deleted + "\n";
			txt += "totalTime\t= " + this.totalTime + "\n";
			txt += "totIndividuals\t= " + this.totalIndividuals + "\n";
			txt += "meanSpeed\t= " + this.meanSpeed + "\n";
			txt += "disasters\t= " + this.disasters + "\n";
			txt += "migrations\t= " + this.migrations + "\n";
			txt += "threads\t\t= " + this.threads;
			return txt;
		}
	}
	
	private LinkedList<EvolutiveIsland> world;
	private long startTime = 0;
	
	public Statistics getStatistics()
	{
		LinkedList<EvolutiveIsland.Statistics> islandsStatistics = new LinkedList<EvolutiveIsland.Statistics>();
		for(EvolutiveIsland island : world)
			islandsStatistics.add(island.getLastGenerationStatistics());
		
		Statistics worldStatistics = new Statistics();
		for(EvolutiveIsland.Statistics islandStatistics : islandsStatistics)
		{
			if( islandStatistics != null )
			{
				worldStatistics.worldSize += islandStatistics.islandSize;
				worldStatistics.passedGenerations += islandStatistics.generationNumber;
				worldStatistics.deleted += islandStatistics.deleted;
				worldStatistics.totalIndividuals += islandStatistics.totalIndividuals;
				worldStatistics.disasters += islandStatistics.disasters;
				worldStatistics.migrations += islandStatistics.migrations;
				worldStatistics.threads += islandStatistics.threads;
			}
		}
		worldStatistics.islandsN = this.world.size();
		worldStatistics.totalTime = (System.currentTimeMillis() - startTime)/1000D;
		worldStatistics.meanSpeed = worldStatistics.totalIndividuals / worldStatistics.totalTime;
		
		return worldStatistics;
	}
	
	public EvolutiveWorld(Collection<EvolutiveIsland> islands)
	{
		this();
		for(EvolutiveIsland island : islands)
			this.world.add(island);
	}
	
	public EvolutiveWorld()
	{
		world = new LinkedList<EvolutiveIsland>();
	}
	
	public void addIsland(EvolutiveIsland island)
	{
		this.world.add(island);
		island.setWorld(this);
	}
	
	public void migrate(Collection<ODEModel> emigrants, EvolutiveIsland source)
	{
		for(EvolutiveIsland island : world)
			if( island != source ) island.sendEmigrants(emigrants);
	}
	
	public int totalThreadsNumber()
	{
		int tot = 0;
		for(EvolutiveIsland island : world) tot += island.getThreadsNumber();
		return tot;
	}
	
	public void go()
	{
		for(EvolutiveIsland island : world) island.go();
		startTime = System.currentTimeMillis();
	}
	
	public void stop() throws InterruptedException
	{
		for(EvolutiveIsland island : world) island.stop();
	}
}