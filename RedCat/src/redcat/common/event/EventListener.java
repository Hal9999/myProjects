package redcat.common.event;

import redcat.manager.*;

import java.io.*;
import java.net.*;
import java.util.logging.*;

/**
 * Classe che implementa un ascoltatore di eventi tramite diversi protocolli di comunicazione.
 *
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class EventListener
{
    private final ManagerController controller;
    private final int tcpPort;
    private final int udpPort;
    
    /**
     * Crea un EventListener.
     * @param tcpPort la porta TCP da aprire in ascolto di ExtendedEvent trasferiti da remoto
     * @param udpPort la porta UDP da aprire in ascolto di ExtendedEvent trasferiti da remoto
     * @param controller il controller a cui inviare gli ExtendedEvent ricevuti dalla porte
     */
    public EventListener(int tcpPort, int udpPort, ManagerController controller)
    {
        this.controller = controller;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }
    
    /**
     * Avvia l'ascolto sulle porte TCP e UDP specificate nel costruttore.
     * Gli ExtendedEvent, arrivati da remoto sulle porte locali,
     * vengono passati al controller specificato nel costruttore.
     * @return questo EventListener
     */
    public EventListener go()
    {
        
        new Thread( new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        ServerSocket s = new ServerSocket(tcpPort);
                        while(true)
                        {
                            final Socket socket = s.accept();
                            new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    try
                                    {
                                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                                        ExtendedEvent event = ExtendedEvent.deserializeFromEvent((Event)ois.readObject());
                                        controller.enqueue(event);
                                        
                                        ois.close();
                                        socket.close();
                                    }
                                    catch (ClassNotFoundException ex)
                                    {
                                        Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    catch (IOException  ex)
                                    {
                                        Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    catch (InterruptedException ex)
                                    {
                                        Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }).start();
                        }
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        
        new Thread( new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        DatagramSocket sock = new DatagramSocket(udpPort);
                        while(true)
                        {
                            DatagramPacket pack = new DatagramPacket(new byte[1024], 1024);
                            sock.receive(pack);

                            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(pack.getData()));
                            Event event = (Event)input.readObject();
                            input.close();
                            
                            ExtendedEvent exEvent = ExtendedEvent.deserializeFromEvent(event);
                            controller.enqueue(exEvent);
                        }
                    }
                    catch (ClassNotFoundException ex)
                    {
                        Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    catch (InterruptedException ex)
                    {
                        Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        
        return this;
    }
}