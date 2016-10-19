package it.polito.bigdata.hadoop.exercise3;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

class ReducerBigData extends Reducer<Text, Text, Text, Text>
{
	@Override
	protected void reduce(Text user, Iterable<Text> products, Context context) throws IOException, InterruptedException
	{
		StringBuffer concatenation= new StringBuffer();
		
		for(Text product : products)
			concatenation.append(product.toString()).append(",");
		
		concatenation.deleteCharAt(concatenation.length()-1);
		
		context.write(user, new Text(concatenation.toString()));
	}
}