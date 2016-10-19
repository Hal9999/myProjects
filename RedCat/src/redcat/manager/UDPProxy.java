package redcat.manager;

import redcat.common.event.*;

import java.net.*;
import java.io.*;
import java.util.logging.*;

/**
 * Classe che implementa e costruisce proxy di tipo UDP
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class UDPProxy extends ManagerProxy
{
    private String address;
    private int port;

    /**
     * Crea un UDPProxy.
     * @param address l'indirizzo a cui connettersi
     * @param port porta a cui connettersi
     */
    public UDPProxy(String address, int port)
    {
        this.address = address;
        this.port = port;
    }

    @Override
    public void report(Event obj)
    {
        try
        {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(byteStream);
            oo.writeObject(obj);
            
            DatagramSocket datasocket = new DatagramSocket();
            InetAddress netAddress = InetAddress.getByName(address);
            
            byte[] datatosend = byteStream.toByteArray();
            DatagramPacket packet = new DatagramPacket(datatosend, datatosend.length, netAddress, port);
            datasocket.send(packet);
            
            datasocket.close();
            oo.close();
        }
        catch (IOException ex)
        {
            Logger.getLogger(UDPProxy.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
}