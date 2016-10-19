package it.polito.dp2.WF.sol1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import it.polito.dp2.WF.ActionReader;
import it.polito.dp2.WF.Actor;
import it.polito.dp2.WF.WorkflowMonitorException;

public class ActionStatusReader implements it.polito.dp2.WF.ActionStatusReader
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

	ActionStatusReader(Element element, ProcessReader parentProcess) throws WorkflowMonitorException
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

	void consolidate() throws WorkflowMonitorException
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
