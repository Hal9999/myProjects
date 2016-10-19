package redcat.common;

import java.util.concurrent.*;

/**
 * Classe che implementa l'interfaccia Observable su un unico thread.
 * <p>
 * L'implementazione comporta che reportObservers() e i seguenti report()
 * siano eseguiti su un unico thread.</p>
 * <p>
 * Questa implementazione non fornisce alcuna forma di multi threading
 * ed è adatta a Observable i cui tempi di reportObservers() non è importante che siano piccoli.</p>
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 * @param <T> il tipo dell'oggetto che questo Observable vuole notificare agli Observer registrati
 * @see Observer
 * @see ThreadedObserverProxy
 */
public class SynchronousObservable<T> implements Observable<T>
{
    private ConcurrentLinkedQueue<Observer<T>> observers = new ConcurrentLinkedQueue<Observer<T>>();
    
    @Override
    public void registerObserver(Observer<T> observer)
    {
        observers.add(observer);
    }
    
    @Override
    public void unregisterObserver(Observer<T> observer)
    {
        observers.remove(observer);
    }
    
    @Override
    public void reportObservers(T obj)
    {
        for( Observer obs : observers )
            obs.report(obj);
    }
}