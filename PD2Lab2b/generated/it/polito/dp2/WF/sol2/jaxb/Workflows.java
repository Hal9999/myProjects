//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2016.02.22 alle 05:59:37 PM CET 
//


package it.polito.dp2.WF.sol2.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="workflow" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="name" type="{http://www.pd2.org/schemas/wfInfo}WorkflowNameType"/>
 *                   &lt;element name="action" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="name" type="{http://www.pd2.org/schemas/wfInfo}ActionNameType"/>
 *                             &lt;element name="role" type="{http://www.pd2.org/schemas/wfInfo}RoleNameType"/>
 *                             &lt;choice>
 *                               &lt;element name="nextAction" type="{http://www.pd2.org/schemas/wfInfo}ActionNameType" maxOccurs="unbounded" minOccurs="0"/>
 *                               &lt;element name="nextProcessAction" type="{http://www.pd2.org/schemas/wfInfo}WorkflowNameType"/>
 *                             &lt;/choice>
 *                           &lt;/sequence>
 *                           &lt;attribute name="autoInstanced" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="process" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="workflowName" type="{http://www.pd2.org/schemas/wfInfo}WorkflowNameType"/>
 *                   &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *                   &lt;element name="actionStatus" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="name" type="{http://www.pd2.org/schemas/wfInfo}ActionNameType"/>
 *                             &lt;element name="actor" type="{http://www.pd2.org/schemas/wfInfo}ActorNameType" minOccurs="0"/>
 *                             &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "workflow",
    "process"
})
@XmlRootElement(name = "workflows")
public class Workflows {

    protected List<Workflows.Workflow> workflow;
    protected List<Workflows.Process> process;

    /**
     * Gets the value of the workflow property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the workflow property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWorkflow().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Workflows.Workflow }
     * 
     * 
     */
    public List<Workflows.Workflow> getWorkflow() {
        if (workflow == null) {
            workflow = new ArrayList<Workflows.Workflow>();
        }
        return this.workflow;
    }

