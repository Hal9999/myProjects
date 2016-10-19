package it.unict.eve;

import it.unict.eve.Matrix.DistanceType;

import java.io.*;

public class MainPreElaboration
{
	public static void main(String[] args) throws IOException
	{
		if( args.length != 7 )
		{
			System.out.println("Wrong number of parameters:\nsampleMatrix Transpose(true/false) Names SelectionThreshold ClusteringRadius ClusteringDistanceType NumberOfPointOFInterpolation");
			System.out.println("0 - Max Error");
			System.out.println("1 - Mean Error");
			System.out.println("2 - Mean Square Error");
			System.out.println("3 - Root Mean Square Error");
			System.out.println("4 - Total Error");
			System.out.println("you used " + args.length + " parameters");
			System.exit(-1);
		}
		System.out.println("Start...");
		
		File samplesMatrixFile = new File (args[0]);
		boolean transpose = Boolean.parseBoolean(args[1]);
		File namesFile = new File(args[2]);
		double selectionThreshold = Double.parseDouble(args[3]);
		float clusteringRadius = Float.parseFloat(args[4]);
		int clusteringDistanceType = Integer.parseInt(args[5]);
		if( clusteringDistanceType < 0 || clusteringDistanceType > 4 ) throw new IllegalArgumentException("ClusteringDistanceType isn't correct");
		int NumberOfPointOFInterpolation = Integer.parseInt(args[6]);
		
		Matrix samplesMatrix = new Matrix( samplesMatrixFile );
		if( transpose == true ) samplesMatrix = samplesMatrix.transpose();
		String[] names = Experiment.readNamesFromFile( namesFile );
		
		if( names.length != samplesMatrix.nRows() ) throw new IllegalArgumentException("nomi e campioni non compatibili" + names.length  + " " + samplesMatrix.nRows());
		
		Profile[] profiles = new Profile[samplesMatrix.nRows()];
		for(int i=0; i<samplesMatrix.nRows(); i++)
		profiles[i] = new Profile(names[i], samplesMatrix.getRow(i), 1.0);
		
		Experiment experiment = new Experiment("Data", profiles);
		
		//inizio selezione per varianza minima
		experiment = experiment.selectByMinVariance(selectionThreshold);
		System.out.println(experiment.getSize());
		//fine selezione per varianza minima
		
		System.out.println("post selezione");

		//inizio clustering
			 if( clusteringDistanceType == 0) experiment.setCluDistType(DistanceType.MaximumError);
		else if( clusteringDistanceType == 0) experiment.setCluDistType(DistanceType.MeanError);
		else if( clusteringDistanceType == 0) experiment.setCluDistType(DistanceType.MeanSquareError);
		else if( clusteringDistanceType == 0) experiment.setCluDistType(DistanceType.RootMeanSquareError);
		else if( clusteringDistanceType == 0) experiment.setCluDistType(DistanceType.TotalError);
//		{
//			int i = 0;
//			for(Profile[] pp : experiment.clusterizeToProfiles(0.4F))
//			{
//				System.out.println("Cluster " + i++);
//				for(Profile p : pp) System.out.println(p);
//				System.out.println("fine cluster\n");
//			}
//		}
		experiment = experiment.clusterize(clusteringRadius);
		experiment.setName(experiment.getName() + ", Selected, Clustered");
		//fine clustering

		System.out.println(experiment.getSize());
		
		experiment = experiment.interpolate(NumberOfPointOFInterpolation);
		
		String[] profilesNames = experiment.getProfilesNames();
		Matrix matrix = experiment.toMatrix();
		
		matrix.toFile(new File(samplesMatrixFile.getAbsolutePath() + "_fromEVE.txt"));
		Experiment.writeNamesToFile(profilesNames, new File(namesFile.getAbsolutePath() + "_fromEVE.txt"));
		
		System.out.println("End.");
	}
}
