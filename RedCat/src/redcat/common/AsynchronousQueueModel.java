package redcat.common;

import java.util.concurrent.*;
import java.util.logging.*;

/**
 * Classe che implementa il Model del pattern MVC.
 * <p>Come prevede il pattern MVC, alla modifica del Model, che coincide
 * in questa implementazione all'arrivo di un oggetto nella coda, segue
 * la notifica della stessa a tutte le View che si sono registrate
 * a questo Model.</p>
 * <p>Per questo motivo il Model è Observable ed eredita i metodi
 * della classe AsynchronousObservable.</p>
 * <p>L'implementazione del Model poggia su una coda tread-safe,
 * onde consentire a più thread di accodare contemporanemante oggetti.</p>
 * <p>L'implementazione fa in modo che l'accodamento di un oggetto
 * sia asincrona rispetto alla notifica dello stesso alle View registrate.
 * @param <T> tipo degli oggetti che il Model tratta</p>
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class AsynchronousQueueModel<T> extends AsynchronousObservable<T>
{
    /**
     * Coda thread-safe che agisce da buffer per la memorizzazione degli oggetti
     * che sono in attesa di essere notificati agli Observer registrati a questo AsynchronousObservable.
     */
    protected BlockingQueue<T> queue = new LinkedBlockingQueue<T>();
    private Thread thread;
    private final Object lock = new Object();
    private boolean stop = true;
    
    /**
     * Crea un Model vuoto.
     * <p>Viene altresì avviato il thread che si occupa della notifica dei cambiamenti
     * del Model alle View.</p>
     */
    public AsynchronousQueueModel() { go(null); }
    /**
     * avvia il thread che notifica i cambiamenti del Model alle View
     * @param obj 
     */
    public void go(T obj)
    {
        if( stop )
        {
            stop = false;
            if( obj != null ) reportObservers(obj);
            thread = new Thread( new Runnable()
            {
                @Override
                public void run()
                {
                    while(!stop)
                        try { reportObservers(queue.take()); }
                        catch (InterruptedException ex) { Logger.getLogger(AsynchronousQueueModel.class.getName()).log(Level.SEVERE, null, ex); }
                }
            });
            thread.start();
        }
    }
    
    /**
     * Ferma il thread che notifica i cambiamenti del Model alle View
     * @param obj 
     */
    public void stop(T obj)
    {
        if( !stop ) synchronized(lock)
                    {
                        stop = true;
                        if( obj != null ) reportObservers(obj);
                    }
    }
    
    /**
     * Inserisce un oggetto nella coda di notifica.
     * <p>Questo metodo non è bloccante e non dà garanzie circa
     * il momento in cui avviene l'effettiva notifica alle View registrate</p>
     * @param obj l'oggetto da accodare alla coda di notifica
     * @throws InterruptedException 
     */
    public void enqueue(T obj) throws InterruptedException
    {
        queue.put(obj);
    }
    
    /**
     * Svuota la coda dagli oggetti che ancora non sono stati notificati
     */
    public void emptyQueue()
    {
        queue.clear();
    }
    
    /**
     * Restituisce il numero di elementi attualmente contenuti nella coda
     * @return il numero di elementi attualmente in coda
     */
    public int queueLength()
    {
        return queue.size();
    }
    
    /**
     * Restituisce lo stato del Dispatcher, ovvero lo stato di esecuzione
     * del Thread che si occupa della notifica alle View.
     * @return {@code true} se il Dispatcher è in runnig, altrimenti {@code false}
     */
    public boolean isDispatcherRunning()
    {
        return !stop;
    }
}