package redcat.ui;

import redcat.common.*;
import redcat.common.event.*;
import redcat.manager.*;
import redcat.client.*;

import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

/**
 * Classe che implementa la GUI tesuale del Manager del sistema RedCat
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ManagerCLIMain
{
    /**
     * 
     * @param args
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException
    {
        System.out.println("manager");
        
        AsynchronousQueueModel<ExtendedEvent> model = new AsynchronousQueueModel<ExtendedEvent>();
        ToCLIObserver<ExtendedEvent> cliView = new ToCLIObserver<ExtendedEvent>();
        ManagerController controller = new ManagerController(model, cliView);
        
        ClientAuthenticator authenticator = new ClientAuthenticator(new File("./configurationFiles/clientCredentials.xml"));
        
        Manager manager = new Manager(model, controller, authenticator, 3000, 3001, 7000);
        
        manager.go();
    }
}