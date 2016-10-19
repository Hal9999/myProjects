package it.polito.dp2.WF.sol1;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import it.polito.dp2.WF.*;

public class WorkflowMonitorFactoryV2 extends it.polito.dp2.WF.WorkflowMonitorFactory 
{
	@Override
	public WorkflowMonitor newWorkflowMonitor() throws WorkflowMonitorException
	{
		return newWorkflowMonitor(new File(System.getProperty("it.polito.dp2.WF.sol2.WorkflowInfo.file")));
	}

	public WorkflowMonitor newWorkflowMonitor(File xmlFile) throws WorkflowMonitorException
	{
		return new WorkflowInfoMonitor(xmlFile);
	}
	
	public static class WorkflowInfoMonitor implements WorkflowMonitor
	{
		private Map<String, WorkflowReader> workflowsMap = new HashMap<String, WorkflowReader>();
		private Set<it.polito.dp2.WF.ProcessReader> processesSet = new LinkedHashSet<it.polito.dp2.WF.ProcessReader>();
		
		private WorkflowInfoMonitor(File xmlFile) throws WorkflowMonitorException
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
	
	public static class WorkflowReader implements it.polito.dp2.WF.WorkflowReader
	{
		private static final Pattern workspaceNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
		
		private WorkflowInfoMonitor parentMonitor;
		private String name;
		private Map<String, ActionReader> actionReaders = new TreeMap<String, ActionReader>();
		private Set<it.polito.dp2.WF.ProcessReader> processesSet = new LinkedHashSet<it.polito.dp2.WF.ProcessReader>();
		
		private WorkflowReader(Element node, WorkflowInfoMonitor parentMonitor) throws WorkflowMonitorException
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
		
		private void consolidateWorkflows() throws WorkflowMonitorException
		{
			for(ActionReader actionReader : actionReaders.values())
				if(actionReader instanceof SimpleActionReader) ((SimpleActionReader)actionReader).consolidate();
				else ((ProcessActionReader)actionReader).consolidate();
		}

		private void consolidateProcesses()
		{
			for(it.polito.dp2.WF.ProcessReader processReader : parentMonitor.getProcesses())
				if(processReader.getWorkflow().getName().equals(name)) processesSet.add((ProcessReader) processReader);
		}

		@Override
		public String getName() { return name; }

		@Override
		public ActionReader getAction(String actionName) { return actionReaders.get(actionName); }

		@Override
		public Set<ActionReader> getActions() { return new HashSet<ActionReader>(actionReaders.values()); }

		@Override
		public Set<it.polito.dp2.WF.ProcessReader> getProcesses() { return processesSet; }
	}
	
	public static class ProcessReader implements it.polito.dp2.WF.ProcessReader
	{
		private static final Pattern workspaceNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
		private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");    
	    static { dateFormat.setLenient(false); }
	    
	    private WorkflowInfoMonitor parentMonitor;
	    private String workflowName;
		private Calendar startDate = new GregorianCalendar();
		private List<it.polito.dp2.WF.ActionStatusReader> status = new LinkedList<it.polito.dp2.WF.ActionStatusReader>();
		private WorkflowReader workflowAffinity;
		
		private ProcessReader(Element element, WorkflowInfoMonitor parentMonitor) throws WorkflowMonitorException
		{
			this.parentMonitor = parentMonitor;
			
			workflowName = element.getElementsByTagName("workflowName").item(0).getFirstChild().getNodeValue().trim();
			if(!workspaceNamePattern.matcher(workflowName).matches()) throw new WorkflowMonitorException("Invalid workflow name of process: " + workflowName);
			
			String startDateString = element.getElementsByTagName("startDate").item(0).getFirstChild().getNodeValue().trim();
			try { startDate.setTime(dateFormat.parse(startDateString)); }
			catch (ParseException e) { throw new WorkflowMonitorException(e); }
			
			NodeList actionStatesNodes = element.getElementsByTagName("actionStatus");
			for(int i=0; i<actionStatesNodes.getLength(); i++)
			{
				ActionStatusReader newActionStatus = new ActionStatusReader((Element)actionStatesNodes.item(i), this);
				status.add(newActionStatus);
			}
		}
		
		private void consolidate() throws WorkflowMonitorException
		{
			if( (workflowAffinity = parentMonitor.getWorkflow(workflowName)) == null)
				throw new WorkflowMonitorException("Process referenced workflow not found: " + workflowName);
			
			for (it.polito.dp2.WF.ActionStatusReader actionStatusReader : status)
				((ActionStatusReader) actionStatusReader).consolidate();
		}

		@Override
		public Calendar getStartTime() { return startDate; }

		@Override
		public List<it.polito.dp2.WF.ActionStatusReader> getStatus() { return status; }

		@Override
		public WorkflowReader getWorkflow() { return workflowAffinity; }
	}
	
	public static class SimpleActionReader implements it.polito.dp2.WF.ActionReader, it.polito.dp2.WF.SimpleActionReader
	{
		private static final Pattern actionNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
		private static final Pattern rolePattern = Pattern.compile("^[a-zA-Z0-9]+$");
		
