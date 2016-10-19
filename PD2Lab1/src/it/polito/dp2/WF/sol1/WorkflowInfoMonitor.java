package it.polito.dp2.WF.sol1;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import it.polito.dp2.WF.*;

public class WorkflowInfoMonitor implements WorkflowMonitor
{
	private Map<String, WorkflowReader> workflowsMap = new HashMap<String, WorkflowReader>();
	private Set<it.polito.dp2.WF.ProcessReader> processesSet = new LinkedHashSet<it.polito.dp2.WF.ProcessReader>();
	
	WorkflowInfoMonitor(File xmlFile) throws WorkflowMonitorException
	{
		try
		{
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			documentFactory.setValidating(true);
			//TODO: JAXP non lancia eccezioni in caso di errori di validazione? allora devo usare un gestore delle eccezioni custom in questo caso?
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(xmlFile);
			
			NodeList workflowNodes = document.getElementsByTagName("workflow");
			if(workflowNodes.getLength() == 0) return;
			
			for(int i = 0; i<workflowNodes.getLength(); i++)
			{
				WorkflowReader newWorkflow = new WorkflowReader((Element)workflowNodes.item(i), this);
				if(workflowsMap.containsKey(newWorkflow.getName())) throw new WorkflowMonitorException("Duplicated workflow name: " + newWorkflow.getName());
				workflowsMap.put(newWorkflow.getName(), newWorkflow);
			}
			
			NodeList processNodes = document.getElementsByTagName("process");
			for(int i=0; i<processNodes.getLength(); i++)
			{
				ProcessReader newProcess = new ProcessReader((Element)processNodes.item(i), this);
				processesSet.add(newProcess);
			}
		}
		catch(IOException e) { throw new WorkflowMonitorException(e); }
		catch(ParserConfigurationException e) { throw new WorkflowMonitorException(e); }
		catch(SAXException e) { throw new WorkflowMonitorException(e); }
		
		for (WorkflowReader workflowReader : workflowsMap.values()) workflowReader.consolidateWorkflows();
		for (it.polito.dp2.WF.ProcessReader processReader : processesSet) ((ProcessReader)processReader).consolidate();
		for (WorkflowReader workflowReader : workflowsMap.values()) workflowReader.consolidateProcesses();
	}
	
	@Override
	public Set<it.polito.dp2.WF.ProcessReader> getProcesses() { return processesSet; }

	@Override
	public Set<it.polito.dp2.WF.WorkflowReader> getWorkflows() { return new HashSet<it.polito.dp2.WF.WorkflowReader>(workflowsMap.values()); }

	@Override
	public WorkflowReader getWorkflow(String name) { return workflowsMap.get(name); }
}
