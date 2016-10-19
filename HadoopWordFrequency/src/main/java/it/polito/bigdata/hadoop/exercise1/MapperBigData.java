package it.polito.bigdata.hadoop.exercise1;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

class MapperBigData extends Mapper<LongWritable, Text, Text, IntWritable>
{
	private static final Pattern stripPattern = Pattern.compile("[^A-Za-z0-9\\s]+");
	
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		String phrase = stripPattern.matcher(value.toString()).replaceAll(" ").toLowerCase();
		String[] words = phrase.toString().split("\\s+");

		for(String word : words)
			if(word.length() > 0) context.write(new Text(word.toLowerCase()), new IntWritable(1));
			else continue;
	}
}