    /**
     * Gets the value of the process property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the process property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProcess().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Workflows.Process }
     * 
     * 
     */
    public List<Workflows.Process> getProcess() {
        if (process == null) {
            process = new ArrayList<Workflows.Process>();
        }
        return this.process;
    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="workflowName" type="{http://www.pd2.org/schemas/wfInfo}WorkflowNameType"/>
     *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
     *         &lt;element name="actionStatus" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="name" type="{http://www.pd2.org/schemas/wfInfo}ActionNameType"/>
     *                   &lt;element name="actor" type="{http://www.pd2.org/schemas/wfInfo}ActorNameType" minOccurs="0"/>
     *                   &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "workflowName",
        "startDate",
        "actionStatus"
    })
    public static class Process {

        @XmlElement(required = true)
        protected String workflowName;
        @XmlElement(required = true)
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar startDate;
        @XmlElement(required = true)
        protected List<Workflows.Process.ActionStatus> actionStatus;

        /**
         * Recupera il valore della proprietà workflowName.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWorkflowName() {
            return workflowName;
        }

        /**
         * Imposta il valore della proprietà workflowName.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWorkflowName(String value) {
            this.workflowName = value;
        }

        /**
         * Recupera il valore della proprietà startDate.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getStartDate() {
            return startDate;
        }

        /**
         * Imposta il valore della proprietà startDate.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setStartDate(XMLGregorianCalendar value) {
            this.startDate = value;
        }

        /**
         * Gets the value of the actionStatus property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the actionStatus property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getActionStatus().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Workflows.Process.ActionStatus }
         * 
         * 
         */
        public List<Workflows.Process.ActionStatus> getActionStatus() {
            if (actionStatus == null) {
                actionStatus = new ArrayList<Workflows.Process.ActionStatus>();
            }
            return this.actionStatus;
        }


        /**
         * <p>Classe Java per anonymous complex type.
         * 
         * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="name" type="{http://www.pd2.org/schemas/wfInfo}ActionNameType"/>
         *         &lt;element name="actor" type="{http://www.pd2.org/schemas/wfInfo}ActorNameType" minOccurs="0"/>
         *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "name",
            "actor",
            "endDate"
        })
        public static class ActionStatus {

            @XmlElement(required = true)
            protected String name;
            protected String actor;
            @XmlSchemaType(name = "dateTime")
            protected XMLGregorianCalendar endDate;

            /**
             * Recupera il valore della proprietà name.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Imposta il valore della proprietà name.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Recupera il valore della proprietà actor.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getActor() {
                return actor;
            }

            /**
             * Imposta il valore della proprietà actor.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setActor(String value) {
                this.actor = value;
            }

            /**
             * Recupera il valore della proprietà endDate.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getEndDate() {
                return endDate;
            }

            /**
             * Imposta il valore della proprietà endDate.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setEndDate(XMLGregorianCalendar value) {
                this.endDate = value;
            }

        }

    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="name" type="{http://www.pd2.org/schemas/wfInfo}WorkflowNameType"/>
     *         &lt;element name="action" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="name" type="{http://www.pd2.org/schemas/wfInfo}ActionNameType"/>
     *                   &lt;element name="role" type="{http://www.pd2.org/schemas/wfInfo}RoleNameType"/>
     *                   &lt;choice>
     *                     &lt;element name="nextAction" type="{http://www.pd2.org/schemas/wfInfo}ActionNameType" maxOccurs="unbounded" minOccurs="0"/>
     *                     &lt;element name="nextProcessAction" type="{http://www.pd2.org/schemas/wfInfo}WorkflowNameType"/>
     *                   &lt;/choice>
     *                 &lt;/sequence>
     *                 &lt;attribute name="autoInstanced" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "name",
        "action"
    })
    public static class Workflow {

        @XmlElement(required = true)
        protected String name;
        @XmlElement(required = true)
        protected List<Workflows.Workflow.Action> action;

        /**
         * Recupera il valore della proprietà name.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getName() {
            return name;
        }

        /**
         * Imposta il valore della proprietà name.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setName(String value) {
            this.name = value;
        }

        /**
         * Gets the value of the action property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the action property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAction().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Workflows.Workflow.Action }
         * 
         * 
         */
        public List<Workflows.Workflow.Action> getAction() {
            if (action == null) {
                action = new ArrayList<Workflows.Workflow.Action>();
            }
            return this.action;
        }


        /**
         * <p>Classe Java per anonymous complex type.
         * 
         * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="name" type="{http://www.pd2.org/schemas/wfInfo}ActionNameType"/>
         *         &lt;element name="role" type="{http://www.pd2.org/schemas/wfInfo}RoleNameType"/>
         *         &lt;choice>
         *           &lt;element name="nextAction" type="{http://www.pd2.org/schemas/wfInfo}ActionNameType" maxOccurs="unbounded" minOccurs="0"/>
         *           &lt;element name="nextProcessAction" type="{http://www.pd2.org/schemas/wfInfo}WorkflowNameType"/>
         *         &lt;/choice>
         *       &lt;/sequence>
         *       &lt;attribute name="autoInstanced" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "name",
            "role",
            "nextAction",
            "nextProcessAction"
        })
        public static class Action {

            @XmlElement(required = true)
            protected String name;
            @XmlElement(required = true)
            protected String role;
            protected List<String> nextAction;
            protected String nextProcessAction;
            @XmlAttribute(name = "autoInstanced")
            protected Boolean autoInstanced;

            /**
             * Recupera il valore della proprietà name.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Imposta il valore della proprietà name.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }

            /**
             * Recupera il valore della proprietà role.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRole() {
                return role;
            }

            /**
             * Imposta il valore della proprietà role.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRole(String value) {
                this.role = value;
            }

            /**
             * Gets the value of the nextAction property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the nextAction property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getNextAction().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link String }
             * 
             * 
             */
            public List<String> getNextAction() {
                if (nextAction == null) {
                    nextAction = new ArrayList<String>();
                }
                return this.nextAction;
            }

            /**
             * Recupera il valore della proprietà nextProcessAction.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNextProcessAction() {
                return nextProcessAction;
            }

            /**
             * Imposta il valore della proprietà nextProcessAction.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNextProcessAction(String value) {
                this.nextProcessAction = value;
            }

            /**
             * Recupera il valore della proprietà autoInstanced.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public boolean isAutoInstanced() {
                if (autoInstanced == null) {
                    return false;
                } else {
                    return autoInstanced;
                }
            }

            /**
             * Imposta il valore della proprietà autoInstanced.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setAutoInstanced(Boolean value) {
                this.autoInstanced = value;
            }

        }

    }

}
