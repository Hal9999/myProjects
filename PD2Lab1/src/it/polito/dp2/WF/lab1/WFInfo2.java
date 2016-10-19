package it.polito.dp2.WF.lab1;

import it.polito.dp2.WF.ActionReader;
import it.polito.dp2.WF.ActionStatusReader;
import it.polito.dp2.WF.ProcessActionReader;
import it.polito.dp2.WF.ProcessReader;
import it.polito.dp2.WF.SimpleActionReader;
import it.polito.dp2.WF.WorkflowMonitor;
import it.polito.dp2.WF.WorkflowMonitorException;
import it.polito.dp2.WF.WorkflowMonitorFactory;
import it.polito.dp2.WF.WorkflowReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.List;

public class WFInfo2
{
	private WorkflowMonitor monitor;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");

	public WFInfo2() throws WorkflowMonitorException
	{
		WorkflowMonitorFactory factory = WorkflowMonitorFactory.newInstance();
		monitor = factory.newWorkflowMonitor();
	}

	public WFInfo2(WorkflowMonitor monitor)
	{
		this.monitor = monitor;
	}

	public void printAll(File txtFile) throws FileNotFoundException
	{
		PrintStream out = new PrintStream(txtFile);
		printWorkflows(out);
		printProcesses(out);
		out.close();
	}

	private void printProcesses(PrintStream out)
	{

		// Get the list of processes
		Set<ProcessReader> set = monitor.getProcesses();

		/* Print the header of the table */
		out.println("#");
		out.println("#Number of Processes: " + set.size());
		out.println("#");
		String header = new String("#List of processes:");
		printHeader(header, out);

		// For each process print related data
		for (ProcessReader wfr : set)
		{
			out.println("Process started at " + dateFormat.format(wfr.getStartTime().getTime()) + " "
					+ "- Workflow " + wfr.getWorkflow().getName());
			out.println("Status:");
			List<ActionStatusReader> statusSet = wfr.getStatus();
			printHeader("Action Name\tTaken in charge by\tTerminated", out);
			for (ActionStatusReader asr : statusSet)
			{
				out.print(asr.getActionName() + "\t");
				if (asr.isTakenInCharge())
					out.print(asr.getActor().getName() + "\t\t");
				else
					out.print("-" + "\t\t\t");
				if (asr.isTerminated())
					out.println(dateFormat.format(asr.getTerminationTime().getTime()));
				else
					out.println("-");
			}
			out.println("#");
		}
		out.println("#End of Processes");
		out.println("#");
	}

	private void printWorkflows(PrintStream out)
	{
		// Get the list of workflows
		Set<WorkflowReader> set = monitor.getWorkflows();

		/* Print the header of the table */
		out.println("#");
		out.println("#Number of Workflows: " + set.size());
		out.println("#");
		String header = new String("#List of workflows:");
		printHeader(header, out);

		// For each workflow print related data
		for (WorkflowReader wfr : set)
		{
			out.println();
			out.println("Data for Workflow " + wfr.getName());
			out.println();

			// Print actions
			out.println("Actions:");
			Set<ActionReader> setAct = wfr.getActions();
			printHeader("Action Name\tRole\t\tAutom.Inst.\tSimple/Process\tWorkflow\tNext Possible Actions", out);
			for (ActionReader ar : setAct)
			{
				out.print(ar.getName() + "\t" + ar.getRole() + "\t" + ar.isAutomaticallyInstantiated() + "\t");
				if (ar instanceof SimpleActionReader)
				{
					out.print("\tSimple\t\t" + "-\t\t");
					// Print next actions
					Set<ActionReader> setNxt = ((SimpleActionReader) ar).getPossibleNextActions();
					for (ActionReader nAct : setNxt)
						out.print(nAct.getName() + " ");
					out.println();
				} else if (ar instanceof ProcessActionReader)
				{
					out.print("\tProcess\t\t");
					// print workflow
					out.println(((ProcessActionReader) ar).getActionWorkflow().getName());
				}
			}
			out.println("#");
		}
		out.println("#End of Workflows");
		out.println("#");
	}

	private void printHeader(String header, PrintStream out)
	{
		StringBuffer line = new StringBuffer(132);

		for (int i = 0; i < 132; ++i)
		{
			line.append('-');
		}

		out.println(header);
		out.println(line);
	}
}
