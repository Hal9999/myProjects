package redcat.common;

/**
 * Interfaccia che esplicita quali metodi una classe deve implementare per supportare il design pattern Observer.
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 * @param <T> il tipo dell'oggetto che un Observable vuole notificare agli Observer registrati.
 */
public interface Observable<T>
{
    /**
     * Registra un Observer
     * @param observer l'Observer da registrare
     */
    public void registerObserver(Observer<T> observer);
    
    /**
     * Rimuove un Observer registrato in precedenza
     * @param observer l'Observer da deregistrare
     */
    public void unregisterObserver(Observer<T> observer);
    
    /**
     * Notifica tutti gli osservatori registrati
     * @param obj l'oggetto che verrà notifcato agli Observer registrati a questo Observable
     */
    public void reportObservers(T obj);
}
