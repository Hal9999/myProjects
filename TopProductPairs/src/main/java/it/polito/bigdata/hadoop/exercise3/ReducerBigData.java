package it.polito.bigdata.hadoop.exercise3;

import java.io.IOException;
import java.util.TreeSet;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

class ReducerBigData extends Reducer<Text, IntWritable, Text, IntWritable>
{
	private class Record implements Comparable<Record>
	{
		public String key;
		public int count;
		
		public Record(String key, int count){ this.key = key; this.count = count; }
		
		@Override
		public int compareTo(Record other) { return this.count!=other.count ? other.count-this.count : this.key.compareTo(other.key); }
	}
	
	private TreeSet<Record> topK;
	private int topKDimension;
	private int countThreshold;
	
	@Override
	protected void setup(Reducer<Text,IntWritable,Text,IntWritable>.Context context) throws IOException ,InterruptedException
	{
		topK = new TreeSet<Record>();
		topKDimension = context.getConfiguration().getInt("topKlimit", 100);
		countThreshold = context.getConfiguration().getInt("countThreshold", 1);
	};
	
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
	{
		int total = 0;
		for(IntWritable value : values) total += value.get();
		
		if(total<countThreshold) return;
		
		topK.add(new Record(key.toString(), total));
		if(topK.size()>topKDimension) topK.pollLast();
	}
	
	@Override
	protected void cleanup(Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException
	{
		super.cleanup(context);
		for (Record record : topK)
			context.write(new Text(record.key), new IntWritable(record.count));
	}
}