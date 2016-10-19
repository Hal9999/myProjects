package it.unict.test;

import java.util.Random;

import it.unict.eve.*;

import org.junit.Before;
import org.junit.Test;

public class ODEModelMutatorsTest
{
	private ProbabilisticObjectSource<ODEModel> source;
	
	@Before
	public void setUp()
	{
		source = new UniformProbabilisticObjectSource<ODEModel>();
		
		source.put( 1.0, new ODEModel( new Matrix(4).fill(0) ) );
		source.put( 1.0, new ODEModel( new Matrix(4).fill(1) ) );
	}
	
	@Test
	public void testAllMutators()
	{
		System.out.println("---------- testAllMutators >>>>>>>>>>");
		
		ProbabilisticObjectSource<ODEModel> source = new UniformProbabilisticObjectSource<ODEModel>();
		ProbabilisticObjectSource<ODEModelMutator> mutators = new UniformProbabilisticObjectSource<ODEModelMutator>();
		
		mutators.put(1.0, new BiphasicEteroODEModelMutator(source));
		mutators.put(1.0, new DiffEteroODEModelMutator(source));
		mutators.put(1.0, new LimitedDivisionEteroODEModelMutator(source));
		mutators.put(1.0, new MeanEteroODEModelMutator(source));
		mutators.put(1.0, new ProductEteroODEModelMutator(source));
		mutators.put(1.0, new RandScatteredEteroODEModelMutator(source));
		mutators.put(1.0, new SumEteroODEModelMutator(source));
		mutators.put(1.0, new AbsSingleODEModelMutator(source));
		mutators.put(1.0, new NewRandomODEModelMutator(source));
		mutators.put(1.0, new RandMutationSingleODEModelMutator(source, 0.2));
		mutators.put(1.0, new RandSignSingleODEModelMutator(source));
		mutators.put(1.0, new SignSingleODEModelMutator(source));
		mutators.put(1.0, new SignSquareSingleODEModelMutator(source));
		mutators.put(1.0, new SQRTSingleODEModelMutator(source));
		mutators.put(1.0, new SquareSingleODEModelMutator(source));
		mutators.put(1.0, new TransposeSingleODEModelMutator(source));
		
		source.put(1.0, new ODEModel(4));
		
		for(int i=0; i<1000; i++) source.put(1.0, mutators.getByProbability().evolve() );
		
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testAllMutators ----------");
	}
	
	@Test
	public void testBiphasicEteroODEModelMutator()
	{
		System.out.println("---------- testBiphasicEteroODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new BiphasicEteroODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testBiphasicEteroODEModelMutator ----------");
	}
	
	@Test
	public void testDiffEteroODEModelMutator()
	{
		System.out.println("---------- testDiffEteroODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new DiffEteroODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testDiffEteroODEModelMutator ----------");
	}
	
	@Test
	public void testDivisionEteroODEModelMutator()
	{
		System.out.println("---------- testDivisionEteroODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new LimitedDivisionEteroODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testDivisionEteroODEModelMutator ----------");
	}
	
	@Test
	public void testMeanEteroODEModelMutator()
	{
		System.out.println("---------- testMeanEteroODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new MeanEteroODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testMeanEteroODEModelMutator ----------");
	}
	
	@Test
	public void testProductEteroODEModelMutator()
	{
		System.out.println("---------- testProductEteroODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new ProductEteroODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testProductEteroODEModelMutator ----------");
	}
	
	@Test
	public void testRandScatteredEteroODEModelMutator()
	{
		System.out.println("---------- testRandScatteredEteroODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new RandScatteredEteroODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testRandScatteredEteroODEModelMutator ----------");
	}
	
	@Test
	public void testSumEteroODEModelMutator()
	{
		System.out.println("---------- testSumEteroODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new SumEteroODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testSumEteroODEModelMutator ----------");
	}
	
	@Test
	public void testAbsSingleODEModelMutator()
	{
		System.out.println("---------- testAbsSingleODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new AbsSingleODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testAbsSingleODEModelMutator ----------");
	}
	
	@Test
	public void testNewRandomSingleODEModelMutator()
	{
		System.out.println("---------- testNewRandomSingleODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new NewRandomODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testNewRandomSingleODEModelMutator ----------");
	}
	
	@Test
	public void testRandMutationSingleODEModelMutator()
	{
		System.out.println("---------- testRandMutationSingleODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new RandMutationSingleODEModelMutator(source, 0.2);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testRandMutationSingleODEModelMutator ----------");
	}
	
	@Test
	public void testRandSignSingleODEModelMutator()
	{
		System.out.println("---------- testRandSignSingleODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new RandSignSingleODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testRandSignSingleODEModelMutator ----------");
	}
	
	@Test
	public void testSignSingleODEModelMutator()
	{
		System.out.println("---------- testSignSingleODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new SignSingleODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testSignSingleODEModelMutator ----------");
	}
	
	@Test
	public void testSignSquareSingleODEModelMutator()
	{
		System.out.println("---------- testSignSquareSingleODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new SignSquareSingleODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testSignSquareSingleODEModelMutator ----------");
	}
	
	@Test
	public void testSQRTSingleODEModelMutator()
	{
		System.out.println("---------- testSQRTSingleODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new SQRTSingleODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testSQRTSingleODEModelMutator ----------");
	}
	
	@Test
	public void testSquareSingleODEModelMutator()
	{
		System.out.println("---------- testSquareSingleODEModelMutator >>>>>>>>>>");
		
		ODEModelMutator gen = new SquareSingleODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testSquareSingleODEModelMutator ----------");
	}
	
	@Test
	public void testTransposeSingleODEModelMutator()
	{
		System.out.println("---------- testTransposeSingleODEModelMutator >>>>>>>>>>");
		
		source = new UniformProbabilisticObjectSource<ODEModel>();
		source.put(1.0, new ODEModel(new Matrix(4).fill(new Random())));
		ODEModelMutator gen = new TransposeSingleODEModelMutator(source);
		for(int i=0; i<100; i++) source.put(1.0, gen.evolve() );
		System.out.println(source);
		
		System.out.println("<<<<<<<<<< testTransposeSingleODEModelMutator ----------");
	}
}