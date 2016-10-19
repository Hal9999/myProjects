package redcat.manager;

import redcat.common.*;
import redcat.common.event.*;
import redcat.client.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * Classe che implementa il Manager del sistema RedCat.
 * Raccoglie gli eventi dal Sensor e le ridistribuisce ai Client connessi.
 *
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class Manager
{
    private final ClientAuthenticator authenticator;
    private final ManagerController controller;
    private final AsynchronousQueueModel<ExtendedEvent> model;
    private final int eventTCPPort;
    private final int eventUDPPort;
    private final int clientServerPort;
    
    /**
     * Crea un Manager che gestisce lo smistamento degli eventi.
     * Inoltre gestisce la connessione dei Client.
     * @param model il Model contenente la coda degli ExtendedEvent ricevuti da remoto
     * @param controller il Controller associato
     * @param authenticator il ClientAuthenticator per gestire gli accessi dei Client
     * @param eventTCPPort la porta TCP per la ricezione degli ExtendedEvent dal Sensore
     * @param eventUDPPort la porta UDP per la ricezione degli ExtendedEvent dal Sensore
     * @param clientServerPort la porta d'ascolto per i Client che vogliono connettersi a questo Manager
     */
    public Manager(AsynchronousQueueModel<ExtendedEvent> model, ManagerController controller, ClientAuthenticator authenticator, int eventTCPPort, int eventUDPPort, int clientServerPort)
    {
        this.model = model;
        this.controller = controller;
        this.authenticator = authenticator;
        this.eventTCPPort = eventTCPPort;
        this.eventUDPPort = eventUDPPort;
        this.clientServerPort = clientServerPort;
    }
    
    /**
     * Avvia i thread del Manager per l'ascolto, gestione delle comunicazioni
     * e lo smistamento degli eventi.
     */
    public void go()
    {
        new redcat.common.event.EventListener(eventTCPPort, eventUDPPort, controller).go();
        
        final Set<FilterView<ExtendedEvent>> views = new HashSet<FilterView<ExtendedEvent>>();
            views.add( new ExtendedEventTypeFilterView(model, "Fault").go() );
            views.add( new ExtendedEventTypeFilterView(model, "Performance").go() );
            views.add( new ExtendedEventTypeFilterView(model, "Monitoring").go() );
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    ServerSocket s = new ServerSocket(clientServerPort);
                    while(true)
                    {
                        final Socket socket = s.accept();
                        new Thread(new Runnable()
                        {
                            private Set<FilterView<ExtendedEvent>> registeredViews = new HashSet<FilterView<ExtendedEvent>>();
                            
                            @Override
                            public void run()
                            {
                                RemoteClient remoteClient = null;
                                ObjectOutputStream outObj = null;
                                ObjectInputStream inObj = null;
                                try
                                {
                                    outObj = new ObjectOutputStream( socket.getOutputStream() );
                                    inObj = new ObjectInputStream( socket.getInputStream() );

                                    Client client = (Client)inObj.readObject();

                                    Boolean legit = authenticator.isLegitUser(client);
                                    
                                    outObj.writeObject(legit);
                                    outObj.flush();

                                    if( legit )
                                    {
                                        remoteClient = client.getClientRemoteProxy(socket, inObj, outObj);
                                        for(FilterView<ExtendedEvent> fv : views)
                                            if( remoteClient.isInterested(fv) )
                                            {
                                                registeredViews.add(fv);
                                                fv.registerObserver(remoteClient);
                                            }
                                        //si blocca finchè il remoteClient non manda
                                        //un oggetto per comunicare la fine della connessione
                                        inObj.readObject();
                                    }
                                }
                                catch (ClassNotFoundException ex)
                                {
                                    Logger.getLogger(redcat.common.event.EventListener.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                catch (IOException ex)
                                {
                                    Logger.getLogger(redcat.common.event.EventListener.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                finally
                                {
                                    if( remoteClient != null )
                                        for(FilterView<ExtendedEvent> fv : registeredViews)
                                            fv.unregisterObserver(remoteClient);
                                    try
                                    {
                                        outObj.close();
                                        inObj.close();
                                        socket.close();
                                    }
                                    catch (IOException ex)
                                    {
                                        Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    catch (NullPointerException ex)
                                    {
                                        Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }).start();
                    }
                }
                catch (IOException ex)
                {
                    Logger.getLogger(redcat.common.event.EventListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
}