		private String name;
		private String role;
		private boolean isAutomaticallyInstantiated;
		private WorkflowReader enclosingWorkflow;
		private String[] nextPossibleActionsNames;
		private Set<it.polito.dp2.WF.ActionReader> nextPossibleActionsSet = new LinkedHashSet<it.polito.dp2.WF.ActionReader>();
		
		private SimpleActionReader(Element node, WorkflowReader parentWorkflow) throws WorkflowMonitorException
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
		
		private void consolidate() throws WorkflowMonitorException
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

	public static class ProcessActionReader implements it.polito.dp2.WF.ActionReader, it.polito.dp2.WF.ProcessActionReader
	{
		private static final Pattern workspaceNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
		private static final Pattern rolePattern = Pattern.compile("^[a-zA-Z0-9]+$");
		
		private String workflowName;
		private String role;
		private boolean isAutomaticallyInstantiated;
		private WorkflowReader enclosingWorkflow;
		private WorkflowReader actionWorkflow;
		
		private ProcessActionReader(Element node, WorkflowReader parentWorkflow) throws WorkflowMonitorException
		{
			enclosingWorkflow = parentWorkflow;
			
			workflowName = node.getElementsByTagName("workflowName").item(0).getFirstChild().getNodeValue().trim();
			if(!workspaceNamePattern.matcher(workflowName).matches()) throw new WorkflowMonitorException("Invalid workflow name: " + workflowName);
			
			role = node.getElementsByTagName("role").item(0).getFirstChild().getNodeValue().trim();
			if(!rolePattern.matcher(role).matches()) throw new WorkflowMonitorException("Invalid role name: " + role);
			
			isAutomaticallyInstantiated = node.getAttribute("autoInstance").equals("yes") ? true : false;
		}
		
		private void consolidate() throws WorkflowMonitorException
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
	
	public static class ActionStatusReader implements it.polito.dp2.WF.ActionStatusReader
	{
		private static final Pattern actionNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
		private static final Pattern actorNamePattern = Pattern.compile("^[A-Z][ 'a-zA-Z]+$");
		private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");    
	    static { dateFormat.setLenient(false); }
	    
	    private String actionName;
		private boolean isTakenInCharge;
		private boolean isTerminated;
		private ProcessReader parentProcess; //torna utile per fare le verifiche di correttezza
		private GregorianCalendar endDate = null;
		private String actorName = null;
		private Actor actor = null;

		private ActionStatusReader(Element element, ProcessReader parentProcess) throws WorkflowMonitorException
		{
			this.parentProcess = parentProcess;
			
			actionName = element.getElementsByTagName("name").item(0).getFirstChild().getNodeValue().trim();
			if(!actionNamePattern.matcher(actionName).matches()) throw new WorkflowMonitorException("Invalid actionStatus action name: " + actionName);
			
			if(isTerminated = element.getAttribute("terminated").equals("yes") ? true : false)
			{
				NodeList endDateNode = element.getElementsByTagName("endDate");
				if(endDateNode.getLength()!=1) throw new WorkflowMonitorException("endDate element not present despite action is terminated: " + actionName);
				endDate = new GregorianCalendar();
				try { endDate.setTime(dateFormat.parse(endDateNode.item(0).getFirstChild().getNodeValue().trim())); }
				catch (ParseException e) { throw new WorkflowMonitorException(e); }
			}
			else if(element.getElementsByTagName("endDate").getLength()!=0) throw new WorkflowMonitorException("Illegal presence of endDate element although action is NOT Terminated");
			
			if(isTakenInCharge = element.getAttribute("taken").equals("yes") ? true : false)
			{
				NodeList actorNameNode = element.getElementsByTagName("actor");
				if(actorNameNode.getLength()!=1) throw new WorkflowMonitorException("actor not present despite action in taken in charge: " + actionName);
				actorName = actorNameNode.item(0).getFirstChild().getNodeValue().trim();
				if(!actorNamePattern.matcher(actorName).matches()) throw new WorkflowMonitorException("Invalid actor name: " + actorName);
			}
			else if(element.getElementsByTagName("actor").getLength()!=0) throw new WorkflowMonitorException("Illegal presence of actor element although action is NOT Taken In Charge");
		}

		private void consolidate() throws WorkflowMonitorException
		{
			ActionReader action = parentProcess.getWorkflow().getAction(actionName);
			if(action != null) actor = new Actor(actorName, action.getRole());
			else throw new WorkflowMonitorException("ActionStatus action not found in the workflow: " + actionName);
		}

		@Override
		public String getActionName() { return actionName; }

		@Override
		public Actor getActor() { return actor; }

		@Override
		public boolean isTakenInCharge() { return isTakenInCharge; }

		@Override
		public boolean isTerminated() { return isTerminated; }

		@Override
		public Calendar getTerminationTime(){ return endDate; }
	}
}