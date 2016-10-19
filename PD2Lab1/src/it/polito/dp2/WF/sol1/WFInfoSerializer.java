package it.polito.dp2.WF.sol1;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import it.polito.dp2.WF.ActionReader;
import it.polito.dp2.WF.ActionStatusReader;
import it.polito.dp2.WF.ProcessActionReader;
import it.polito.dp2.WF.ProcessReader;
import it.polito.dp2.WF.SimpleActionReader;
import it.polito.dp2.WF.WorkflowMonitor;
import it.polito.dp2.WF.WorkflowMonitorException;
import it.polito.dp2.WF.WorkflowMonitorFactory;
import it.polito.dp2.WF.WorkflowReader;

public class WFInfoSerializer
{
	private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");
	private WorkflowMonitor monitor;
	private Document document;
	
	public static void main(String[] args)
	{
		if( args.length != 1 )
		{
			System.err.println("Not enough parameters. Destination XML file name required.");
			System.exit(1);
		}
		
		WFInfoSerializer wfis;
		try {
			wfis = new WFInfoSerializer();
	        
			//new WFInfo2(wfis.monitor).printAll(new File("originale.txt"));
	
			wfis.serialize(args[0]);
		}
		catch(ParserConfigurationException e)
		{
			System.err.println("Could not locate a JAXP DocumentBuilder class");
			System.err.println(e);
		}
		catch(WorkflowMonitorException e)
		{
			System.err.println("Could not instantiate data generator.");
			System.err.println(e);
		}
		catch(TransformerException e)
		{
			System.err.println("Unexpected error during serialization");
			System.err.println(e);
		}
		catch(Exception e)
		{
			System.err.println("Unkown Exception");
			System.err.println(e);
		}
	}

	public WFInfoSerializer() throws WorkflowMonitorException
	{
		WorkflowMonitorFactory factory = WorkflowMonitorFactory.newInstance();
		monitor = factory.newWorkflowMonitor();
	}
	
	public WFInfoSerializer(WorkflowMonitor monitor)
	{
		this.monitor = monitor;
	}
	
	public void serialize(String fileName) throws ParserConfigurationException, TransformerException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.newDocument();
		
		Element root = document.createElement("workflows");
		document.appendChild(root);
		
		if(monitor.getWorkflows()!=null)
		{
			for(WorkflowReader workflowReader : monitor.getWorkflows())
			{
				Element workflow = document.createElement("workflow");
				
				fillAndAppendNewNode(workflow, "name", workflowReader.getName());
				
				for(ActionReader action : workflowReader.getActions())
				{
					if(action instanceof SimpleActionReader)
					{
						Element simpleAction = document.createElement("simpleAction");
						
						fillAndAppendNewNode(simpleAction, "name", action.getName());
						fillAndAppendNewNode(simpleAction, "role", action.getRole());
	
						for(ActionReader nextAction : ((SimpleActionReader)action).getPossibleNextActions())
							fillAndAppendNewNode(simpleAction, "nextAction", nextAction.getName());
						
						simpleAction.setAttribute("autoInstance", action.isAutomaticallyInstantiated() ? "yes" : "no");
						
						workflow.appendChild(simpleAction);
					}
					else if(action instanceof ProcessActionReader)
					{
						Element processAction = document.createElement("processAction");
						
						fillAndAppendNewNode(processAction, "workflowName", ((ProcessActionReader)action).getActionWorkflow().getName());
						fillAndAppendNewNode(processAction, "role", action.getRole());
						processAction.setAttribute("autoInstance", action.isAutomaticallyInstantiated() ? "yes" : "no");
						
						workflow.appendChild(processAction);
					}
				}
				root.appendChild(workflow);
			}
			for(ProcessReader processReader: monitor.getProcesses())
			{
				Element process = document.createElement("process");
				
				fillAndAppendNewNode(process, "workflowName", processReader.getWorkflow().getName());
				fillAndAppendNewNode(process, "startDate", dateFormat.format(processReader.getStartTime().getTime()));
				
				for(ActionStatusReader actionStatusReader : processReader.getStatus())
				{
					Element actionStatus = document.createElement("actionStatus");
					
					fillAndAppendNewNode(actionStatus, "name", actionStatusReader.getActionName());
					
					actionStatus.setAttribute("taken", actionStatusReader.isTakenInCharge() ? "yes" : "no");
					if(actionStatusReader.isTakenInCharge())
						fillAndAppendNewNode(actionStatus, "actor", actionStatusReader.getActor().getName());
	
					actionStatus.setAttribute("terminated", actionStatusReader.isTerminated() ? "yes" : "no");
					if(actionStatusReader.isTerminated())
						fillAndAppendNewNode(actionStatus, "endDate", dateFormat.format(actionStatusReader.getTerminationTime().getTime()));
					
					process.appendChild(actionStatus);
				}
				root.appendChild(process);
			}
		}
		
		TransformerFactory xformFactory = TransformerFactory.newInstance();
		Transformer idTransform = xformFactory.newTransformer();
		idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
		idTransform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		idTransform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "wfInfo.dtd");
		Source input = new DOMSource(document);
		Result output = new StreamResult(fileName);
		idTransform.transform(input, output);
	}
	
	private void fillAndAppendNewNode(Element father, String nodeName, String nodeContent)
	{
		Element childNode = document.createElement(nodeName);
		childNode.setTextContent(nodeContent);
		father.appendChild(childNode);
	}
}