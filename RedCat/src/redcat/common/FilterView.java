package redcat.common;

import java.util.concurrent.*;
import java.util.logging.*;

/**
 * Classe che definisce una View con filtro del patterno MVC.
 * <p>Implementando il metodo astratto isInteresting(), le classi derivate
 * possono applicare i loro filtri personalizzati per stabilire se l'oggetto
 * loro notificato è di loro interesse o meno.</p>
 * <p>Questa classe agisce da filtro e, se l'oggetto è di interesse per il filtro,
 * allora l'oggetto viene notificato agli Observer di questo FilterView,
 * che è un Observable.</p>
 * @param <T> tipo di oggetto di cui il filtro deve potere stabilire l'interesse o meno.
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public abstract class FilterView<T> extends AsynchronousObservable<T> implements Observer<T>, Runnable
{
    /**
     * Stringa che descrive il tipo di filtro applicato da questa FilterView per decidere quali oggetti
     * sono da riportare agli Observer di questa FilterView.
     */
    protected String type;
    /**
     * Coda degli oggetti in attesa di essere filtrati.
     */
    protected BlockingQueue<T> objQueue = new LinkedBlockingQueue<T>();
    private boolean stop = false;
    private Thread thread;
    private Observable<T> observable;
    
    /**
     * Crea una FilterView associandogli una stringa che ne descrive
     * il filtro e l'Observer a cui notificare gli oggetti che soddisfano il filtro di questa FilterView.
     * @param observable l'Observable a cui notificare gli oggetti che rispettano le condizioni del filtro
     * @param type la stringa che descrive i criteri di filtraggio attuati dal filtro sugli oggetti notificati
     * dall'Observable a cui questa FilterView è registrata
     */
    public FilterView(Observable<T> observable, String type)
    {
        if( observable == null || type == null) throw new NullPointerException();
        this.observable = observable;
        this.type = type;
        
        observable.registerObserver(this);
    }
    
    /**
     * Restituisce la stringa che descrive il filtraggio di questa FilterView
     * @return la stringa contenente il tipo di view
     */
    public String getType()
    {
        return type;
    }
    
    @Override
    public void run()
    {
        while(!stop)
            try { reportObservers( objQueue.take() ); }
            catch (InterruptedException ex)
                { Logger.getLogger(AsynchronousQueueModel.class.getName()).log(Level.SEVERE, null, ex); }
    }
    
    /**
     * Avvia il thread della FilterView.
     * @return questa FilterView
     */
    public synchronized FilterView<T> go()
    {
        this.stop = false;
        this.thread = new Thread(this);
        this.thread.start();
        
        return this;
    }
    
    /**
     * Ferma il thread della FilterView
     */
    public synchronized void stop()
    {
        if(!stop)
        {
            stop = true;
            thread.interrupt();
        }
    }
    
    @Override
    public void report(T obj)
    {
        if( isInteresting(obj) )
            try { objQueue.put(obj); }
            catch (InterruptedException ex)
                { Logger.getLogger(FilterView.class.getName()).log(Level.SEVERE, null, ex); }
    }
    
    /**
     * Stabilisce se l'oggetto passato soddifa il filtro di questa FilterView.
     * @param obj l'oggetto da controllare
     * @return {@code true} se l'oggetto soddisfa il filtro, {@code false} altrimenti
     */
    protected abstract boolean isInteresting(T obj);
}