package redcat.client;

import redcat.common.*;
import redcat.common.event.*;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * Classe che implementa il componente Client di RedCat.
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class Client extends AsynchronousObservable<ExtendedEvent> implements Serializable
{
    private String login;
    private String password;
    private String[] interests;

    private transient String address;
    private transient int port;
    
    private transient boolean stop = false;
    private transient ObjectOutputStream outObj;
    private transient ObjectInputStream inObj;
    private transient Socket socket;
    private transient boolean loginSuccess = false;
    private transient ExecutorService threadExecutor;
    private transient Future<Boolean> clientLoop;
    
    /**
     * Crea un Client, fornendo le coordinate per la connessione al server e
     * la tipologia di informazioni richieste al Manager.
     * @param login il nome utente
     * @param password la password per l'autenticazione dell'utente
     * @param interests le informazioni richieste
     * @param address l'indirizzo del Manager
     * @param port la porta ove il Manager ascolta le connessioni dei client
     */
    public Client(String login, String password, String[] interests, String address, int port)
    {
        this.login = login;
        this.password = password;
        this.interests = interests;
        this.address = address;
        this.port = port;
    }
    
    /**
     * Restituisce il nome utente del Client
     * @return il nome utente
     */
    String getLogin() { return login; }
    
    /**
     * Restituisce la password del Client
     * @return la password
     */
    String getPassword() { return password; }
    
    /**
     * Collega questo Client al Manager specificato nel costruttore
     * e tenta l'autenticazione.
     * @return {@code true} se l'utente è stato autenticato, {@code false} altrimenti
     * @throws UnknownHostException
     * @throws IOException
     * @throws ClassNotFoundException
     * @see ClientAuthenticator
     */
    public boolean connect() throws UnknownHostException, IOException, ClassNotFoundException
    {
        socket = new Socket(address, port);
        outObj = new ObjectOutputStream( socket.getOutputStream() );
        inObj = new ObjectInputStream( socket.getInputStream() );

        outObj.writeObject(this);
        outObj.flush();
        
        return loginSuccess = (Boolean)inObj.readObject();
    }
    
    /**
     * Avvia la ricezione degli ExtendedEvent in arrivo dal Manager, se autenticato.
     * @return {@code true} se il Client si è avviato, altrimenti {@code false}
     */
    public boolean go()
    {
        if( loginSuccess )
        {
            Callable<Boolean> threadLoop = new Callable<Boolean>()
            {
                @Override
                public Boolean call() throws Exception
                {
                    while(!stop) reportObservers( (ExtendedEvent)inObj.readObject() );

                    outObj.writeObject(new Byte( (byte)0 ));
                    outObj.flush();
                    outObj.close();
                    inObj.close();
                    socket.close();
                    return true;
                }
            };
            threadExecutor = Executors.newSingleThreadExecutor();
            clientLoop = threadExecutor.submit(threadLoop);
            return true;
        }
        else return false;
    }
    
    /**
     * Segnala a questo Client di terminare la ricezione di dati e
     * di chiudere la connessione.
     */
    public void stop()
    {
        this.stop = true;
        
    }
    
    /**
     * Si blocca in attesa che il Client termini i thread che riceve
     * i dati dal Manager.
     * Se la negoziazione si conclude senza eccezioni, il metodo ritorna {@code true},
     * altrimenti l'eccezione viene propagata da questo metodo.
     * @return {@code true} in caso di negoziazione terminata senza errori
     * @throws InterruptedException
     * @throws Throwable 
     */
    public boolean awaitTermination() throws InterruptedException, Throwable
    {
        try
        {
            return clientLoop.get();
        }
        catch (ExecutionException ex)
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            throw ex.getCause();
        }
        finally
        {
            threadExecutor.shutdown();
        }
    }
    
    /**
     * Restituisce l'oggetto RemoteClient che rappresenta questo Client
     * in un sistema remoto.
     * <p>Chi vuole comunicare con un Client che sta in una macchina non locale può,
     * ottenuta una copia di questo Client, richiamare questo metodo per ottenere
     * un RemoteClient, che rappresnta l'oggetto proxy per il Client remoto nella
     * macchina locale.
     * @param socket la socket con cui la copia di questo Client è stata ricevuta
     * @param objIn lo ObjectInputStream correlato alla socket
     * @param objOut lo ObjectOutputStream correlato alla socket
     * @return il RemoteClient che rappresenta questo Client locale nella macchina remota
     * @throws IOException
     * @see RemoteClient
     */
    public RemoteClient getClientRemoteProxy(Socket socket, ObjectInputStream objIn, ObjectOutputStream objOut) throws IOException
    {
        return new RemoteClient(socket, objIn, objOut, this.interests);
    }
}