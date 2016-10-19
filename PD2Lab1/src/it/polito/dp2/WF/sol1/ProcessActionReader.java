package it.polito.dp2.WF.sol1;

import java.util.regex.Pattern;

import org.w3c.dom.Element;

import it.polito.dp2.WF.WorkflowMonitorException;

public class ProcessActionReader implements it.polito.dp2.WF.ActionReader, it.polito.dp2.WF.ProcessActionReader
{
	private static final Pattern workspaceNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
	private static final Pattern rolePattern = Pattern.compile("^[a-zA-Z0-9]+$");
	
	private String workflowName;
	private String role;
	private boolean isAutomaticallyInstantiated;
	private WorkflowReader enclosingWorkflow;
	private WorkflowReader actionWorkflow;
	
	ProcessActionReader(Element node, WorkflowReader parentWorkflow) throws WorkflowMonitorException
	{
		enclosingWorkflow = parentWorkflow;
		
		workflowName = node.getElementsByTagName("workflowName").item(0).getFirstChild().getNodeValue().trim();
		if(!workspaceNamePattern.matcher(workflowName).matches()) throw new WorkflowMonitorException("Invalid workflow name: " + workflowName);
		
		role = node.getElementsByTagName("role").item(0).getFirstChild().getNodeValue().trim();
		if(!rolePattern.matcher(role).matches()) throw new WorkflowMonitorException("Invalid role name: " + role);
		
		isAutomaticallyInstantiated = node.getAttribute("autoInstance").equals("yes") ? true : false;
	}
	
	void consolidate() throws WorkflowMonitorException
	{
		if((actionWorkflow = enclosingWorkflow.parentMonitor.getWorkflow(workflowName)) == null)
			throw new WorkflowMonitorException("Cannot find referred workflow by processAction: " + workflowName);
	}

	@Override
	public String getName() { return workflowName; }

	@Override
	public String getRole() { return role; }

	@Override
	public boolean isAutomaticallyInstantiated() { return isAutomaticallyInstantiated; }

	@Override
	public WorkflowReader getEnclosingWorkflow() { return enclosingWorkflow; }

	@Override
	public WorkflowReader getActionWorkflow() { return actionWorkflow; }
}
