package redcat.sensor;

import redcat.common.*;
import redcat.common.event.*;

import java.io.*;
import java.sql.*;
import java.util.logging.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Classe che implementa il Sensor del sistema RedCat
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class Sensor extends AsynchronousObservable<ExtendedEvent> implements Runnable
{
    private String address;
    private String user;
    private String password;
    private double samplingPeriod = 1000;
    private long sensorID;
    private boolean stop;
    private Thread thisThread;
    private EventLibrary library;

    /**
     * Crea un Sensor impostando la configurazione in base ad un file XML di configurazione.
     * @param xmlConfigFile il file XML di configurazione
     * @param library la EventLibrary contenente le tipologie di eventi di interesse
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Sensor(File xmlConfigFile, EventLibrary library) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(xmlConfigFile);
        document.getDocumentElement().normalize();

        Element sensorConfig = (Element) document.getElementsByTagName("sensor").item(0);
        
        try
        {
            this.sensorID = Long.parseLong(getTagValue("id", sensorConfig));
            this.address = getTagValue("address", sensorConfig) + ":" + getTagValue("port", sensorConfig);
            this.user = getTagValue("user", sensorConfig);
            this.password = getTagValue("password", sensorConfig);
            this.samplingPeriod = Double.parseDouble(getTagValue("samplingperiod", sensorConfig));
        }
        catch(Exception ex)
        {
            throw new SAXException("Malformed XML file" + ex);
        }
        
        this.library = library;
    }
    
    /**
     * Crea un Sensor.
     * @param xmlDocument nodo XML in cui trovare le informazioni di configurazioni con cui
     * inizializzare questo Sensor
     * @param library la EventLibrary contenente le tipologie di eventi di interesse
     * @throws SAXException qualora il file xml non sia ben formattato
     */
    public Sensor(Document xmlDocument, EventLibrary library) throws SAXException
    {
        xmlDocument.getDocumentElement().normalize();

        Element sensorConfig = (Element) xmlDocument.getElementsByTagName("sensor").item(0);
        
        try
        {
            this.sensorID = Long.parseLong(getTagValue("id", sensorConfig));
            this.address = getTagValue("address", sensorConfig) + ":" + getTagValue("port", sensorConfig);
            this.user = getTagValue("user", sensorConfig);
            this.password = getTagValue("password", sensorConfig);
            this.samplingPeriod = Double.parseDouble(getTagValue("samplingperiod", sensorConfig));
        }
        catch(Exception ex)
        {
            throw new SAXException("Malformed XML" + ex);
        }
        
        this.library = library;
    }
    
    /**
     * Crea un Sensor.
     * @param sensorID l'id del nuovo Sensor
     * @param address indirizzo del server MySQL da monitorare
     * @param port la porta del server MySQL
     * @param user il nome utente con cui loggarsi al server MySQL
     * @param password la password dello user utilizzato per la connessione al server MySQL
     * @param samplingPeriod il periodo di campionamento in millisecondi
     * @param library la EventLibrary contenente le tipologie di eventi di interesse
     */
    public Sensor(long sensorID, String address, int port, String user, String password, double samplingPeriod, EventLibrary library)
    {
        this.sensorID = sensorID;
        this.address = address + ":" + port;
        this.user = user;
        this.password = password;
        this.samplingPeriod = samplingPeriod;
        this.library = library;
    }
    
    /**
     * Avvia la raccolta delle informazioni dal server MySQL specificato nel costruttore.
     */
    public void go()
    {
        this.stop = false;
        thisThread = new Thread( this );
        thisThread.start();
    }
    
    /**
     * Sospende la raccolta delle informazioni.
     */
    public void pause()
    {
        this.stop = true;
    }
    
    @Override
    public void run()
    {
        try
        {
            Connection connection = DriverManager.getConnection("jdbc:mysql://" + address, user, password);
            Statement statement = connection.createStatement();

            while(!stop)
            {
                ResultSet result = statement.executeQuery("SHOW STATUS;");

                while( result.next() )
                {
                    ExtendedEvent event = library.seek(result.getString(1), result.getString(2));
                    if(event != null)
                    {
                        event.sensorID = sensorID;
                        reportObservers(event);
                    }
                }
                Thread.sleep( (long)samplingPeriod );
            }
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex)
        {
            Logger.getLogger(Sensor.class.getName()).log(Level.SEVERE, null, ex);
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