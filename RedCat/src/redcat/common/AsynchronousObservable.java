package redcat.common;

import java.util.concurrent.*;

/**
 * Classe che implementa l'interfaccia Obervable in maniera multi-threaded.
 * <p>
 * L'implementazione assicura che le operazioni di reportObservers()
 * siano eseguite su thread diversi, onde garantire la piena asincronia
 * tra il passaggio dell'oggetto notificato e l'effettiva esecuzione di report()
 * agli Observer registrati a questo Observable.
 * </p>
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 * @param <T> il tipo dell'oggetto che questo Observable vuole notificare gli Observer registrati
 * @see Observer
 * @see ThreadedObserverProxy
 */
public class AsynchronousObservable<T> implements Observable<T>
{
    /**
     * Mappa che associa un Observer ad un ThreadedObserverProxy, per
     * disaccoppiare l'esecuzione del metodo report() su thread indipendenti
     * da quello dell'oggetto che chiama reportObservers() su questo Observable.
     */
    protected ConcurrentHashMap<Observer<T>, ThreadedObserverProxy<T>> controllers = new ConcurrentHashMap<Observer<T>, ThreadedObserverProxy<T>>();
    
    @Override
    public void registerObserver(Observer<T> observer)
    {
        controllers.put(observer, new ThreadedObserverProxy<T>(observer));
    }
    
    @Override
    public void unregisterObserver(Observer<T> observer)
    {
        controllers.remove(observer);
    }
    
    @Override
    public void reportObservers(T obj)
    {
        for( Observer obs : controllers.values() )
            obs.report(obj);
    }
}