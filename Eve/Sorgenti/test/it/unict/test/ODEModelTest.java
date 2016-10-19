package it.unict.test;

import it.unict.eve.Matrix;
import it.unict.eve.ODEModel;

import org.junit.Assert;
import org.junit.Test;

public class ODEModelTest
{
	@Test
	public void testCalculate1()
	{
		System.out.println("---------- testCalculate1 >>>>>>>>>>");
		
		Matrix init = new Matrix( new double[][]
			{ {1},
			  {4},
			  {6} }) ;
		ODEModel odem = new ODEModel( new double[][]
			{ {1, 2, 2},
			  {1, 1, 1},
			  {2, 1, 2} });
		double Ts = 0.12;
		
		System.out.println("model\n" + odem.getModel());
		
		Matrix s = odem.calculate(init, Ts, 4);
		
		Matrix expected = new Matrix( new double[][]
			{{1, 3.52, 6.7456, 10.533376},
			{4, 4.96, 5.5168, 5.387008},
			{6, 6.72, 6.8928, 6.266112}});
		
		System.out.println("Matrice S\n" + s);
		System.out.println("Matrice Expected\n" + expected);
		
		Assert.assertTrue( expected.equals(s) );
		
		System.out.println("<<<<<<<<<< testCalculate1 ----------");
	}
	
	@Test
	public void testCalculate2()
	{
		System.out.println("---------- testCalculate2 >>>>>>>>>>");
		
		Matrix init = new Matrix( new double[][]
			{ {1},
			  {4},
			  {6} }) ;
		ODEModel odem = new ODEModel( new double[][]
			{ { 1, -1  ,  0  },
			  {-1,  0  ,  1  },
			  { 2,  0.5, -0.5} });
		double Ts = 0.53;
		
		System.out.println("model\n" + odem.getModel());
		
		Matrix s = odem.calculate(init, Ts, 5);
		
		Matrix expected = new Matrix( new double[][]
			{{1, -0.59, -4.989, -12.197, -21.148785065},
			{4, 7.71, 8.611, 4.6931605, -5.1262264825},
			{6, 2.29, -2.40315, -6.33014525, -7.1400318238}});
		System.out.println("Matrice S\n" + s);
		System.out.println("Matrice Expected\n" + expected);
		Assert.assertTrue( expected.equals(s) );
		
		System.out.println("<<<<<<<<<< testCalculate2 ----------");
	}
	
	@Test
	public void testCalculate3()
	{
		System.out.println("---------- testCalculate3 >>>>>>>>>>");
		
		Matrix init = new Matrix( new double[][]
			{ {1},
			  {4},
			  {6},
			  {2} } ) ;
		ODEModel odem = new ODEModel( new double[][]
			{ { 1,  -1,    0,  0.1},
			  {-1,   0,    1, -0.2},
			  { 2, 0.5, -0.5,    0},
			  { 0,   0, -0.3,    1} } );
		double Ts = 0.071;
		
		System.out.println("model\n" + odem.getModel());
		System.out.println("ODEModel" + odem);
		
		Matrix s = odem.calculate(init, Ts, 10);
		
		Matrix expected = new Matrix( new double[][]
			{{1, 0.8012, 0.55637567, 0.266113984, -0.0686157778, -0.4464580966, -0.865669817, -1.3241213282, -1.8193003441, -2.3483182865},
			{4, 4.4686, 4.88507606, 5.244743125, 5.5432439761, 5.7765891345, 5.9411948975, 6.0339191901, 6.0520949263, 5.9935605854},
			{6, 5.503, 4.9903729, 4.4663742618, 3.9354412136, 3.4021627282, 2.8712481228, 2.3474939767, 1.8357496781, 1.3408818247},
			{2, 2.1917, 2.4050763, 2.6412545301, 2.9013695448, 3.186568019, 3.4980117665, 3.8368818252, 4.2043833487, 4.6017513469}} );
		System.out.println("Matrice S\n" + s);
		System.out.println("Matrice Expected\n" + expected);
		Assert.assertTrue( expected.equals(s) );
		
		System.out.println("<<<<<<<<<< testCalculate3 ----------");
	}
}