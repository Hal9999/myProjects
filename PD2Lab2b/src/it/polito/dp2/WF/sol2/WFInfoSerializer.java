package it.polito.dp2.WF.sol2;

import it.polito.dp2.WF.*;
import it.polito.dp2.WF.sol2.jaxb.*;
import it.polito.dp2.WF.sol2.jaxb.Workflows.*;
import it.polito.dp2.WF.sol2.jaxb.Workflows.Process;
import it.polito.dp2.WF.sol2.jaxb.Workflows.Workflow.*;
import java.io.*;
import java.util.GregorianCalendar;
import javax.xml.bind.*;
import javax.xml.datatype.*;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.*;
import org.xml.sax.SAXException;

public class WFInfoSerializer
{
	public static void main(String[] args) throws Exception
	{
		if( args.length != 1 ) throw new IllegalArgumentException("Not enough parameters: name of destination XML file is required.");
		
		WFInfoSerializer.serialize(args[0]);
	}
	
	private WFInfoSerializer(){};
	
	public static void serialize(String fileName) throws JAXBException, SAXException, WorkflowMonitorException, FactoryConfigurationError, DatatypeConfigurationException, FileNotFoundException
	{ serialize(WorkflowMonitorFactory.newInstance().newWorkflowMonitor(), fileName); }
	
	public static void serialize(WorkflowMonitor monitor, String fileName) throws JAXBException, SAXException, DatatypeConfigurationException, FileNotFoundException
	{
		Marshaller marshaller;
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance("it.polito.dp2.WF.sol2.jaxb");
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			try
			{
				SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = schemaFactory.newSchema(new File("xsd/WFInfo.xsd"));
				marshaller.setSchema(schema);
			}
			catch(SAXException e)
			{
				System.out.println(e.getLocalizedMessage());
				throw e;
			}
		}
		catch(JAXBException e)
		{
			System.out.println(e.getLocalizedMessage());
			throw e;
		}
		ObjectFactory objectFactory = new ObjectFactory();

		Workflows root = objectFactory.createWorkflows();
		
		if(monitor.getWorkflows()!=null)
		{
			for(WorkflowReader workflowReader : monitor.getWorkflows())
			{
				Workflow workflow = objectFactory.createWorkflowsWorkflow();
				root.getWorkflow().add(workflow);
				
				workflow.setName(workflowReader.getName());
				
				for(ActionReader actionReader : workflowReader.getActions())
				{
					Action action = objectFactory.createWorkflowsWorkflowAction();
					workflow.getAction().add(action);
					
					action.setName(actionReader.getName());
					action.setRole(actionReader.getRole());
					action.setAutoInstanced(actionReader.isAutomaticallyInstantiated());
					
					if(actionReader instanceof SimpleActionReader)
					{
						for(ActionReader nextAction : ((SimpleActionReader)actionReader).getPossibleNextActions())
							action.getNextAction().add(nextAction.getName());
					}
					else if(actionReader instanceof ProcessActionReader)
					{
						action.setNextProcessAction(((ProcessActionReader)actionReader).getActionWorkflow().getName());
					}
				}
			}
			for(ProcessReader processReader: monitor.getProcesses())
			{
				Process process = objectFactory.createWorkflowsProcess();
				root.getProcess().add(process);
				
				process.setWorkflowName(processReader.getWorkflow().getName());
				
				{
					GregorianCalendar gCalendar = new GregorianCalendar();
					gCalendar.setTime(processReader.getStartTime().getTime());
					XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
					process.setStartDate(xmlGregorianCalendar);
				}
				
				for(ActionStatusReader actionStatusReader : processReader.getStatus())
				{
					Workflows.Process.ActionStatus actionStatus = objectFactory.createWorkflowsProcessActionStatus();
					process.getActionStatus().add(actionStatus);
					
					actionStatus.setName(actionStatusReader.getActionName());
					
					if(actionStatusReader.getActor() != null)
						actionStatus.setActor(actionStatusReader.getActor().getName());
					
					if(actionStatusReader.getTerminationTime() != null)
					{
						GregorianCalendar gCalendar = new GregorianCalendar();
						gCalendar.setTime(actionStatusReader.getTerminationTime().getTime());
						XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
						actionStatus.setEndDate(xmlGregorianCalendar);
					}
				}
			}
		}
		marshaller.marshal(root, new FileOutputStream(fileName));
	}
}