package it.polito.bigdata.hadoop.exercise2;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

class MapperBigData extends Mapper<LongWritable, Text, Text, NullWritable>
{
	String startFilter;
	
	@Override
	protected void setup(Mapper<LongWritable, Text, Text, NullWritable>.Context context) throws IOException, InterruptedException
	{
		super.cleanup(context);
		startFilter = context.getConfiguration().get("filterString");
	}
	
	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
	{
		if(value.toString().startsWith(startFilter))
			context.write(value, NullWritable.get());
	}	
}
