package redcat.common.event;

import java.io.*;

/**
 * Classe che descrive una tipologia di evento.
 * 
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class EventType implements Serializable
{
    /**
     * ID del tipo di evento
     */
    int typeID;
    /**
     * Priorità dell'evento
     */
    int priority;
    /**
     * Tipo dell'evento
     */
    String classification;
    /**
     * Stringa usata per la ricognizione dell'evento da parte del Sensor di RedCat
     */
    String matchString;
    /**
     * Stringa che specifica un nome per la tipologia di evento
     */
    String niceString;
    /**
     * Breve descrizione dell'evento
     */
    String description;
    
//    /**
//     * restituisce una rappresentazione dell'evento sotto forma di stringa
//     * @return la stringa che rappresenta l'oggetto
//     */
//    @Override
//    public String toString()
//    {
//        return "Event Type -> ID: " + typeID + "; Priority: " + priority + "; Classif: " + classification + "; Match: " + matchString + "; Nice: " + niceString + "; Description: " + description;
//    }
}