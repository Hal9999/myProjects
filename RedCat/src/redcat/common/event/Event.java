package redcat.common.event;

import java.io.*;

/**
 * Classe specificata dal committente del sistema RedCat per la comunicazione tra sensore e manager.
 * Internamente il sistema usa ExtendedEvent per la rappresentazione di un evento.
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class Event implements Serializable
{
    /**
     * ID del sensore
     */
    long ID;
    /**
     * Priorità dell'evento
     */
    int priority;
    /**
     * Tipo dell'evento
     */
    String type;
    /**
     * Contenuto dell'evento
     */
    String content;
    
    /**
     * Costruttore dell'evento
     * @param ID id del sensore che ha generato l'evento
     * @param priority priorità associata all'evento
     * @param type tipologia dell'evento, ad esemepio "FAULT", "PERFORMANCE" o "MONITORING"
     * @param content contenuto dell'evento
     */
    public Event(long ID, int priority, String type, String content)
    {
        this.ID = ID;
        this.priority = priority;
        this.type = type;
        this.content = content;
    }
}