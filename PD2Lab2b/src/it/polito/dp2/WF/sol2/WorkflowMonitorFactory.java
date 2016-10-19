package it.polito.dp2.WF.sol2;

import java.io.*;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.WF.*;
import it.polito.dp2.WF.sol2.jaxb.Workflows;
import it.polito.dp2.WF.sol2.jaxb.Workflows.Process;
import it.polito.dp2.WF.sol2.jaxb.Workflows.Process.ActionStatus;
import it.polito.dp2.WF.sol2.jaxb.Workflows.Workflow;
import it.polito.dp2.WF.sol2.jaxb.Workflows.Workflow.Action;

public class WorkflowMonitorFactory extends it.polito.dp2.WF.WorkflowMonitorFactory 
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
		private Map<String, it.polito.dp2.WF.WorkflowReader> workflowsMap = new HashMap<String, it.polito.dp2.WF.WorkflowReader>();
		private Set<it.polito.dp2.WF.ProcessReader> processesSet = new LinkedHashSet<it.polito.dp2.WF.ProcessReader>();

		private WorkflowInfoMonitor(File xmlFile) throws WorkflowMonitorException
		{
			Unmarshaller unmarshaller;
			try
			{
				JAXBContext jaxbContext = JAXBContext.newInstance("it.polito.dp2.WF.sol2.jaxb");
				unmarshaller = jaxbContext.createUnmarshaller();
				
				//TODO: va bene il try-catch esterno oppure ce ne vuole uno annidato solo per lo Schema?
				SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = schemaFactory.newSchema(new File("xsd/WFInfo.xsd"));
				unmarshaller.setSchema(schema);
				
				Workflows root = (Workflows) unmarshaller.unmarshal(xmlFile);
				
				if(root.getWorkflow().isEmpty()) return;
				
				for(Workflow workflow : root.getWorkflow())
				{
					WorkflowReader workflowReader = new WorkflowReader(workflow, this);
					workflowsMap.put(workflowReader.getName(), workflowReader);
				}
				
				for(it.polito.dp2.WF.sol2.jaxb.Workflows.Process process : root.getProcess())
				{
					ProcessReader processReader = new ProcessReader(process, this);
					processesSet.add(processReader);
				}
			}
			catch(SAXException e) { throw new WorkflowMonitorException(e); }
			catch(JAXBException e) { throw new WorkflowMonitorException(e); }
			
			for (it.polito.dp2.WF.WorkflowReader workflowReader : workflowsMap.values()) ((WorkflowReader) workflowReader).consolidateWorkflows();
			for (it.polito.dp2.WF.ProcessReader processReader : processesSet) ((ProcessReader)processReader).consolidate();
			for (it.polito.dp2.WF.WorkflowReader workflowReader : workflowsMap.values()) ((WorkflowReader) workflowReader).consolidateProcesses();
		}

		@Override
		public Set<it.polito.dp2.WF.ProcessReader> getProcesses() { return processesSet; }

		@Override
		public Set<it.polito.dp2.WF.WorkflowReader> getWorkflows() { return new HashSet<it.polito.dp2.WF.WorkflowReader>(workflowsMap.values()); }

		@Override
		public it.polito.dp2.WF.WorkflowReader getWorkflow(String name) { return workflowsMap.get(name); }

		public static class WorkflowReader implements it.polito.dp2.WF.WorkflowReader
		{
			private WorkflowInfoMonitor parentMonitor;
			private String name;
			private Map<String, it.polito.dp2.WF.ActionReader> actionReaders = new TreeMap<String, it.polito.dp2.WF.ActionReader>();
			private Set<it.polito.dp2.WF.ProcessReader> processesSet = new LinkedHashSet<it.polito.dp2.WF.ProcessReader>();

			private WorkflowReader(Workflow workflow, WorkflowInfoMonitor parentMonitor) throws WorkflowMonitorException
			{
				this.parentMonitor = parentMonitor;
				this.name = workflow.getName();
				
				for(Action action : workflow.getAction())
				{
					if( action.getNextProcessAction() != null )
						 actionReaders.put(action.getName(), new ProcessActionReader(action, this));
					else actionReaders.put(action.getName(), new  SimpleActionReader(action, this));
				}
			}

			private void consolidateWorkflows() throws WorkflowMonitorException
			{
				for(ActionReader actionReader : actionReaders.values())
					if(actionReader instanceof SimpleActionReader)
						 ( (SimpleActionReader)actionReader).consolidate();
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
			public it.polito.dp2.WF.ActionReader getAction(String actionName) { return actionReaders.get(actionName); }

			@Override
			public Set<it.polito.dp2.WF.ActionReader> getActions() { return new HashSet<ActionReader>(actionReaders.values()); }

			@Override
			public Set<it.polito.dp2.WF.ProcessReader> getProcesses() { return processesSet; }

			public static class SimpleActionReader implements it.polito.dp2.WF.SimpleActionReader
			{
				private String name;
				private String role;
				private boolean isAutomaticallyInstantiated;
				private WorkflowReader enclosingWorkflow;
				private List<String> nextPossibleSimpleActionsNames;
				private Set<it.polito.dp2.WF.ActionReader> nextPossibleActionsSet = new LinkedHashSet<it.polito.dp2.WF.ActionReader>();
				
				private SimpleActionReader(Action action, WorkflowReader parentWorkflow) throws WorkflowMonitorException
				{
					enclosingWorkflow = parentWorkflow;
					
					name = action.getName();
					role = action.getRole();
					isAutomaticallyInstantiated = action.isAutoInstanced();
					nextPossibleSimpleActionsNames = action.getNextAction();
				}
				
				private void consolidate() throws WorkflowMonitorException
				{
					for(String actionName : nextPossibleSimpleActionsNames)
					{
						ActionReader actionReader = enclosingWorkflow.getAction(actionName);
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
				public it.polito.dp2.WF.WorkflowReader getEnclosingWorkflow() { return enclosingWorkflow; }
			
				@Override
				public Set<it.polito.dp2.WF.ActionReader> getPossibleNextActions() { return nextPossibleActionsSet; }
			}

			public static class ProcessActionReader implements it.polito.dp2.WF.ProcessActionReader
			{
				private String name;
				private String role;
				private boolean isAutomaticallyInstantiated;
				private WorkflowReader enclosingWorkflow;
				private String actionWorkflowName;
				private it.polito.dp2.WF.WorkflowReader actionWorkflow;
				
				private ProcessActionReader(Action action, WorkflowReader parentWorkflow) throws WorkflowMonitorException
				{
					enclosingWorkflow = parentWorkflow;
					
					name = action.getName();
					role = action.getRole();
					isAutomaticallyInstantiated = action.isAutoInstanced();
					actionWorkflowName = action.getNextProcessAction();
				}
				
				private void consolidate() throws WorkflowMonitorException
				{
					actionWorkflow = enclosingWorkflow.parentMonitor.getWorkflow(actionWorkflowName);
				}
			
				@Override
				public String getName() { return name; }
			
				@Override
				public String getRole() { return role; }
			
				@Override
				public boolean isAutomaticallyInstantiated() { return isAutomaticallyInstantiated; }
			
				@Override
				public it.polito.dp2.WF.WorkflowReader getEnclosingWorkflow() { return enclosingWorkflow; }
			
				@Override
				public it.polito.dp2.WF.WorkflowReader getActionWorkflow() { return actionWorkflow; }
			}
		}

		public static class ProcessReader implements it.polito.dp2.WF.ProcessReader
		{
		    private WorkflowInfoMonitor parentMonitor;
		    private String workflowName;
			private Calendar startDate = new GregorianCalendar();
			private List<it.polito.dp2.WF.ActionStatusReader> status = new LinkedList<it.polito.dp2.WF.ActionStatusReader>();
			private WorkflowReader workflowAffinity;
			
			private ProcessReader(Process process, WorkflowInfoMonitor parentMonitor) throws WorkflowMonitorException
			{
				this.parentMonitor = parentMonitor;
				workflowName = process.getWorkflowName();
				startDate = process.getStartDate().toGregorianCalendar();
				
				for(ActionStatus actionStatus : process.getActionStatus())
				{
					ActionStatusReader actionStatusReader = new ActionStatusReader(actionStatus, this);
					status.add(actionStatusReader);
				}
			}
			
			private void consolidate() throws WorkflowMonitorException
			{
				workflowAffinity = (WorkflowReader)parentMonitor.getWorkflow(workflowName);
				
				for (it.polito.dp2.WF.ActionStatusReader actionStatusReader : status)
					((ActionStatusReader)actionStatusReader).consolidate();
			}
		
			@Override
			public Calendar getStartTime() { return startDate; }
		
			@Override
			public List<it.polito.dp2.WF.ActionStatusReader> getStatus() { return status; }
		
			@Override
			public it.polito.dp2.WF.WorkflowReader getWorkflow() { return workflowAffinity; }

			public static class ActionStatusReader implements it.polito.dp2.WF.ActionStatusReader
			{
			    private String actionName;
				private boolean isTakenInCharge;
				private boolean isTerminated;
				private ProcessReader parentProcess;
				private GregorianCalendar endDate;
				private String actorName;
				private Actor actor = null;
			
				private ActionStatusReader(ActionStatus actionStatus, ProcessReader parentProcess) throws WorkflowMonitorException
				{
					this.parentProcess = parentProcess;
					actionName = actionStatus.getName();
					
					if(actionStatus.getEndDate() != null)
					{
						endDate = actionStatus.getEndDate().toGregorianCalendar();
						isTerminated = true;
					}
					else { endDate = null; isTerminated = false; }
					
					if((actorName=actionStatus.getActor()) != null ) isTakenInCharge = true;
					else isTakenInCharge = false;
				}
			
				private void consolidate() throws WorkflowMonitorException
				{
					//TODO: controllo che il nome dell'azione sia effettivamente contenuto nel workflow a cui appartiene il processo
					//FIXME: su xml Schema non riesco ad impostare chiavi gerarchiche
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
	}
}