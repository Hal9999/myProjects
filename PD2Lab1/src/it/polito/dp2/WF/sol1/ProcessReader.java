package it.polito.dp2.WF.sol1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.polito.dp2.WF.WorkflowMonitorException;
import it.polito.dp2.WF.WorkflowReader;

public class ProcessReader implements it.polito.dp2.WF.ProcessReader
{
	private static final Pattern workspaceNamePattern = Pattern.compile("^[a-zA-Z0-9]+$");
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z");    
    static { dateFormat.setLenient(false); }
    
    private WorkflowInfoMonitor parentMonitor;
    private String workflowName;
	private Calendar startDate = new GregorianCalendar();
	private List<it.polito.dp2.WF.ActionStatusReader> status = new LinkedList<it.polito.dp2.WF.ActionStatusReader>();
	private WorkflowReader workflowAffinity;
	
	ProcessReader(Element element, WorkflowInfoMonitor parentMonitor) throws WorkflowMonitorException
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
	
	void consolidate() throws WorkflowMonitorException
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
