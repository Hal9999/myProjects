package redcat.common;

import java.util.concurrent.*;
import java.util.logging.*;

/**
 * Classe che implementa una forma di Thread Proxy per un Observer.
 * <p>
 * Gli oggetti di questa classe incapsulano un Observer e disaccoppiano
 * l'esecuzione, tramite multi threading, di report().</p>
 * <p>
 * In effetti, questa classe consente ad un Observable di poter velocemente
 * notificare un oggetto agli Observer registrati, demandando l'effettiva
 * esecuzione di report() a thread separati da quello principale.</p>
 * <p>
 * Questa classe è stata principalmente concepita per l'uso da parte della classe AsynchronousObservable</p>
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 * @param <T> il paramentro generico per definire il tipo di dato notificato
 */
public class ThreadedObserverProxy<T> implements Runnable, Observer<T>
{
    private BlockingQueue<T> eventQueue = new LinkedBlockingQueue<T>();
    private Observer myObserver;
    private boolean go = true;
    private Thread thisThread;

    /**
     * Crea un Observer a cui l'Observable farà riferimento.
     * <p>
     * L'Observer creato sostituisce l'Observer incapsulato e ne disaccoppia su un
     * altro thread l'esecuzione di report().</p>
     * @param observer l'Observer da incapsulare
     */
    public ThreadedObserverProxy(Observer<T> observer)
    {
        this.myObserver = observer;
        thisThread = new Thread(this);
        thisThread.start();
    }

    @Override
    public void report(T obj)
    {
        try{ eventQueue.put(obj); }
        catch (InterruptedException ex) { Logger.getLogger(ThreadedObserverProxy.class.getName()).log(Level.SEVERE, null, ex); }
    }
    
    @Override
    public void run()
    {
        while(go) {
            try
            {
                myObserver.report(eventQueue.take());
            } catch (InterruptedException ex) {
                Logger.getLogger(ThreadedObserverProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}