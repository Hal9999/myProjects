package it.unict.eve;

public interface ProbabilisticObjectSource<O>
{
	public void put(Double key, O obj);
	public O getByProbability();
}