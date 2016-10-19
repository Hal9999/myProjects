package it.polito.dp2.WF.sol1;

import java.util.*;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.polito.dp2.WF.ActionReader;
import it.polito.dp2.WF.ProcessReader;
import it.polito.dp2.WF.WorkflowMonitorException;

public class WorkflowReader implements it.polito.dp2.WF.WorkflowReader
{
	private static final Pattern workspaceNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
	
	WorkflowInfoMonitor parentMonitor;
	private String name;
	private Map<String, ActionReader> actionReaders = new TreeMap<String, ActionReader>();
	private Set<ProcessReader> processesSet = new LinkedHashSet<ProcessReader>();
	
	WorkflowReader(Element node, WorkflowInfoMonitor parentMonitor) throws WorkflowMonitorException
	{
		this.parentMonitor = parentMonitor;
		
		name = node.getElementsByTagName("name").item(0).getFirstChild().getNodeValue().trim();
		if(!workspaceNamePattern.matcher(name).matches()) throw new WorkflowMonitorException("Invalid workflow name: " + name);
		
		NodeList simpleActionNodes = node.getElementsByTagName("simpleAction");
		for(int i=0; i<simpleActionNodes.getLength(); i++)
		{
			SimpleActionReader action = new SimpleActionReader((Element)simpleActionNodes.item(i), this);
			if(actionReaders.containsKey(action.getName())) throw new WorkflowMonitorException("Duplicated simple action name: " + action.getName());
			actionReaders.put(action.getName(), action);
		}
		
		NodeList processActionNodes = node.getElementsByTagName("processAction");
		for(int i=0; i<processActionNodes.getLength(); i++)
		{
			ProcessActionReader action = new ProcessActionReader((Element)processActionNodes.item(i), this);
			if(actionReaders.containsKey(action.getName())) throw new WorkflowMonitorException("Duplicated process action name: " + action.getName());
			actionReaders.put(action.getName(), action);
		}
	}
	
	void consolidateWorkflows() throws WorkflowMonitorException
	{
		for(ActionReader actionReader : actionReaders.values())
			if(actionReader instanceof SimpleActionReader) ((SimpleActionReader)actionReader).consolidate();
			else ((ProcessActionReader)actionReader).consolidate();
	}

	void consolidateProcesses()
	{
		for(ProcessReader processReader : parentMonitor.getProcesses())
			if(processReader.getWorkflow().getName().equals(name)) processesSet.add(processReader);
	}

	@Override
	public String getName() { return name; }

	@Override
	public ActionReader getAction(String actionName) { return actionReaders.get(actionName); }

	@Override
	public Set<ActionReader> getActions() { return new HashSet<ActionReader>(actionReaders.values()); }

	@Override
	public Set<ProcessReader> getProcesses() { return processesSet; }
}
