//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2016.02.22 alle 05:59:37 PM CET 
//


package it.polito.dp2.WF.sol2.jaxb;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the it.polito.dp2.WF.sol2.jaxb package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: it.polito.dp2.WF.sol2.jaxb
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Workflows }
     * 
     */
    public Workflows createWorkflows() {
        return new Workflows();
    }

    /**
     * Create an instance of {@link Workflows.Process }
     * 
     */
    public Workflows.Process createWorkflowsProcess() {
        return new Workflows.Process();
    }

    /**
     * Create an instance of {@link Workflows.Workflow }
     * 
     */
    public Workflows.Workflow createWorkflowsWorkflow() {
        return new Workflows.Workflow();
    }

    /**
     * Create an instance of {@link Workflows.Process.ActionStatus }
     * 
     */
    public Workflows.Process.ActionStatus createWorkflowsProcessActionStatus() {
        return new Workflows.Process.ActionStatus();
    }

    /**
     * Create an instance of {@link Workflows.Workflow.Action }
     * 
     */
    public Workflows.Workflow.Action createWorkflowsWorkflowAction() {
        return new Workflows.Workflow.Action();
    }

}
