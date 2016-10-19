package redcat.common.event;

import java.io.*;

/**
 * Classe utilizzata in RedCat per la rappresentazione di un evento.
 * <p>Fornisce metodi per passare da oggetti Event a oggetti ExtendedEvent e viceversa.</p>
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ExtendedEvent implements Serializable
{
    /**
     * ID del sensore che ha generato questo ExtendedEvent
     */
    public long sensorID;
    
    /**
     * Priorità dell'evento
     */
    public int priority;
    
    /**
     * Tipo dell'evento
     */
    public String type;
    
    /**
     * ID del tipo di evento
     */
    public int typeID;
    
    /**
     * Momento in cui l'evento è stato generato
     */
    public String time;
    
    /**
     * Breve descrizione dell'evento
     */
    public String description;
    
    /**
     * Nome per la tipologia di questo ExtendedEvent
     */
    public String niceString;
    
    /**
     * Il valore associato a questo ExtendedEvent
     */
    public String value;
    
    /**
     * Crea un nuovo ExtendedEvent vuoto.
     */
    public ExtendedEvent(){}
    
    /**
     * Crea un ExtendedEvent a partire dall'Event passatogli.
     * L'Event passato deve essere un Event restituito da una precedente chiamata a serializeToEvent().
     * @param event l'evento da deserializzare
     * @return l'ExtendedEvent costruito
     */
    public static ExtendedEvent deserializeFromEvent(Event event)
    {
        ExtendedEvent exEvent = new ExtendedEvent();
        
        exEvent.sensorID = event.ID;
        exEvent.priority = event.priority;
        exEvent.type = event.type;
        
        String[] tokens = event.content.split("<::>");
        
        exEvent.typeID = Integer.parseInt(tokens[0]);
        exEvent.time = tokens[1];
        exEvent.description = tokens[2];
        exEvent.niceString = tokens[3];
        exEvent.value = tokens[4];
        
        return exEvent;
    }
    
    /**
     * Costruisce un Event da un ExentededEvent.
     * L'Evento ritornato contiene tutte le informazioni di questo ExtendedEvent e può essere riconvertito
     * in un ExtendedEvent col metodo statico deserializeFromEvent().
     * @return l'Event costruito, contenente le informazioni di questo ExtendedEvent
     */
    public Event serializeToEvent()
    {
        String content = this.typeID + "<::>" + this.time + "<::>" + this.description + "<::>" + this.niceString + "<::>" + this.value;
        
        Event event = new Event(this.sensorID, this.priority, this.type, content);
        
        return event;
    }
    
    /**
     * Genera una rappresentazione in String di questo ExetendedEvent.
     * @return la stringa che rappresenta questo l'ExtendedEvent
     */
    @Override
    public String toString()
    {
        String text = "sID: " + sensorID + " pri: " + priority +
                " type: " + type + " at " + time + " -> " + niceString + ": " + value;
        
        return text;
    }
    
    /**
     * Verifica che questo ExtendedEvent abbia lo stesso contenuto dell'ExtendedEvent passato.
     * @param o
     * @return {@code true} se l'ExtendedEvent passato rappresenta lo stesso contenuto
     * di questo ExtendedEvent, {@code false} atrimenti
     */
    public boolean equals(ExtendedEvent o)
    {       
        return
            (this.sensorID == o.sensorID) && (this.priority == o.priority) && this.type.equals(o.type) &&
            (this.typeID == o.typeID) && this.time.equals(o.time) && this.description.equals(o.description) &&
            this.niceString.equals(o.niceString) && this.value.equals(o.value);
    }
}