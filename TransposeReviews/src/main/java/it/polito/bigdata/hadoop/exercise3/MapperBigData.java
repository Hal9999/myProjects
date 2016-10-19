package it.polito.bigdata.hadoop.exercise3;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

class MapperBigData extends Mapper<LongWritable, Text, Text, Text>
{
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		String[] ids = value.toString().split(",");
		
		context.write(new Text(ids[2]), new Text(ids[1]));
	}	
}
