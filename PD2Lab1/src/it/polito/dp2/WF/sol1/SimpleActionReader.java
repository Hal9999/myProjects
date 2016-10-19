package it.polito.dp2.WF.sol1;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.polito.dp2.WF.ActionReader;
import it.polito.dp2.WF.WorkflowMonitorException;
import it.polito.dp2.WF.WorkflowReader;

public class SimpleActionReader implements it.polito.dp2.WF.ActionReader, it.polito.dp2.WF.SimpleActionReader
{
	private static final Pattern actionNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
	private static final Pattern rolePattern = Pattern.compile("^[a-zA-Z0-9]+$");
	
	private String name;
	private String role;
	private boolean isAutomaticallyInstantiated;
	private WorkflowReader enclosingWorkflow;
	private String[] nextPossibleActionsNames;
	private Set<it.polito.dp2.WF.ActionReader> nextPossibleActionsSet = new LinkedHashSet<it.polito.dp2.WF.ActionReader>();
	
	SimpleActionReader(Element node, WorkflowReader parentWorkflow) throws WorkflowMonitorException
	{
		enclosingWorkflow = parentWorkflow;
		
		name = node.getElementsByTagName("name").item(0).getFirstChild().getNodeValue().trim();
		if(!actionNamePattern.matcher(name).matches()) throw new WorkflowMonitorException("Invalid simpleAction name: " + name);
		
		role = node.getElementsByTagName("role").item(0).getFirstChild().getNodeValue().trim();
		if(!rolePattern.matcher(role).matches()) throw new WorkflowMonitorException("Invalid role name: " + role);
		
		isAutomaticallyInstantiated = node.getAttribute("autoInstance").equals("yes") ? true : false;
		
		NodeList nextActions = node.getElementsByTagName("nextAction");
		nextPossibleActionsNames = new String[nextActions.getLength()];
		for (int i=0; i<nextActions.getLength(); i++)
		{
			nextPossibleActionsNames[i] = nextActions.item(i).getFirstChild().getNodeValue().trim();
			if(!actionNamePattern.matcher(nextPossibleActionsNames[i]).matches()) throw new WorkflowMonitorException("Invalid nextAction name: " + nextPossibleActionsNames[i]);
		}
	}
	
	void consolidate() throws WorkflowMonitorException
	{
		ActionReader actionReader;
		for (int i=0; i<nextPossibleActionsNames.length; i++)
		{
			if((actionReader = enclosingWorkflow.getAction(nextPossibleActionsNames[i])) == null) throw new WorkflowMonitorException("Next possible action not found in the workflow: " + nextPossibleActionsNames[i]);
			if(nextPossibleActionsSet.contains(actionReader)) throw new WorkflowMonitorException("Duplicated next possible action: " + nextPossibleActionsNames[i]);
			nextPossibleActionsSet.add(actionReader);
		}
	}
	
	@Override
	public String getName() { return name; }

	@Override
	public String getRole() { return role; }

	@Override
	public boolean isAutomaticallyInstantiated() { return isAutomaticallyInstantiated; }

	@Override
	public WorkflowReader getEnclosingWorkflow() { return enclosingWorkflow; }

	@Override
	public Set<it.polito.dp2.WF.ActionReader> getPossibleNextActions() { return nextPossibleActionsSet; }
}
