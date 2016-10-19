package it.unict.eve;

public class UniformProbabilisticObjectSource<O> extends AbstractMapProbabilisticObjectSource<O>
{
	protected double total = 0;
	
	public UniformProbabilisticObjectSource() {}
	
	public void put(Double key, O obj)
	{
		if( key <= 0 ) throw new IllegalArgumentException("key must be positive");
		map.put( total, obj);
		total += key;
	}
	
	public O getByProbability()
	{
		if( total == 0 ) throw new IllegalStateException("TreeMap is empty");
		else return map.lowerEntry(getRandom()).getValue();
	}

	@Override
	protected double getRandom() { return randomGenerator.nextDouble()*total; }
	
	public String toString()
	{
		String text = "UPOS size = " + map.size() + "\n";
		text += super.toString();
		return text;
	}
}