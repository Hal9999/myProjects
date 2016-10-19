package redcat.common;

/**
 * Classe che implementa Observer e che manda gli Object notificati sullo standard output.
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 * @param <T> tipo dell'oggetto notificato dall'Observable a cui questo Observer è registrato
 */
public class ToCLIObserver<T> implements Observer<T>
{
    @Override
    public void report(T obj)
    {
        System.out.println(obj);
    }   
}