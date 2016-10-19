package it.unict.test;

import it.unict.eve.Environment;
import it.unict.eve.Matrix;
import it.unict.eve.ODEModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EnvironmentTest
{
	private Environment environment;
	
	@SuppressWarnings("deprecation")
	@Before
	public void setUp()
	{
		Matrix realData = new Matrix( new double[][]
				{{1, 3.52, 6.7456, 10.533376},
				{4, 4.96, 5.5168, 5.387008},
				{6, 6.72, 6.8928, 6.266112}});
		double Ts = 0.12;

		this.environment = new Environment(realData, Ts, Matrix.totalErrorMatrixDistanceCalculator);
	}
	
	@Test
	public void testEnvironment()
	{
		System.out.println("---------- testEnvironment >>>>>>>>>>");
		
		ODEModel odemPerfect = new ODEModel( new double[][]
			{ {1, 2, 2},
			  {1, 1, 1},
			  {2, 1, 2} });
		
		
		double fitPerfect;
		environment.setDistanceCalculator(Matrix.maximumErrorMatrixDistanceCalculator);
		fitPerfect = environment.live(odemPerfect);
			System.out.println("fitPerfect max = " + fitPerfect);
		Assert.assertTrue(Matrix.areEqualWithinLimit(0.0, fitPerfect, 0.000000001));
		
		double fitPerfect2;
		environment.setDistanceCalculator(Matrix.totalErrorMatrixDistanceCalculator);
		fitPerfect2 = environment.live(odemPerfect);
			System.out.println("fitPerfect tot = " + fitPerfect2);
		Assert.assertTrue(Matrix.areEqualWithinLimit(0.0, fitPerfect2, 0.000000001));
		

		ODEModel odemUnperfect1 = new ODEModel( new double[][]
				{ { 1  ,  2.3, 2},
				  {-2.3,  1  , 1},
				  {-2  , -1  , 2} });
		
		double fitUnperfect1;
		environment.setDistanceCalculator(Matrix.maximumErrorMatrixDistanceCalculator);
		fitUnperfect1 = environment.live(odemUnperfect1);
		System.out.println("fitUnperfect1 max = " + fitUnperfect1);
		Assert.assertTrue(Matrix.areEqualWithinLimit(0.569121984, fitUnperfect1, 0.000000001));
		environment.setDistanceCalculator(Matrix.totalErrorMatrixDistanceCalculator);
		fitUnperfect1 = environment.live(odemUnperfect1);
		System.out.println("fitUnperfect1 tot = " + fitUnperfect1);
		Assert.assertTrue(Matrix.areEqualWithinLimit(1.91167776, fitUnperfect1, 0.000000001));
		
		
		ODEModel odemUnperfect2 = new ODEModel( new double[][]
				{ { 0  ,  2.3,  2    },
				  {-2.3,  0  , -0.5  },
				  {-2  ,  0.5,  0.001} });
		double fitUnperfect2;
		
		environment.setDistanceCalculator(Matrix.maximumErrorMatrixDistanceCalculator);
		fitUnperfect2 = environment.live(odemUnperfect2);
		System.out.println("fitUnperfect2 max = " + fitUnperfect2);
		Assert.assertTrue(Matrix.areEqualWithinLimit(5.314246162, fitUnperfect2, 0.000000001));
		environment.setDistanceCalculator(Matrix.totalErrorMatrixDistanceCalculator);
		fitUnperfect2 = environment.live(odemUnperfect2);
		System.out.println("fitUnperfect2 tot = " + fitUnperfect2);
		Assert.assertTrue(Matrix.areEqualWithinLimit(18.5047521732, fitUnperfect2, 0.000000001));

		System.out.println("<<<<<<<<<< testEnvironment ----------");
	}
}