package redcat.ui;

import redcat.common.*;
import redcat.common.event.*;
import redcat.client.*;

import java.io.*;
import java.net.*;
import java.util.logging.*;

/**
 * Classe che implementa la GUI testuale del Client del sistema RedCat
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ClientCLIMain
{
    /**
     * 
     * @param args
     * @throws UnknownHostException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException
    {
        System.out.println("client");
        Client client = new Client("stefano", "opera", new String[]{"Fault", "Performance"}, "localhost", 7000);

        ToCLIObserver<ExtendedEvent> cli = new ToCLIObserver<ExtendedEvent>();
        client.registerObserver(cli);

        client.connect();
        client.go();
            
        try
        {
            client.awaitTermination();
        }
        catch (Throwable ex)
        {
            Logger.getLogger(ClientCLIMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            System.out.println("Disconnected.");
            System.exit(0);
        }
    }
}