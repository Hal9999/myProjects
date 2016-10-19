package redcat.manager;

import redcat.common.*;
import redcat.common.event.*;

/**
 * Classe astratta che specifica il metodo report(Event e) che le
 * sottoclassi devono implementare.
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public abstract class ManagerProxy implements Observer<Event>
{
    /**
     * Notifica la ricezione dell'evento
     * @param e l'evento da notificare
     */
    @Override
    abstract public void report(Event e);
}