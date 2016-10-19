package it.polito.dp2.WF.sol1;

import java.io.*;

import it.polito.dp2.WF.*;

public class WorkflowMonitorFactory extends it.polito.dp2.WF.WorkflowMonitorFactory 
{
	@Override
	public WorkflowMonitor newWorkflowMonitor() throws WorkflowMonitorException
	{
		return newWorkflowMonitor(new File(System.getProperty("it.polito.dp2.WF.sol1.WFInfo.file")));
	}

	public WorkflowMonitor newWorkflowMonitor(File xmlFile) throws WorkflowMonitorException
	{
		return new WorkflowInfoMonitor(xmlFile);
	}
}