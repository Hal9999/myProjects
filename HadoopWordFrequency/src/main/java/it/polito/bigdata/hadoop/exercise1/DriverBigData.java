package it.polito.bigdata.hadoop.exercise1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class DriverBigData extends Configured implements Tool
{
	@Override
	public int run(String[] args) throws Exception
	{
		int numberOfReducers = Integer.parseInt(args[0]);
		Path inputPath = new Path(args[1]);
		Path outputDir = new Path(args[2]);

		Configuration conf = this.getConf();

		Job job = Job.getInstance(conf);					// Define a new job

		job.setJobName("Exercise #1 - WordCounter");		// Assign a name to the job

		FileInputFormat.addInputPath(job, inputPath);		// Set path of the input file/folder (if it is a folder, the job reads all the files in the specified folder) for this job
		FileOutputFormat.setOutputPath(job, outputDir);		// Set path of the output folder for this job

		job.setJarByClass(DriverBigData.class);				// Specify the class of the Driver for this job
		job.setInputFormatClass(TextInputFormat.class);		// Set job input format
		job.setOutputFormatClass(TextOutputFormat.class);	// Set job output format
		
		job.setMapperClass(MapperBigData.class);			// Set map class
		job.setMapOutputKeyClass(Text.class);				// Set map output key and value classes
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setReducerClass(ReducerBigData.class);			// Set reduce class

		job.setOutputKeyClass(Text.class);					// Set reduce output key and value classes
		job.setOutputValueClass(IntWritable.class);

		job.setNumReduceTasks(numberOfReducers);			// Set number of reducers

		if(job.waitForCompletion(true) == true) return 0;	// Execute the job and wait for completion
		else return 1;
	}

	public static void main(String args[]) throws Exception
	{
		// Exploit the ToolRunner class to "configure" and run the Hadoop application
		int res = ToolRunner.run(new Configuration(), new DriverBigData(), args);

		System.exit(res);
	}
}