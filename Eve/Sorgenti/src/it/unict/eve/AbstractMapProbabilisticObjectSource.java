package it.unict.eve;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public abstract class AbstractMapProbabilisticObjectSource<O> implements ProbabilisticObjectSource<O>
{
	protected ConcurrentSkipListMap<Double, O> map;
	protected Random randomGenerator;
	
	public AbstractMapProbabilisticObjectSource()
	{
		this.map = new ConcurrentSkipListMap<Double, O>();
		this.randomGenerator = new Random();
	}

	public void put(Double key, O obj)
	{
		map.put(Math.abs(key), obj);
	}
	
	public Collection<O> getAll()
	{
		LinkedList<O> list = new LinkedList<O>();
		if( list.addAll(this.map.values()) != true ) throw new IllegalArgumentException("list didn't changed");
		return list;
	}
	
	public O getByProbability()
	{
		double r = getRandom();
		Map.Entry<Double, O> minore, maggiore;
		
			 if( (   minore = map.floorEntry(r)   ) != null ) return minore.getValue();
		else if( ( maggiore = map.ceilingEntry(r) ) != null ) return maggiore.getValue();		
		else throw new IllegalStateException("TreeMap is empty");
	}
	
	protected abstract double getRandom();
	
	public String toString()
	{
		String text = "";
		
		boolean first = true;
		for( Map.Entry<Double, O> entry : this.map.entrySet() )
			if(first) { text += "fit:" + entry.getKey() + "\n" + entry.getValue(); first = false; }
			else text += "\n" + "fit:" + entry.getKey() + "\n" + entry.getValue();
		
		return text;
	}
	
	synchronized public int size() { return this.map.size(); }
}