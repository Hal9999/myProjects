package it.polito.bigdata.hadoop.exercise3;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

class MapperBigData extends Mapper<LongWritable, Text, Text, IntWritable>
{
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		String[] ids = value.toString().split(",");
		if(ids.length<=2) return;
		for(int i=1; i<ids.length; i++)
			for(int j=i+1; j<ids.length; j++)
				context.write(new Text( ids[i].compareTo(ids[j])<0 ? ids[i]+","+ids[j] : ids[j]+","+ids[i]), new IntWritable(1));
	}	
}
