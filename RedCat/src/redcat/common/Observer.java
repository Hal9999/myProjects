package redcat.common;

/**
 * Interfaccia utilizzata dagli oggetti Observable per aggiungere oggetti Observer come listener.
 * Implementa il design pattern Observer.
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 * @param <T> il tipo dell'oggetto notificato dall'Observable a cui questo Observer è registrato
 */
public interface Observer<T>
{
    /**
     * Metodo chiamato dagli Observable a cui questo Observer è "registered".
     * @param obj l'oggetto notificato dall'Observable
     */
    public void report(T obj);
}