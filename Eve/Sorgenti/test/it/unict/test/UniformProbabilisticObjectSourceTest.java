package it.unict.test;

import it.unict.eve.UniformProbabilisticObjectSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UniformProbabilisticObjectSourceTest
{
	private UniformProbabilisticObjectSource<String> source;
	
	@Before
	public void setUp() throws Exception
	{
		source = new UniformProbabilisticObjectSource<String>();
			source.put(1.0, "Alfa");
			source.put(2.0, "Beta");
			source.put(1.0, "Gamma");
			source.put(4.0, "Delta");
			source.put(2.0, "Iota");
	}

	@Test
	public void testPut()
	{
		System.out.println("---------- testPut >>>>>>>>>>");
		
		System.out.println(source);
		Assert.assertEquals(5, source.size());
		
		source.put(0.1, "Omega");
		System.out.println(source);
		Assert.assertEquals(6, source.size());
		
		source.put(1.11, "Chi");
		System.out.println(source);
		Assert.assertEquals(7, source.size());
		
		try
		{
			source.put(-1.11, "Sigma");
			Assert.fail("Exception not thrown");
		}
		catch(IllegalArgumentException e) {}
		
		System.out.println(source);
		Assert.assertEquals(7, source.size());
		
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
		Assert.assertEquals(6, source.size());
		
		source.put(1.11, "Chi");
		System.out.println(source);
		Assert.assertEquals(7, source.size());
		
		System.out.println("<<<<<<<<<< testSize ----------");
	}
	
	@Test
	public void testToString()
	{
		System.out.println("---------- testToString >>>>>>>>>>");
		
		System.out.println(source);
		Assert.assertEquals("UPOS size = 5\nfit:0.0\nAlfa\nfit:1.0\nBeta\nfit:3.0\nGamma\nfit:4.0\nDelta\nfit:8.0\nIota",
							source.toString());
		
		System.out.println("<<<<<<<<<< testToString ----------");
	}
	
	@Test
	public void testGetByProbability()
	{
		System.out.println("---------- testGetByProbability >>>>>>>>>>");
		
		try
		{
			UniformProbabilisticObjectSource<String> s = new UniformProbabilisticObjectSource<String>();
			s.getByProbability();
			Assert.fail("Exception not thrown");
		}
		catch(IllegalStateException e) {}
		
		long[] freq = new long[5];
		for(int i=0; i<1000000; i++)
		{
			String x = source.getByProbability();
				 if( x.equals("Alfa" ) ) freq[0]++;
			else if( x.equals("Beta" ) ) freq[1]++;
			else if( x.equals("Gamma") ) freq[2]++;
			else if( x.equals("Delta") ) freq[3]++;
			else if( x.equals("Iota" ) ) freq[4]++;
		}
		System.out.println("Alfa  -->" + freq[0]);
		System.out.println("Beta  -->" + freq[1]);
		System.out.println("Gamma -->" + freq[2]);
		System.out.println("Delta -->" + freq[3]);
		System.out.println("Iota  -->" + freq[4]);
		
		System.out.println("<<<<<<<<<< testGetByProbability ----------");
	}
}