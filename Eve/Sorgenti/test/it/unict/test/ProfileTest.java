package it.unict.test;

import junit.framework.*;

import org.junit.Before;
import org.junit.Test;

import it.unict.eve.*;

public class ProfileTest
{
	private Profile profile;
	
	@Before
	public void setUp() throws Exception
	{
		profile = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
	}
	
	@Test
	public void testProfileConstructor()
	{
		System.out.println("---------- testProfileConstructor >>>>>>>>>>");
		
		new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		
		try //nome null
		{
			new Profile(null, new double[]{1,2,4,5,8,4,2,5,4}, 1);
			Assert.fail("Exception not thrown");
		}
		catch(NullPointerException e) {}
		
		try //data null
		{
			new Profile("Alfa", null, 1);
			Assert.fail("Exception not thrown");
		}
		catch(NullPointerException e) {}
		
		try //Ts 0
		{
			new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 0);
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e) {}
		
		try //Ts < 0
		{
			new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, -0.3);
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e) {}
		
		System.out.println("<<<<<<<<<< testProfileConstructor ----------");
	}

	@Test
	public void testDataToString()
	{
		System.out.println("---------- testDataToString >>>>>>>>>>");
		
		System.out.println(profile.dataToString());
		Assert.assertEquals(profile.dataToString(), "[ 1.0\t2.0\t4.0\t5.0\t8.0\t4.0\t2.0\t5.0\t4.0 ]");
		
		System.out.println("<<<<<<<<<< testDataToString ----------");
	}
	
	@Test
	public void testToString()
	{
		System.out.println("---------- testToString >>>>>>>>>>");
		
		System.out.println(profile);
		Assert.assertEquals(profile.toString(), "Profilo \"Alfa\"\tN = 9\tTs = 1.0\t[ 1.0\t2.0\t4.0\t5.0\t8.0\t4.0\t2.0\t5.0\t4.0 ]");
		
		System.out.println("<<<<<<<<<< testToString ----------");
	}
	
	@Test
	public void testSetName()
	{
		System.out.println("---------- testSetName >>>>>>>>>>");
		
		profile.setName("AlfaAlfa");
		Assert.assertEquals("AlfaAlfa", profile.getName());
		
		try
		{
			profile.setName(null);
			Assert.fail("Exception not thrown");
		}
		catch(NullPointerException e) {}
		
		System.out.println("<<<<<<<<<< testSetName ----------");
	}
	
	@Test
	public void testSetAdmittableError()
	{
		System.out.println("---------- testSetAdmittableError >>>>>>>>>>");
		
		Assert.assertEquals(0.000001, profile.getAdmittableError()); //errore di default
		profile.setAdmittableError(Profile.DoubleEqualityErrorAdmissibility.BILLIONTH);
		Assert.assertEquals(0.000000001, profile.getAdmittableError());
		profile.setAdmittableError(0.0002);
		Assert.assertEquals(0.0002, profile.getAdmittableError());
		profile.setAdmittableError(-0.0008);
		Assert.assertEquals(0.0008, profile.getAdmittableError());
		
		System.out.println("<<<<<<<<<< testSetAdmittableError ----------");
	}
	
	@Test
	public void testSetDistanceType()
	{
		System.out.println("---------- testSetDistanceType >>>>>>>>>>");
		
		Assert.assertEquals(Matrix.DistanceType.TotalError, profile.getDistanceType());
		profile.setDistanceType(Matrix.DistanceType.MaximumError);
		Assert.assertEquals(Matrix.DistanceType.MaximumError, profile.getDistanceType());
		
		System.out.println("<<<<<<<<<< testSetDistanceType ----------");
	}

	@Test
	public void testIsCompatible()
	{
		System.out.println("---------- testIsCompatible >>>>>>>>>>");
		
		Profile alfa = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Profile beta = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
		System.out.println(alfa);
		System.out.println(beta);
		Assert.assertTrue(alfa.isCompatible(beta));
		
		alfa = new Profile("Alfa", new double[]{1,2,4,5,4,2,5,4}, 1);
		beta = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
		System.out.println(alfa);
		System.out.println(beta);
		Assert.assertFalse(alfa.isCompatible(beta));
		
		alfa = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1.01);
		beta = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
		System.out.println(alfa);
		System.out.println(beta);
		Assert.assertFalse(alfa.isCompatible(beta));
		
		System.out.println("<<<<<<<<<< testIsCompatible ----------");
	}

	@Test
	public void testAreCompatibleProfileArray()
	{
		System.out.println("---------- testAreCompatibleProfileArray >>>>>>>>>>");
		
		Profile[] dati = new Profile[5];
		
		dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
		dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
		dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Assert.assertTrue(Profile.areCompatible(dati));
		
		dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
		dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1.22); //Ts diverso
		dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Assert.assertFalse(Profile.areCompatible(dati));
		
		dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
		dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,4}, 1); //non isodimensionale
		dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Assert.assertFalse(Profile.areCompatible(dati));
		
		System.out.println("<<<<<<<<<< testAreCompatibleProfileArray ----------");
	}
	
	@Test
	public void testEquals()
	{
		System.out.println("---------- testEquals >>>>>>>>>>");
		
		//Errore di default è un milionesimo
		Profile p1, p2;
		
		p1 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		p2 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Assert.assertTrue(p1.equals(p2));
		
		p1 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		p2 = new Profile("Alfa", new double[]{1,2,4,5,6,4,2,5,4}, 1); //campione diverso
		Assert.assertFalse(p1.equals(p2));
		
		p1 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		p2 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1.2); //Ts diverso
		Assert.assertFalse(p1.equals(p2));
		
		p1 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		p2 = new Profile("AlfaBeta", new double[]{1,2,4,5,8,4,2,5,4}, 1.2); //nome diverso
		System.out.println(p1);
		System.out.println(p2);
		Assert.assertFalse(p1.equals(p2));
		
		Profile[] dati = new Profile[5];
		dati[0] = new Profile("Alfa", new double[]{-0.7027027027027027,-0.4594594594594595,0.027027027027027042,0.2702702702702703,1.0,0.027027027027027042,-0.4594594594594595,0.2702702702702703,0.027027027027027042}, 1);
		dati[1] = new Profile("Beta", new double[]{0.035714285714285594,-0.28571428571428586,1.0,0.6785714285714285,0.035714285714285594,0.35714285714285704,-0.6071428571428573,-0.9285714285714288,-0.28571428571428586}, 1);
		dati[2] = new Profile("Gamma", new double[]{-0.7027027027027027,-0.4594594594594595,0.027027027027027042,0.2702702702702703,1.0,0.027027027027027042,-0.4594594594594595,0.2702702702702703,0.027027027027027042}, 1);
		dati[3] = new Profile("Delta", new double[]{0.035714285714285594,-0.28571428571428586,1.0,0.6785714285714285,0.035714285714285594,0.35714285714285704,-0.6071428571428573,-0.9285714285714288,-0.28571428571428586}, 1);
		dati[4] = new Profile("Iota", new double[]{-0.7027027027027027,-0.4594594594594595,0.027027027027027042,0.2702702702702703,1.0,0.027027027027027042,-0.4594594594594595,0.2702702702702703,0.027027027027027042}, 1);

		Profile[] dati2 = new Profile[5];
		dati2[0] = new Profile("Alfa", new double[]{-0.7027027027,-0.4594594595,0.027027027,0.2702702703,1,0.027027027,-0.4594594595,0.2702702703,0.027027027}, 1);
		dati2[1] = new Profile("Beta", new double[]{0.0357142857,-0.2857142857,1,0.6785714286,0.0357142857,0.3571428571,-0.6071428571,-0.9285714286,-0.2857142857}, 1);
		dati2[2] = new Profile("Gamma", new double[]{-0.7027027027,-0.4594594595,0.027027027,0.2702702703,1,0.027027027,-0.4594594595,0.2702702703,0.027027027}, 1);
		dati2[3] = new Profile("Delta", new double[]{0.0357142857,-0.2857142857,1,0.6785714286,0.0357142857,0.3571428571,-0.6071428571,-0.9285714286,-0.2857142857}, 1);
		dati2[4] = new Profile("Iota", new double[]{-0.7027027027,-0.4594594595,0.027027027,0.2702702703,1,0.027027027,-0.4594594595,0.2702702703,0.027027027}, 1);
		
		for(int i=0; i<dati.length; i++)
		{
			dati[i].setAdmittableError(Profile.DoubleEqualityErrorAdmissibility.NONE);
			Assert.assertFalse(dati[i].equals(dati2[i]));
		}
		for(int i=0; i<dati.length; i++)
		{
			dati[i].setAdmittableError(Profile.DoubleEqualityErrorAdmissibility.BILLIONTH);
			Assert.assertTrue(dati[i].equals(dati2[i]));
		}
		for(int i=0; i<dati.length; i++)
		{
			dati[i].setAdmittableError(0.000000000000001); //errore concesso molto piccolo
			Assert.assertFalse(dati[i].equals(dati2[i]));
		}

		System.out.println("<<<<<<<<<< testEquals ----------");
	}
	
	@Test
	public void testEqualsButName()
	{
		System.out.println("---------- testEqualsButName >>>>>>>>>>");
		
		//Errore di default è un milionesimo
		Profile p1, p2;
		
		p1 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		p2 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Assert.assertTrue(p1.equalsButName(p2));
		
		p1 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		p2 = new Profile("Alfa", new double[]{1,2,4,5,6,4,2,5,4}, 1); //campione diverso
		Assert.assertFalse(p1.equalsButName(p2));
		
		p1 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		p2 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1.2); //Ts diverso
		Assert.assertFalse(p1.equalsButName(p2));
		
		p1 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		p2 = new Profile("AlfaBeta", new double[]{1,2,4,5,8,4,2,5,4}, 1); //nome diverso
		System.out.println(p1);
		System.out.println(p2);
		Assert.assertTrue(p1.equalsButName(p2));
		
		Profile[] dati = new Profile[5];
		dati[0] = new Profile("Alfa", new double[]{-0.7027027027027027,-0.4594594594594595,0.027027027027027042,0.2702702702702703,1.0,0.027027027027027042,-0.4594594594594595,0.2702702702702703,0.027027027027027042}, 1);
		dati[1] = new Profile("Beta", new double[]{0.035714285714285594,-0.28571428571428586,1.0,0.6785714285714285,0.035714285714285594,0.35714285714285704,-0.6071428571428573,-0.9285714285714288,-0.28571428571428586}, 1);
		dati[2] = new Profile("Gamma", new double[]{-0.7027027027027027,-0.4594594594594595,0.027027027027027042,0.2702702702702703,1.0,0.027027027027027042,-0.4594594594594595,0.2702702702702703,0.027027027027027042}, 1);
		dati[3] = new Profile("Delta", new double[]{0.035714285714285594,-0.28571428571428586,1.0,0.6785714285714285,0.035714285714285594,0.35714285714285704,-0.6071428571428573,-0.9285714285714288,-0.28571428571428586}, 1);
		dati[4] = new Profile("Iota", new double[]{-0.7027027027027027,-0.4594594594594595,0.027027027027027042,0.2702702702702703,1.0,0.027027027027027042,-0.4594594594594595,0.2702702702702703,0.027027027027027042}, 1);

		Profile[] dati2 = new Profile[5];
		dati2[0] = new Profile("Alfa", new double[]{-0.7027027027,-0.4594594595,0.027027027,0.2702702703,1,0.027027027,-0.4594594595,0.2702702703,0.027027027}, 1);
		dati2[1] = new Profile("Beta", new double[]{0.0357142857,-0.2857142857,1,0.6785714286,0.0357142857,0.3571428571,-0.6071428571,-0.9285714286,-0.2857142857}, 1);
		dati2[2] = new Profile("Gamma", new double[]{-0.7027027027,-0.4594594595,0.027027027,0.2702702703,1,0.027027027,-0.4594594595,0.2702702703,0.027027027}, 1);
		dati2[3] = new Profile("Delta", new double[]{0.0357142857,-0.2857142857,1,0.6785714286,0.0357142857,0.3571428571,-0.6071428571,-0.9285714286,-0.2857142857}, 1);
		dati2[4] = new Profile("Iota", new double[]{-0.7027027027,-0.4594594595,0.027027027,0.2702702703,1,0.027027027,-0.4594594595,0.2702702703,0.027027027}, 1);
		
		for(int i=0; i<dati.length; i++)
		{
			dati[i].setAdmittableError(Profile.DoubleEqualityErrorAdmissibility.NONE);
			Assert.assertFalse(dati[i].equalsButName(dati2[i]));
		}
		for(int i=0; i<dati.length; i++)
		{
			dati[i].setAdmittableError(Profile.DoubleEqualityErrorAdmissibility.BILLIONTH);
			Assert.assertTrue(dati[i].equalsButName(dati2[i]));
		}
		for(int i=0; i<dati.length; i++)
		{
			dati[i].setAdmittableError(0.000000000000001); //errore concesso molto piccolo
			Assert.assertFalse(dati[i].equalsButName(dati2[i]));
		}

		System.out.println("<<<<<<<<<< testEqualsButName ----------");
	}
	
	@Test
	public void testDistance()
	{
		System.out.println("---------- testDistance >>>>>>>>>>");
		
		Profile p1 = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Profile p2 = new Profile("Beta", new double[]{1,4,4,4,6,6,-5,3,4}, 1);
		Assert.assertEquals(p1.distance(p2), 16.0);
		Assert.assertEquals(p2.distance(p1), 16.0);
		
		Profile alfa, beta, gamma, delta, iota;
			alfa  = new Profile("Alfa" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
			beta  = new Profile("Beta" , new double[]{5,4,8,7,5,6,3,2,4}, 1);
			gamma = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			delta = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			iota  = new Profile("Iota" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Assert.assertEquals(alfa.distance(beta), 21.0);
		Assert.assertEquals(alfa.distance(gamma), 0.0);
		Assert.assertEquals(alfa.distance(delta), 21.0);
		Assert.assertEquals(alfa.distance(iota), 0.0);
		Assert.assertEquals(beta.distance(gamma), 21.0);
		Assert.assertEquals(beta.distance(delta), 0.0);
		Assert.assertEquals(beta.distance(iota), 21.0);
		Assert.assertEquals(gamma.distance(delta), 21.0);
		Assert.assertEquals(gamma.distance(iota), 0.0);
		Assert.assertEquals(delta.distance(iota), 21.0);
		
		Profile[] dati = new Profile[5];
		dati[0] = new Profile("Alfa" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
		dati[1] = new Profile("Beta" , new double[]{5,4,8,7,5,6,3,2,4}, 1);
		dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
		dati[4] = new Profile("Iota" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
		
		for(int i=0; i<dati.length; i++)
			for(int j=i+1; j<dati.length; j++)
			{
				Assert.assertTrue( dati[i].distance(dati[j]) == 0.0 || dati[i].distance(dati[j]) == 21.0 );
				System.out.println(i + " vs " + j + " d=" + dati[i].distance(dati[j]));
			}
	
		System.out.println("<<<<<<<<<< testDistance ----------");
	}

	@Test
	public void testNormalize()
	{
		System.out.println("---------- testNormalize >>>>>>>>>>");
		
		Profile p = new Profile("Alfa", new double[]{-1,2,4,-5,8,4,-2,5,4}, 1);
		Profile expected = new Profile("Alfa", new double[]{-0.4375, -0.015625, 0.265625, -1, 0.828125, 0.265625, -0.578125, 0.40625, 0.265625}, 1);
		System.out.println(p);
		System.out.println(p.normalize());
		System.out.println(expected);
		
		Assert.assertTrue( expected.equals(p.normalize()) );

		System.out.println("<<<<<<<<<< testNormalize ----------");
	}
	
	@Test
	public void testInterpolate()
	{
		System.out.println("---------- testInterpolate >>>>>>>>>>");
		
		Profile notInterpolated = new Profile("Alfa", new double[]{1, 2, 4, 2, 5, 6, 4, 2}, 3);
		Profile interpolated = notInterpolated.interpolate(100).setName("Alfa_interpolated");
			notInterpolated.setAdmittableError(Profile.DoubleEqualityErrorAdmissibility.BILLIONTH);
		
		double[] expectedData = new double[]
		{ 1         , 1.0244491746, 1.0502929165, 1.0789257933, 1.1117423724, 1.1501372211, 1.195504907 , 1.2492399973, 1.3127370596, 1.3873906612,
		1.4745953696, 1.5757457522, 1.6922363764, 1.8254618097, 1.9768166193, 2.1470397731, 2.3328243383, 2.5293215087, 2.7316794428, 2.9350462993,
		3.1345702368, 3.3253994139, 3.5026819891, 3.6615661211, 3.7971999685, 3.9047316898, 3.9793094437, 4.0160813887, 4.0101956836, 3.9575451212,
		3.8613377831, 3.7289338326, 3.5677410892, 3.3851673728, 3.188620503 , 2.9855082996, 2.7832385823, 2.5892191708, 2.4108578849, 2.2555625442,
		2.1307409685, 2.0438009775, 2.0021503909, 2.0127896957, 2.0758774602, 2.1858950518, 2.3371519943, 2.5239578112, 2.7406220266, 2.9814541639,
		3.2407637471, 3.5128602999, 3.792053346 , 4.0726524091, 4.3489670131, 4.6153066817, 4.8659809385, 5.095380617 , 5.3005827756, 5.4819017962,
		5.6398447942, 5.774918885 , 5.8876311841, 5.9784888068, 6.0479988686, 6.0966684849, 6.1250047712, 6.1335148428, 6.1227058152, 6.0930848038,
		6.0451589241, 5.9794393105, 5.8967872582, 5.7986809882, 5.6866615194, 5.5622698706, 5.4270470607, 5.2825341086, 5.1302720331, 4.971801853 ,
		4.8086645873, 4.6424012548, 4.4745528742, 4.3066604646, 4.1402650447, 3.9769067349, 3.8176691904, 3.6624382948, 3.5109058446, 3.3627636358,
		3.2177034647, 3.0754171276, 2.9355964207, 2.7979331401, 2.6621190822, 2.5278460431, 2.3948058191, 2.2626902065, 2.1311910014, 2 };
		Profile expected = new Profile("Alfa_interpolated_expected", expectedData, 0.21212121212121213);
			expected.setAdmittableError(Profile.DoubleEqualityErrorAdmissibility.BILLIONTH);
		
		System.out.println(notInterpolated);
		System.out.println(interpolated);
		System.out.println(expected);

		Assert.assertTrue(expected.equalsButName(interpolated));
		Assert.assertFalse(expected.equals(interpolated));//perchè hanno nome diverso
		
		System.out.println("<<<<<<<<<< testInterpolate ----------");
	}

	@Test
	public void testUnify()
	{
		System.out.println("---------- testUnify >>>>>>>>>>");
		
		Profile[] dati = new Profile[5];
			dati[0] = new Profile("Alfa" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[1] = new Profile("Beta" , new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[4] = new Profile("Iota" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Profile newP = Profile.unify(dati);
		
		Profile controllo = new Profile("Controllo", new double[]{2.6, 2.8, 5.6, 5.8, 6.8, 4.8, 2.4, 3.8, 4}, 1);
		
		System.out.println(newP);
		System.out.println(controllo);
		
		Assert.assertTrue(newP.equalsButName(controllo));
		Assert.assertFalse(newP.equals(controllo));//nome diverso
		
		System.out.println("<<<<<<<<<< testUnify ----------");
	}
	
	@Test
	public void testTo2DArray()
	{
		System.out.println("---------- testTo2DArray >>>>>>>>>>");
		
		Profile[] dati = new Profile[5];
			dati[0] = new Profile("Alfa" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[1] = new Profile("Beta" , new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[4] = new Profile("Iota" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
		double[][] array = Profile.to2DArray(dati);
		
		Matrix m1 = new Matrix(array);
		Matrix m2 = new Matrix( new double[][]
		{ {1,2,4,5,8,4,2,5,4},
		  {5,4,8,7,5,6,3,2,4},
		  {1,2,4,5,8,4,2,5,4},
		  {5,4,8,7,5,6,3,2,4},
		  {1,2,4,5,8,4,2,5,4} } );
		
		System.out.println("m1\n" + m1);
		System.out.println("m2\n" + m2);
		Assert.assertTrue(m1.equals(m2));
		
		System.out.println("<<<<<<<<<< testTo2DArray ----------");
	}
}