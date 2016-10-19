package redcat.client;

import redcat.common.*;
import redcat.common.event.*;

import java.io.*;
import java.net.Socket;
import java.util.logging.*;

/**
 * Classe che rappresenta un Client in una macchina remota,
 * rappresentando così una implementazione di proxy.
 *
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class RemoteClient implements redcat.common.Observer<ExtendedEvent>
{
    private final String[] interests;
    private Socket socket;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;
    
    private final Object streamLock = new Object();
    
    /**
     * Crea un RemoteClient, ovvero un proxy per un Client su un diverso spazio degli indirizzi di memoria.
     * @param socket l'oggetto Socket che si è generato alla ricezione del Client di cui questo RemoteClient
     * vuole essere un proxy remoto
     * @param objIn lo ObjectInputStream associato alla socket
     * @param objOut lo ObjectOutputStream associato alla socket
     * @param interests le informazioni che il Client remoto vuole conoscere
     * @throws IOException 
     */
    RemoteClient(Socket socket, ObjectInputStream objIn, ObjectOutputStream objOut, String[] interests) throws IOException
    {
        this.interests = interests;
        
        this.socket = socket;
        this.objOut = objOut;
        this.objIn = objIn;
    }

    @Override
    public void report(ExtendedEvent event)
    {
        try
        {
            synchronized(streamLock)
            {
                objOut.writeObject(event);
                objOut.flush();
            }
        }
        catch(IOException ex)
        {
            Logger.getLogger(RemoteClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Controlla se la FilterView è di interesse per il RemoteClient;
     * se lo è restituisce {@code true}, {@code false} altrimenti
     * @param view la FilteredView da controllare
     * @return {@code true} se è interessato alla view, {@code false} altrimenti
     */
    public boolean isInterested(FilterView<ExtendedEvent> view)
    {
        for(String s : interests)
            if( view.getType().equals(s) ) return true;
        
        return false;
    }
}