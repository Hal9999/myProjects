package it.polito.dp2.WF.lab1.tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import it.polito.dp2.WF.WorkflowMonitor;
import it.polito.dp2.WF.WorkflowMonitorException;
import it.polito.dp2.WF.WorkflowMonitorFactory;

public class WFTests3
{
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	
	@BeforeClass
	public static final void setUpBeforeClass()
	{
		System.setProperty("it.polito.dp2.WF.WorkflowMonitorFactory", "it.polito.dp2.WF.sol1.WorkflowMonitorFactory");
	}
	
	@Test
	public final void testGetWorkflows00() throws WorkflowMonitorException
	{
    	System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test00.xml");
        WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows01() throws WorkflowMonitorException
	{
    	System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test01.xml");
    	exception.expect(WorkflowMonitorException.class);
    	exception.expectMessage("Process referenced workflow not found: PrepaidSaleX");
        WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows02() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test02.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("endDate element not present despite action is terminated: Revising");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows03() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test03.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("actor not present despite action in taken in charge: Checking");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows04() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test04.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("Invalid actor name: Andrew Elkan 44");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows05() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test05.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("Illegal presence of endDate element although action is NOT Terminated");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows06() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test06.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("ActionStatus action not found in the workflow: Checkingo");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}

	@Test
	public final void testGetWorkflows07() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test07.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("java.text.ParseException: Unparseable date: \"05/10/2015 16:49:40 CiEST\"");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows08() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test08.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("Invalid role name: ");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows09() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test09.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("Duplicated workflow name: ArticleProduction");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows10() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test10.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("Invalid nextAction name: Payment Reception");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows11() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test11.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("Invalid actor name: Andrea _Menegai");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows12() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test12.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("Duplicated simple action name: Publication");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows13() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test13.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("Next possible action not found in the workflow: Publicationz");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows14() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test14.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("Next possible action not found in the workflow: PrepaidSale");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows15() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test15.xml");
		exception.expect(WorkflowMonitorException.class);
		exception.expectMessage("Duplicated next possible action: Revising");
		WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
	}
	
	@Test
	public final void testGetWorkflows99() throws WorkflowMonitorException
	{
		System.setProperty("it.polito.dp2.WF.sol1.WFInfo.file", "tests\\test00.xml");
		WorkflowMonitor monitor = WorkflowMonitorFactory.newInstance().newWorkflowMonitor();
		
		assertNotNull(monitor.getWorkflow("NormalSale"));
		assertNull(monitor.getWorkflow("zNormalSale"));
		assertFalse(monitor.getProcesses().isEmpty());
		assertEquals("Accountingman", monitor.getWorkflow("PrepaidSale").getAction("PaymentReception").getRole());
		assertNull(monitor.getWorkflow("PrepaidSale").getAction("PaymentReceptionX"));
		assertEquals(4, monitor.getWorkflows().size());
		assertEquals(13, monitor.getProcesses().size());
	}
}
