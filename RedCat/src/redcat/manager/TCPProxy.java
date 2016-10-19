package redcat.manager;

import redcat.common.event.*;

import java.net.*;
import java.io.*;
import java.util.logging.*;

/**
 * Classe che implementa e costruisce proxy di tipo TCP
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class TCPProxy extends ManagerProxy
{
    private Socket socket;
    private int port;
    private String addr;

    /**
     * Crea un TCPProxy.
     * @param address indirizzo a cui connettersi
     * @param port porta a cui connettersi
     */
    public TCPProxy(String address, int port)
    {
        this.addr = address;
        this.port = port;
    }

    @Override
    public void report(Event e)
    {
        try
        {
            socket = new Socket(addr, port);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(e);
                oos.flush();
            socket.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(TCPProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}