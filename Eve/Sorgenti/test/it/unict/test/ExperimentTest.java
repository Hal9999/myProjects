package it.unict.test;

import it.unict.eve.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ExperimentTest
{
	private Experiment experiment;
	
	@Before
	public void setUp() throws Exception
	{
		Profile[] dati = new Profile[5];
			dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		experiment = new Experiment("Exp000", dati);
	}

	@Test
	public void testReadNamesFromFile() throws IOException
	{
		
		LinkedList<File> fileList = new LinkedList<File>();
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\Data_treated_Names.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\Data_UNtreated_Names.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\DATA1_medie_Names.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\DataLZ_Names.txt") );
		
		for(File file : fileList)
		{
			long time = System.currentTimeMillis();
			String[] ss = Experiment.readNamesFromFile(file);
			double elapsed = (System.currentTimeMillis() - time)/(float)1000;

			System.out.println("List from file: " + file.getName());
			System.out.println("lenght = " + ss.length);
			System.out.println("took " + elapsed + " s to load");
			System.out.println();
		}
	}
	
	@Test
	public void testWriteNamesToFile() throws IOException
	{
		
		LinkedList<File> fileList = new LinkedList<File>();
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\Data_treated_Names.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\Data_UNtreated_Names.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\DATA1_medie_Names.txt") );
		fileList.add(new File("C:\\Users\\Stefano\\Desktop\\Download\\matlabbable\\DataLZ_Names.txt") );
		
		for(File file : fileList)
		{
			long time = System.currentTimeMillis();
			String[] ss = Experiment.readNamesFromFile(file);
			double elapsed1 = (System.currentTimeMillis() - time)/(float)1000;
			
			time = System.currentTimeMillis();
			Experiment.writeNamesToFile(ss, new File(file.getAbsolutePath() + "2.txt"));
			double elapsed2 = (System.currentTimeMillis() - time)/(float)1000;

			System.out.println("List from file: " + file.getName());
			System.out.println("lenght = " + ss.length);
			System.out.println("took " + elapsed1 + " s to load");
			System.out.println("took " + elapsed2 + " s to write");
			System.out.println();
		}
	}
	
	@Test
	public void testExperimentStringSetOfProfile()
	{
		System.out.println("---------- testExperimentStringSetOfProfile >>>>>>>>>>");
		
		Set<Profile> set = new HashSet<Profile>();
			set.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
			set.add( new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
			set.add( new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
			set.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
			set.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
		Experiment esp = new Experiment( "Exp01", set );
		System.out.println("\"" + esp + "\"");
		
		try
		{
			new Experiment(null, set);
			Assert.fail("Exception not thrown");
		}
		catch(NullPointerException e){}
		
		try
		{
			new Experiment("hello", (HashSet<Profile>)null);
			Assert.fail("Exception not thrown");
		}
		catch(NullPointerException e){}
		
		System.out.println("<<<<<<<<<< testExperimentStringSetOfProfile ----------");
	}

	@Test
	public void testExperimentStringProfileArray()
	{
		System.out.println("---------- testExperimentStringProfileArray >>>>>>>>>>");
		
		{
			Profile[] dati = new Profile[5];
				dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
				dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
				dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
				dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
				dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			Experiment esp = new Experiment( "Exp00", dati );
			System.out.println("\"" + esp + "\"");
			
			try
			{
				new Experiment(null, dati);
				Assert.fail("Exception not thrown");
			}
			catch(NullPointerException e){}

			try
			{
				new Experiment("hello", (Profile[])null);
				Assert.fail("Exception not thrown");
			}
			catch(NullPointerException e){}
		}
		
		
		try
		{
			Profile[] dati = new Profile[5];
				dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
				dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
				dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
				dati[3] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1); //nome non univoco
				dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			new Experiment( "Exp00", dati );
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e){}
		
		try
		{
			Profile[] dati = new Profile[5];
				dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
				dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
				dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
				dati[3] = new Profile("Delta", new double[]{5,8,7,5,6,3,2,4}, 1); //Profile non compatibile
				dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			new Experiment( "Exp00", dati );
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e){}
		
		try
		{
			Profile[] dati = new Profile[5];
				dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
				dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
				dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
				dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 3); //Profile non compatibile
				dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			new Experiment( "Exp00", dati );
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e){}
		
		System.out.println("<<<<<<<<<< testExperimentStringProfileArray ----------");
	}

	@Test
	public void testToString()
	{
		System.out.println("---------- testToString >>>>>>>>>>");
		
		System.out.println("\"" + experiment + "\"");
		Assert.assertEquals("Experiment \"Exp000\" contains 5 Profile(s), Ts = 1.0\n\tBeta\t9\t[ 5.0\t4.0\t8.0\t7.0\t5.0\t6.0\t3.0\t2.0\t4.0 ]\n\tAlfa\t9\t[ 1.0\t2.0\t4.0\t5.0\t8.0\t4.0\t2.0\t5.0\t4.0 ]\n\tIota\t9\t[ 1.0\t2.0\t4.0\t5.0\t8.0\t4.0\t2.0\t5.0\t4.0 ]\n\tDelta\t9\t[ 5.0\t4.0\t8.0\t7.0\t5.0\t6.0\t3.0\t2.0\t4.0 ]\n\tGamma\t9\t[ 1.0\t2.0\t4.0\t5.0\t8.0\t4.0\t2.0\t5.0\t4.0 ]",
							experiment.toString());
		
		System.out.println("<<<<<<<<<< testToString ----------");
	}
	
	@Test
	public void testGetSize()
	{
		System.out.println("---------- testGetSize >>>>>>>>>>");
		
		Assert.assertEquals(5, experiment.getSize());
		
		System.out.println("<<<<<<<<<< testGetSize ----------");
	}

	@Test
	public void testSetName()
	{
		System.out.println("---------- testSetName >>>>>>>>>>");
		
		experiment.setName("Exp_naame");
		Assert.assertEquals("Exp_naame", experiment.getName());
		
		try
		{
			experiment.setName(null);
			Assert.fail("Exception not thrown");
		}
		catch(NullPointerException e){}
		
		System.out.println("<<<<<<<<<< testSetName ----------");
	}

	@Test
	public void testSetAdmittableErrorDoubleEqualityErrorAdmissibility()
	{
		System.out.println("---------- testSetAdmittableErrorDoubleEqualityErrorAdmissibility >>>>>>>>>>");
		
		experiment.setAdmittableError(Experiment.DoubleEqualityErrorAdmissibility.BILLIONTH);
		Profile[] profs = experiment.getProfiles();
		for(int i=0; i<profs.length; i++)
			Assert.assertEquals(0.000000001, profs[i].getAdmittableError());
		Assert.assertEquals(0.000000001, experiment.getAdmittableError());
		
		System.out.println("<<<<<<<<<< testSetAdmittableErrorDoubleEqualityErrorAdmissibility ----------");
	}

	@Test
	public void testSetAdmittableErrorDouble()
	{
		System.out.println("---------- testSetAdmittableErrorDouble >>>>>>>>>>");
		
		experiment.setAdmittableError(0.002547891564);
		Profile[] profs = experiment.getProfiles();
		for(int i=0; i<profs.length; i++)
			Assert.assertEquals(0.002547891564, profs[i].getAdmittableError());
		Assert.assertEquals(0.002547891564, experiment.getAdmittableError());
		
		System.out.println("<<<<<<<<<< testSetAdmittableErrorDouble ----------");
	}

	@Test
	public void testGetCluDistType()
	{
		System.out.println("---------- testGetCluDistType >>>>>>>>>>");
		
		Assert.assertEquals(Matrix.DistanceType.TotalError, experiment.getCluDistType());
		
		System.out.println("<<<<<<<<<< testGetCluDistType ----------");
	}

	@Test
	public void testSetCluDistType()
	{
		System.out.println("---------- testSetCluDistType >>>>>>>>>>");
		
		experiment.setCluDistType(Matrix.DistanceType.MaximumError);
		Profile[] profs = experiment.getProfiles();
		for(int i=0; i<profs.length; i++)
			Assert.assertEquals(Matrix.DistanceType.MaximumError, profs[i].getDistanceType());
		Assert.assertEquals(Matrix.DistanceType.MaximumError, experiment.getCluDistType());
		
		System.out.println("<<<<<<<<<< testSetCluDistType ----------");
	}

	@Test
	public void testContains()
	{
		System.out.println("---------- testContains >>>>>>>>>>");
		
		{
			Profile alfa = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			Profile beta = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			Profile gamma = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			Profile delta = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			Profile iota = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			Profile omega = new Profile("Omega", new double[]{1,2,4,5,8,4,2,5,4}, 1);
	
			Assert.assertTrue(experiment.contains(alfa));
			Assert.assertTrue(experiment.contains(delta));
			Assert.assertTrue(experiment.contains(iota));
			Assert.assertTrue(experiment.contains(beta));
			Assert.assertTrue(experiment.contains(gamma));
			Assert.assertFalse(experiment.contains(omega));
		}
		{
			Profile alfa = new Profile("Alffffffa", new double[]{1,2,4,5,8,4,2,5,4}, 1); //nome diverso
			Profile beta = new Profile("Beta", new double[]{5,4,7,5,6,3,2,4}, 1); //lunghezza diversa
			Profile gamma = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1.5); //Ts diverso
			Profile delta = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,40}, 1); //campione diverso
			
			Assert.assertFalse(experiment.contains(alfa)); //false per il nome diverso
			Assert.assertFalse(experiment.contains(beta));
			Assert.assertFalse(experiment.contains(gamma));
			Assert.assertFalse(experiment.contains(delta));
		}
		
		System.out.println("<<<<<<<<<< testContains ----------");
	}

	@Test
	public void testEqualsExperiment()
	{
		System.out.println("---------- testEqualsExperiment >>>>>>>>>>");
		
		int i=1;
		
		{
			Set<Profile> set1 = new HashSet<Profile>();
				set1.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set1.add( new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set1.add( new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set1.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set1.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
			Experiment esp1 = new Experiment( "Exp"+i++, set1 );
			
			Set<Profile> set2 = new HashSet<Profile>();
				set2.add( new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set2.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
			Experiment esp2 = new Experiment( "Exp"+i++, set2 );
			
			System.out.println(esp1);
			System.out.println(esp2);
			
			Assert.assertTrue( esp1.equals(esp2) );
		}
		
		{
			Set<Profile> set1 = new HashSet<Profile>();
				set1.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1.0001) ); //Ts diverso
				set1.add( new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1.0001) );
				set1.add( new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1.0001) );
				set1.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1.0001) );
				set1.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1.0001) );
			Experiment esp1 = new Experiment( "Exp"+i++, set1 );
			
			Set<Profile> set2 = new HashSet<Profile>();
				set2.add( new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set2.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
			Experiment esp2 = new Experiment( "Exp"+i++, set2 );
			
			System.out.println(esp1);
			System.out.println(esp2);
			
			Assert.assertFalse( esp1.equals(esp2) );
		}
		
		{
			Set<Profile> set1 = new HashSet<Profile>();
				set1.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set1.add( new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set1.add( new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set1.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set1.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
			Experiment esp1 = new Experiment( "Exp"+i++, set1 );
			
			Set<Profile> set2 = new HashSet<Profile>();
				set2.add( new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Beta", new double[]{5,4,8,7.23,5,6,3,2,4}, 1) ); //campione diverso
				set2.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
			Experiment esp2 = new Experiment( "Exp"+i++, set2 );
			
			System.out.println(esp1);
			System.out.println(esp2);
			
			Assert.assertFalse( esp1.equals(esp2) );
		}
		
		{
			Set<Profile> set1 = new HashSet<Profile>();
				set1.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set1.add( new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set1.add( new Profile("Gammmmmmmma", new double[]{1,2,4,5,8,4,2,5,4}, 1) );//nome diverso
				set1.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set1.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
			Experiment esp1 = new Experiment( "Exp"+i++, set1 );
			
			Set<Profile> set2 = new HashSet<Profile>();
				set2.add( new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set2.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				
			Experiment esp2 = new Experiment( "Exp"+i++, set2 );
			
			System.out.println(esp1);
			System.out.println(esp2);
			
			Assert.assertFalse( esp1.equals(esp2) );
		}
		
		{
			Set<Profile> set1 = new HashSet<Profile>();
				set1.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set1.add( new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set1.add( new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set1.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set1.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
			Experiment esp1 = new Experiment( "Exp"+i++, set1 );
			
			Set<Profile> set2 = new HashSet<Profile>();
				set2.add( new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set2.add( new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1) );
				set2.add( new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1) );
				set2.add( new Profile("Phi", new double[]{5,4,8,7,5,6,3,2,4}, 1) ); //in più
			Experiment esp2 = new Experiment( "Exp"+i++, set2 );
			
			System.out.println(esp1);
			System.out.println(esp2);
			
			Assert.assertFalse( esp1.equals(esp2) );
		}
		
		System.out.println("<<<<<<<<<< testEqualsExperiment ----------");
	}

	@Test
	public void testSelectByVariance()
	{
		System.out.println("---------- testSelectByMinVariance >>>>>>>>>>");
		
		Profile[] dati = new Profile[5];
			dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Experiment esp = new Experiment( "Exp", dati );
		for(Profile p : esp.getProfiles())
			System.out.println(p.getName() + " m=" + p.mean() + " v=" + p.variance());
		
		/*											var
		alfa	1	2	4	5	8	4	2	5	4	3,8765432099
		beta	5	4	8	7	5	6	3	2	4	3,2098765432
		gamma	1	2	4	5	8	4	2	5	4	3,8765432099
		delta	5	4	8	7	5	6	3	2	4	3,2098765432
		iota	1	2	4	5	8	4	2	5	4	3,8765432099
		*/
		Experiment esp_x = esp.selectByMinVariance(3.4).setName("ExpVmin");
		Experiment esp_X = new Experiment("ExpVmin_expected", new Profile[]{ dati[0], dati[2], dati[4] });
		System.out.println(esp_x);
		System.out.println(esp_X);
		Assert.assertTrue(esp_x.equals(esp_X));
		
		Experiment esp_y = esp.selectByMaxVariance(3.4).setName("ExpVmax");
		Experiment esp_Y = new Experiment("ExpVmax_expected", new Profile[]{ dati[1], dati[3] });
		System.out.println(esp_y);
		System.out.println(esp_Y);
		Assert.assertTrue(esp_y.equals(esp_Y));
		
		Set<Profile> set_U = new HashSet<Profile>();
		for(Profile p : esp_x.getProfiles()) set_U.add(p);
		for(Profile p : esp_y.getProfiles()) set_U.add(p);
		
		Experiment esp_U = new Experiment("Complementare", set_U);
		System.out.println(esp_U);
		Assert.assertTrue(esp_U.equals(esp));
		
		System.out.println("<<<<<<<<<< testSelectByMinVariance ----------");
	}

	@Test
	public void testNormalize()
	{
		System.out.println("---------- testNormalize >>>>>>>>>>");
		
		Profile[] dati = new Profile[5];
			dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[4] = new Profile("Iota", new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Experiment esp = new Experiment( "Exp", dati );
		Experiment espNor = esp.normalize().setName("Exp_normalized");
		
		Profile[] dati2 = new Profile[5];
			dati2[0] = new Profile("Beta" , new double[]{0.0357142857,-0.2857142857,1,0.6785714286,0.0357142857,0.3571428571,-0.6071428571,-0.9285714286,-0.2857142857}, 1);
			dati2[1] = new Profile("Alfa" , new double[]{-0.7027027027,-0.4594594595,0.027027027,0.2702702703,1,0.027027027,-0.4594594595,0.2702702703,0.027027027}, 1);
			dati2[2] = new Profile("Iota" , new double[]{-0.7027027027,-0.4594594595,0.027027027,0.2702702703,1,0.027027027,-0.4594594595,0.2702702703,0.027027027}, 1);
			dati2[3] = new Profile("Delta", new double[]{0.0357142857,-0.2857142857,1,0.6785714286,0.0357142857,0.3571428571,-0.6071428571,-0.9285714286,-0.2857142857}, 1);
			dati2[4] = new Profile("Gamma", new double[]{-0.7027027027,-0.4594594595,0.027027027,0.2702702703,1,0.027027027,-0.4594594595,0.2702702703,0.027027027}, 1);
		Experiment expected = new Experiment("Exp_normalized_expected", dati2);
		
		System.out.println(esp);
		System.out.println(espNor);
		System.out.println(expected);
		
		espNor.setAdmittableError(Experiment.DoubleEqualityErrorAdmissibility.NONE);
		Assert.assertFalse(espNor.equals(expected));
		
		espNor.setAdmittableError(0.00000001);
		Assert.assertTrue(espNor.equals(expected));
		
		System.out.println("<<<<<<<<<< testNormalize ----------");
	}

	@Test
	public void testClusterizeToProfiles()
	{
		System.out.println("---------- testClusterizeToProfiles >>>>>>>>>>");
		
		Profile[] dati = new Profile[5];
			dati[0] = new Profile("Alfa" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[1] = new Profile("Beta" , new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[4] = new Profile("Iota" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Experiment esperimento = new Experiment( "Exp", dati );
		
		List<Profile[]> clusters = esperimento.clusterizeToProfiles(10.0F);
		
		Experiment[] reportedClusteredExperiments = new Experiment[]
				{ new Experiment("Reported Clustering n°1", clusters.get(0)),
				  new Experiment("Reported Clustering n°2", clusters.get(1))  };
		
		Experiment[] expectedClusteredExperiments = new Experiment[]
				{ new Experiment("Expected Clustering n°1", new Profile[]{dati[0], dati[2], dati[4]}),
				  new Experiment("Expected Clustering n°2", new Profile[]{dati[1], dati[3]})           };		
		
		System.out.println(expectedClusteredExperiments[0]);
		System.out.println(reportedClusteredExperiments[1]);

		Assert.assertTrue( expectedClusteredExperiments[0].equals(reportedClusteredExperiments[0]) ||
						   expectedClusteredExperiments[0].equals(reportedClusteredExperiments[1])    );
		Assert.assertTrue( expectedClusteredExperiments[1].equals(reportedClusteredExperiments[0]) ||
				   		   expectedClusteredExperiments[1].equals(reportedClusteredExperiments[1])    );
		
		System.out.println("<<<<<<<<<< testClusterizeToProfiles ----------");
	}

	@Test
	public void testClusterize()
	{
		System.out.println("---------- testClusterize >>>>>>>>>>");
		
		Profile[] dati = new Profile[5];
			dati[0] = new Profile("Alfa" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[1] = new Profile("Beta" , new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[2] = new Profile("Gamma", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[3] = new Profile("Delta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
			dati[4] = new Profile("Iota" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
		Experiment source = new Experiment( "Exp_Source", dati );
		
		Experiment clusteredExperiment = source.clusterize(10.0F).setName("Exp_Clustered");
		
		System.out.println(source);
		System.out.println(clusteredExperiment);
		
		Assert.assertEquals(2, clusteredExperiment.getSize());
		
		Profile[] datiClu = new Profile[2];
			datiClu[0] = new Profile("Alfa, Gamma, Iota" , new double[]{1,2,4,5,8,4,2,5,4}, 1);
			datiClu[1] = new Profile("Beta, Delta" , new double[]{5,4,8,7,5,6,3,2,4}, 1);
		Experiment expectedClu = new Experiment( "Exp_expected", datiClu );
		
		System.out.println(expectedClu);
		
		//la disposizione dei nomi varia da esecuzione a esecuzione all'interno del cluster
		//pertanto equals ha in questo ambito un comportamento non prevedibile a tempo di compilazione
		//quindi ci si accontenta della println (comunque testClusterizeToProfiles è già sufficiente)
		
		System.out.println("<<<<<<<<<< testClusterize ----------");
	}

	@Test
	public void testInterpolate()
	{
		System.out.println("---------- testInterpolate >>>>>>>>>>");
		
		Profile[] dati = new Profile[2];
			dati[0] = new Profile("Alfa", new double[]{1,2,4,5,8,4,2,5,4}, 1);
			dati[1] = new Profile("Beta", new double[]{5,4,8,7,5,6,3,2,4}, 1);
		Experiment experiment = new Experiment("Exp_Source", dati);
		
		Experiment experiment_interpolated = experiment.interpolate(100);
		experiment_interpolated.setName("Exp_interpolated");
		
		Profile[] datiExp = new Profile[2];
			datiExp[0] = new Profile("Alfa",  new double[] {1.0,	1.0475032390642727,	1.096319927777269,	1.1477635157877129,	1.2031474527443273,	1.2637851882958366,	1.3309901720909645,	1.4060758537784341,	1.4903556830069695,	1.585143109425294,	1.6917515826821317,	1.8114945524262056,	1.9456854683062403,	2.0954459398218295,	2.2592931546079873,	2.433867336280645,	2.615767270983523,	2.801591744860342,	2.9879395440548207,	3.1714094547106804,	3.3486002629716403,	3.5161107549814203,	3.6705397168837415,	3.8084859348223232,	3.9265481949408847,	4.021349659817674,	4.092463040608585,	4.145189510583137,	4.185488406743049,	4.219319066090041,	4.25264082562583,	4.291413022352138,	4.341594993270686,	4.409146075383189,	4.50002560569137,	4.620192921196948,	4.775607358901642,	4.972228255807172,	5.214128296174154,	5.494362772308586,	5.801999153667195,	6.126099409261397,	6.455725508102606,	6.779939419202245,	7.087803111571727,	7.368378554222468,	7.610727716165885,	7.803912566413396,	7.936995073976415,	7.999037207866361,	7.9795225310208,	7.877631266678722,	7.70224029838055,	7.462648103592852,	7.168153159782194,	6.82805394441515,	6.451648934958282,	6.048236608878168,	5.6271154436413715,	5.197583916714457,	4.768940505564003,	4.350483687656569,	3.9515096422663962,	3.5796483592236306,	3.237926549108107,	2.928582642528174,	2.6538550700921815,	2.415982262408489,	2.217202650085444,	2.0597546637314035,	1.9458767339547167,	1.8778072913637396,	1.8577847665668235,	1.8880475901723213,	1.9708341927885855,	2.1076100929449333,	2.2931136114457153,	2.5186192779262657,	2.7753729956486217,	3.0546206678748127,	3.3476081978668795,	3.6455814888868496,	3.9397864441967645,	4.2214689670586525,	4.481874960734551,	4.712250328486497,	4.9038409735765205,	5.04800738880998,	5.14130054889887,	5.187473593555658,	5.190810169637809,	5.155593924002786,	5.086108503508052,	4.986637555011074,	4.861464725369312,	4.714873661440232,	4.551148010081298,	4.37457141814997,	4.189427532503717,	4.0}, 0.08080808080808081);
			datiExp[1] = new Profile("Beta",  new double[] {5.0,	4.786083395932276,	4.577416219093851,	4.37924789671403,	4.19682785602211,	4.035405524247395,	3.900230328619185,	3.7965516963667794,	3.7296190547194805,	3.70468183090659,	3.726989452157408,	3.8017913457012344,	3.9343369387673723,	4.129238191485548,	4.3824528106416825,	4.683701524919468,	5.022567370109095,	5.388633382000749,	5.771482596384616,	6.160698049050884,	6.5458627757897405,	6.9165598123913705,	7.262372194645963,	7.572882958343706,	7.837675139274782,	8.046371291588617,	8.193375690902112,	8.28237942725248,	8.318140586376286,	8.305417254010102,	8.248967515890495,	8.15354945775403,	8.023921165337278,	7.864840724376807,	7.681066220609182,	7.477355739770972,	7.2584673675987466,	7.029159189829072,	6.794110093261399,	6.55753647061051,	6.323487311589987,	6.0960113750127265,	5.879157419691628,	5.6769742044395795,	5.49351048806948,	5.332815029394223,	5.198936587226704,	5.0959239203798194,	5.027825787666463,	4.998690947899529,	5.012311123595003,	5.066566202439936,	5.153424237292442,	5.264596244713727,	5.391793241264998,	5.526726243507458,	5.661106268002315,	5.786644331310772,	5.8950514499940345,	5.97803864061331,	6.027316919729802,	6.034597303904717,	5.991595768656835,	5.893623533746648,	5.745924610956923,	5.555443934518577,	5.329126438662519,	5.0739170576196715,	4.796760725620944,	4.504602376897255,	4.204386945679515,	3.903059366198645,	3.607564572685557,	3.3248474993711623,	3.061853080486382,	2.8250031617267686,	2.6161667811653286,	2.4348687652906817,	2.280614566941992,	2.152909638958427,	2.051259434179149,	1.975169405443325,	1.9241450055901186,	1.897691687458695,	1.895314903888219,	1.9165201077178555,	1.960812751786769,	2.0276868089767475,	2.116116252618735,	2.2243535194633193,	2.350597898310262,	2.493048677959328,	2.649905147210284,	2.8193665948628897,	2.999632309716913,	3.1889015805721144,	3.3853736962282595,	3.5872479454851143,	3.792723617142439,	4.0}, 0.08080808080808081);
		Experiment experiment_expected = new Experiment( "Exp_interpolated_expected", datiExp );
		
		System.out.println(experiment);
		System.out.println(experiment_interpolated);
		System.out.println(experiment_expected);

		experiment_expected.setAdmittableError(Experiment.DoubleEqualityErrorAdmissibility.BILLIONTH);
		Assert.assertTrue( experiment_expected.equals(experiment_interpolated) );
		
		System.out.println("<<<<<<<<<< testInterpolate ----------");
	}

	@Test
	public void testGetNames()
	{
		System.out.println("---------- testGetNames >>>>>>>>>>");
		
		String[] names = experiment.getProfilesNames();
		String[] names_Expected = new String[] {"Alfa", "Beta", "Gamma", "Delta", "Iota"};
		
		Arrays.sort(names);
		Arrays.sort(names_Expected);
		
		for(int i=0; i<names_Expected.length; i++)
		{
			System.out.println(names_Expected[i] + "<=>" + names[i]);
			Assert.assertEquals(names_Expected[i], names[i]);
		}
		
		System.out.println("<<<<<<<<<< testGetNames ----------");
	}

	@Test
	public void testGetProfiles()
	{
		System.out.println("---------- testGetProfile >>>>>>>>>>");
		
		Profile[] prs = experiment.getProfiles();
		
		Assert.assertEquals(5, prs.length);
		
		System.out.println("<<<<<<<<<< testGetProfile ----------");
	}

	@Test
	public void testGetProfilesStringArray()
	{
		System.out.println("---------- testGetProfilesStringArray >>>>>>>>>>");
		
		String[] names = new String[] {"Alfa", "Beta", "Gamma", "Delta", "Iota"};
		experiment.forceProfileOrder(names);
		
		Profile[] prs = experiment.getProfiles();
		
		for(int i=0; i<prs.length; i++)
		{
			System.out.println(names[i] + "<=>" + prs[i]);
			Assert.assertEquals(names[i], prs[i].getName());
		}
		
		System.out.println("<<<<<<<<<< testGetProfilesStringArray ----------");
	}

	@Test
	public void testToMatrix()
	{
		System.out.println("---------- testToMatrix >>>>>>>>>>");
		
		String[] order = new String[] {"Alfa", "Beta", "Gamma", "Delta", "Iota"};				
		experiment.forceProfileOrder(order);
		Matrix matrix = experiment.toMatrix();
		
		Matrix matrix_expected = new Matrix( new double[][]
		{ {1,2,4,5,8,4,2,5,4},
		  {5,4,8,7,5,6,3,2,4},
		  {1,2,4,5,8,4,2,5,4},
		  {5,4,8,7,5,6,3,2,4},
		  {1,2,4,5,8,4,2,5,4} } );
		
		System.out.println("experiment\n" + experiment);
		System.out.println("m1\n" + matrix);
		System.out.println("m2\n" + matrix_expected);
		Assert.assertTrue(matrix_expected.equals(matrix));
		
		System.out.println("<<<<<<<<<< testToMatrix ----------");
	}
}