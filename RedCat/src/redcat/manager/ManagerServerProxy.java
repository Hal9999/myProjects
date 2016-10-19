package redcat.manager;

import redcat.common.*;
import redcat.common.event.*;

import java.io.*;
import java.util.logging.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Classe che si occupa concretamente di creare il proxy utilizzato dal sensore
 * per la comunicazione degli Event al Manager
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ManagerServerProxy implements Observer<ExtendedEvent>
{
    private final String address;
    private final int tcpPort;
    private final int udpPort;
    
    /**
     * Crea un ManagerServerProxy.
     * @param address l'indirizzo del server a cui connettersi
     * @param tcpPort la porta TCP per la connessione
     * @param udpPort la porta UDP per la connessione
     */
    public ManagerServerProxy(String address, int tcpPort, int udpPort)
    {
        this.address = address;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }
    
    /**
     * crea un ManagerServerProxy ottenendo indirizzo e porte TCP e UDP da un
     * file di configurazione XML
     * @param xmlFile il file XML di configurazione
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException 
     */
    public ManagerServerProxy(File xmlFile) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
        Document document = documentBuilder.parse( xmlFile );
        document.getDocumentElement().normalize();

        try
        {
            Element sensorConfig = (Element)document.getElementsByTagName("manager").item(0);

            address = getTagValue("address", sensorConfig);
            tcpPort = Integer.parseInt(getTagValue("TCPport", sensorConfig));
            udpPort = Integer.parseInt(getTagValue("UDPport", sensorConfig));
        }
        catch(Exception ex)
        {
            throw new SAXException("Malformed XML file");
        }
    }
    
    @Override
    public void report(ExtendedEvent exEvent)
    {
        Event event = exEvent.serializeToEvent();
        try
        {
            ManagerProxyFactory.getProxy(address, tcpPort, udpPort, exEvent).report(event);
        }
        catch (Exception ex)
        {
            Logger.getLogger(ManagerServerProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if( nValue != null ) return nValue.getNodeValue();
        else return "";
    }
}