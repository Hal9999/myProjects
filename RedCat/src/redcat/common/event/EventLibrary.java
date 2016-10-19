package redcat.common.event;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.*;

/**
 * Classe che fornisce una libreria delle tipologie di eventi comprese dal Sensor di RedCat.
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class EventLibrary
{
    private HashMap<String, EventType> map = new HashMap<String, EventType>();
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Costruisce una EventLibrary a partire da una collezione di EventType
     * @param collection la collezione di EventType su cui costruire questa EventLibrary
     * @see EventType
     */
    public EventLibrary(Collection<EventType> collection)
    {
        for(EventType et : collection)
            map.put(et.matchString, et);
    }
    
    /**
     * Crea una EventLibrary da un file XML opportunamente riempito con
     * le tipologie di evento che si vuole che questa EventLibrary comprenda
     * @param xmlFile il file XML contenente le tipologie di evento
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public EventLibrary(File xmlFile) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(xmlFile);
        document.getDocumentElement().normalize();
        
        try
        {
            NodeList nodeList = document.getElementsByTagName("eventType");

            for (int i = 0; i < nodeList.getLength(); i++)
            {
                Element element = (Element) nodeList.item(i);

                EventType eventType = new EventType();
                    eventType.typeID = Integer.parseInt(getTagValue("id", element));
                    eventType.matchString = getTagValue("matchString", element);
                    eventType.niceString = getTagValue("niceString", element);
                    eventType.classification = getTagValue("classification", element);
                    eventType.priority = Integer.parseInt(getTagValue("priority", element));
                    eventType.description = getTagValue("description", element);

                map.put(eventType.matchString, eventType);
            }
        }
        catch(Exception ex)
        {
            throw new SAXException("Malformed XML file");
        }
    }
    
    /**
     * Cerca un evento basandosi sulla stringa di match del tipo di evento.
     * Se una corrispondenza è trovata, un nuovo ExtendedEvent viene creato e riempito
     * delle informazioni del tipo di evento e dal valore specificato nel parametro
     * value.
     * @param string il nome dell'evento da cercare
     * @param value il valore dell'evento
     * @return un nuovo ExtendedEvent se presente nella EventLibrary, {@code null} altrimenti
     */
    public ExtendedEvent seek(String string, String value)
    {
        EventType type = map.get(string);
        if(type != null)
        {
            ExtendedEvent event = new ExtendedEvent();
                event.priority = type.priority;
                event.type = type.classification;
                
                event.typeID = type.typeID;
                event.time = dateFormat.format(new Date());
                event.description = type.description;
                event.niceString = type.niceString;
                event.value = value;
            
            return event;
        }
        else return null;
    }
    
    private String getTagValue(String sTag, Element eElement)
    {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if( nValue != null ) return nValue.getNodeValue();
        else return "";
    }
}
