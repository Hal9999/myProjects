/*******************************************************************************
 * Copyright (C) 2015  ORO e ISMB
 * Questo e' il main del programma di ottimizzazione VRPTW
 * Il programma prende in input un file csv con i clienti ed un csv di configurazione (deposito e veicoli) e restituisce in output il file delle route ed un file sintetico di dati dei viaggi.
 * L'algoritmo ultizzato e' un Large Neighborhood
 ******************************************************************************/

package main;

import java.util.Collection;

import jsprit.analysis.toolbox.GraphStreamViewer;
import jsprit.analysis.toolbox.GraphStreamViewer.Label;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.io.VehicleRoutingAlgorithms;
import jsprit.core.algorithm.selector.SelectBest;
import jsprit.core.algorithm.termination.TimeTermination;
import jsprit.core.analysis.SolutionAnalyser;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.instance.reader.SolomonReader;
import jsprit.util.*;
import main.OROoptions.CONSTANTS;
import main.OROoptions.PARAMS;

public class Main {


	public static void main(String[] args) {
		int number=0;
		// Some preparation - create output folder
		Examples.createOutputFolder();
		// Read input parameters
		OROoptions options = new OROoptions(args);
		try {
			OROutils.write2((String)options.get(PARAMS.INSTANCE), (String)options.get(CONSTANTS.OUTPUT));
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		for(int r=0; r<(int)options.get(CONSTANTS.REPETITION); r++) {
			// Time tracking
			long startTime = System.currentTimeMillis();
			// Create a vrp problem builder
			VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
			// A solomonReader reads solomon-instance files, and stores the required information in the builder.
			new SolomonReader(vrpBuilder,0).read("input/" + options.get(PARAMS.INSTANCE));
			final VehicleRoutingProblem vrp = vrpBuilder.build();
			// Create the instace and solve the problem
			/*
			VehicleRoutingAlgorithms vras = new VehicleRoutingAlgorithms();
			VehicleRoutingAlgorithm vra = vras.readAndCreateAlgorithm(vrp,
					(int)options.get(CONSTANTS.THREADS), (String)options.get(CONSTANTS.CONFIG),number);
			setTimeLimit(vra, (long)options.get(CONSTANTS.TIME));
			*/
			// Solve the problem
			//Collection<VehicleRoutingProblemSolution> solutions = vra.searchSolutions();
			/*
			System.out.println("grandezza soluzione: "+solutions.size());
			for(VehicleRoutingProblemSolution a : solutions) {
				System.out.println(a.getCost());
			}
			*/
			// Extract the best solution
			//VehicleRoutingProblemSolution solution = new SelectBest().selectSolution(solutions);



			//nuovo codice
			Collection<VehicleRoutingProblemSolution> solutions;
			VehicleRoutingProblemSolution solution;
			VehicleRoutingAlgorithms vras;
			VehicleRoutingAlgorithm vra;
			int j = 0;
			do {
				vras = new VehicleRoutingAlgorithms();
				vra = vras.readAndCreateAlgorithm(vrp,
						(int)options.get(CONSTANTS.THREADS), (String)options.get(CONSTANTS.CONFIG),number);
				setTimeLimit(vra, (long)options.get(CONSTANTS.TIME));
				solutions = vra.searchSolutions();
				solution = new SelectBest().selectSolution(solutions);
				j++;
			}
			while(solution.getRoutes().size()!=Integer.parseInt(System.getProperty("number"))&&j<60);

			//fine nuovo codice
			SolutionAnalyser analyser= new SolutionAnalyser(vrp, solution, new SolutionAnalyser.DistanceCalculator() {

				@Override
				public double getDistance(Location from, Location to) {
					return vrp.getTransportCosts().getTransportCost(from, to, 0., null, null);
				}
			});
			int i=0;
			for(VehicleRoute vr : solution.getRoutes()) {
				System.out.println("att"+vr.getActivities().size());
				//System.out.println(i+" "+analyser.getDistance(vr));
				i++;
			}
			// Print solution on a file
			try {
			//OROutils.write(solution, (String)options.get(PARAMS.INSTANCE), System.currentTimeMillis()-startTime, (String)options.get(CONSTANTS.OUTPUT));
			OROutils.write(solution, (String)options.get(PARAMS.INSTANCE), System.currentTimeMillis()-startTime, (String)options.get(CONSTANTS.OUTPUT),j);

			}
			catch(Exception e) {
				e.printStackTrace();
			}
			// Print solution on the screen (optional)
			//SolutionPrinter.print(vrp, solution, SolutionPrinter.Print.VERBOSE);
			// Draw solution on the screen (optional)
			//new GraphStreamViewer(vrp, solution).labelWith(Label.ID).setRenderDelay(10).display();
		}
	}

	private static void setTimeLimit(VehicleRoutingAlgorithm vra, long timeMilliSec) {
		TimeTermination tterm = new TimeTermination(timeMilliSec);
		vra.setPrematureAlgorithmTermination(tterm);
		vra.addListener(tterm);
	}
}
