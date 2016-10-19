package it.unict.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses
({
	EnvironmentTest.class,
	MatrixTest.class,
	ODEModelTest.class,
	ODEModelMutatorsTest.class,
	ProfileTest.class,
	ExperimentTest.class,
	GaussianProbabilisticObjectSourceTest.class,
	UniformProbabilisticObjectSourceTest.class
})
public class AllTests{}
