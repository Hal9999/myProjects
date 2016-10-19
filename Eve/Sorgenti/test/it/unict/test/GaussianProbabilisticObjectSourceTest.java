package it.unict.test;

import it.unict.eve.GaussianProbabilisticObjectSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GaussianProbabilisticObjectSourceTest
{
	private GaussianProbabilisticObjectSource<String> source;
	
	@Before
	public void setUp() throws Exception
	{
		source = new GaussianProbabilisticObjectSource<String>();
			source.put(0.1, "Alfa");
			source.put(0.2, "Beta");
			source.put(0.3, "Gamma");
			source.put(0.4, "Delta");
			source.put(0.5, "Iota");
	}

	@Test
	public void testPut()
	{
		System.out.println("---------- testPut >>>>>>>>>>");
		
		source.put(0.1, "Omega");
		System.out.println(source);
		Assert.assertEquals(5, source.size());
		
		source.put(1.11, "Chi");
		System.out.println(source);
		Assert.assertEquals(6, source.size());
		
		source.put(-1.11, "Sigma");
		System.out.println(source);
		Assert.assertEquals(6, source.size());
		
		System.out.println("<<<<<<<<<< testPut ----------");
	}

	@Test
	public void testSize()
	{
		System.out.println("---------- testSize >>>>>>>>>>");
		
		Assert.assertEquals(5, source.size());
		System.out.println(source);
		
		source.put(0.1, "Omega");
		System.out.println(source);
		Assert.assertEquals(5, source.size());
		
		source.put(1.11, "Chi");
		System.out.println(source);
		Assert.assertEquals(6, source.size());
		
		System.out.println("<<<<<<<<<< testSize ----------");
	}

	@Test
	public void testToString()
	{
		System.out.println("---------- testToString >>>>>>>>>>");
		
		System.out.println(source);
		Assert.assertEquals("GPOS size = 5, mean = 0.0, variance = 1.0\nfit:0.1\nAlfa\nfit:0.2\nBeta\nfit:0.3\nGamma\nfit:0.4\nDelta\nfit:0.5\nIota",
							source.toString());
		
		System.out.println("<<<<<<<<<< testToString ----------");
	}

	@Test
	public void testGetByProbability()
	{
		System.out.println("---------- testGetByProbability >>>>>>>>>>");
		
		try
		{
			GaussianProbabilisticObjectSource<String> s = new GaussianProbabilisticObjectSource<String>();
			s.getByProbability();
			Assert.fail("Exception not thrown");
		}
		catch(IllegalStateException e) {}
		
		String[] strings = new String[] { "Zero", "ZeroCinque", "Uno", "UnoCinque", "Due", "DueCinque", "Tre", "TreCinque",
										  "Quattro", "QuattroCinque", "Cinque", "CinqueCinque", "Sei", "SeiCinque", "Sette",
										  "SetteCinque", "Otto", "OttoCinque", "Nove" };
		double[] vals = new double[strings.length];
		for(int i=0; i<vals.length; i++) vals[i] = i*0.5;

		source = new GaussianProbabilisticObjectSource<String>();
		for(int i=0; i<strings.length; i++)
			source.put(vals[i], strings[i]);
		
		System.out.println("Before_start\n" + source + "\nBefore_end");
		
		source.setMean(2.0);
		source.setVariance(3.0);
		
		long[] freq = new long[strings.length];
		for(int i=0; i<100000; i++)
		{
			String x = source.getByProbability();

			for(int j=0; j<strings.length; j++)
				if( x.equals(strings[j]) )
				{ freq[j]++; break; }
		}
		
		for(int i=0; i<strings.length; i++)
			System.out.println( freq[i] + "\t:" + strings[i]);
		
		System.out.println("<<<<<<<<<< testGetByProbability ----------");
	}

//	@Test
//	public void testGetFirst()
//	{
//		System.out.println("---------- testGetFirst >>>>>>>>>>");
//
//		ArrayList<Entry<Double, String>> firsts = source.getFirst(3);
//		Assert.assertEquals(3, firsts.size());
//		
//		Assert.assertEquals("Alfa" , firsts.get(0).getValue());
//		Assert.assertEquals("Beta" , firsts.get(1).getValue());
//		Assert.assertEquals("Gamma", firsts.get(2).getValue());
//		
//		System.out.println("<<<<<<<<<< testGetFirst ----------");
//	}
//
//	@Test
//	public void testRetainFirst()
//	{
//		System.out.println("---------- testRetainFirst >>>>>>>>>>");
//
//		System.out.println("Before\n" + source);
//		
//		ArrayList<Map.Entry<Double, String>> deleted = source.retainFirsts(3);
//		
//		System.out.println("After\n" + source);
//		
//		Assert.assertEquals(2, deleted.size());
//		
//		String[] deletedExpected = new String[] {"Delta", "Iota"};
//		
//		String[] returned = new String[deleted.size()];
//		for(int i=0; i<deleted.size(); i++)
//			returned[i] = deleted.get(i).getValue();
//		
//		Arrays.sort(returned);
//		Arrays.sort(deletedExpected);
//		
//		for(int i = 0; i < deletedExpected.length; i++)
//			Assert.assertEquals(deletedExpected[i], returned[i]);
//
//		System.out.println("<<<<<<<<<< testRetainFirst ----------");
//	}
}