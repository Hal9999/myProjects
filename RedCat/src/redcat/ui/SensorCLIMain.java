package redcat.ui;

import redcat.common.*;
import redcat.common.event.*;
import redcat.manager.*;
import redcat.sensor.*;

import java.io.*;
import javax.xml.bind.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

/**
 * Classe che implementa la GUI testuale del Sensor del sistema RedCat
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class SensorCLIMain
{
    /**
     * @param args array di String con i parametri da passare al Sensor.
     * <p>args[0] is the xml file name containing the event type library understand by this sensor</p>
     * <p>args[1] is the xml file name containing the configuration for this sensor</p>
     * <p>args[2] is the file name where store captured events</p>
     * @throws InterruptedException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws JAXBException
     */
    public static void main(String[] args) throws InterruptedException, ParserConfigurationException, SAXException, IOException, JAXBException
    {
        //args = new String[]{"configurationFiles/eventTypeLibrary.xml", "configurationFiles/sensorSideConfiguration.xml"};
        //args = new String[]{"configurationFiles/eventTypeLibrary.xml", "configurationFiles/sensorSideConfiguration.xml", "../../log.txt"};
        
        if( args.length < 2 || args.length > 3 )
        {
            System.out.println("RedCat - Sensor - Command Line Interface\n");
            System.out.println("Usage: java -jar \"RedCat Sensor.jar\" library configuration [log]");
            System.out.println("where:");
            System.out.println("\tlibrary -> the xml file containing the event type library understand by this sensor");
            System.out.println("\tconfiguration -> is the xml file containing the configuration for this sensor");
            System.out.println("\tlog -> (optional) file where store captured events");
            System.out.println("\nEnjoy!");
        }
        else
        {
            EventLibrary recognizer = new EventLibrary(new File(args[0]));
            Sensor sensor = new Sensor(new File(args[1]), recognizer);

            Observer<ExtendedEvent> cli = new ToCLIObserver<ExtendedEvent>();
            sensor.registerObserver(cli);

            Observer<ExtendedEvent> server = new ManagerServerProxy(new File(args[1]));
            sensor.registerObserver(server);

            if(args.length == 3)
            {
                Observer<ExtendedEvent> fileObserver = new ToFileObserver<ExtendedEvent>( new File(args[2]) );
                sensor.registerObserver(fileObserver);
            }

            sensor.go();
        }
    }
}