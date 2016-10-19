package redcat.common;

import java.io.*;
import java.util.logging.*;

/**
 * Classe che implementa Observer e che scrive gli Object notificati su un file.
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 * @param <T> tipo dell'oggetto notificato dall'Observable a cui questo Observer è registrato
 */
public class ToFileObserver<T> implements Observer<T>
{
    private PrintWriter logFile;
    
    /**
     * Crea un Observer che registra su file gli Object che gli vengono notificati.
     * <p>
     * Viene utilizzato toString() per rappresentare l'Object nel file.</p>
     * @param logFile File ove registrare gli Object notificati a questo Observer; il file
     * viene aperto in modalità "append"
     * @throws IOException
     */
    public ToFileObserver(File logFile) throws IOException
    {
        this.logFile = new PrintWriter(new FileWriter(logFile, true));
    }
    
    @Override
    public void report(T obj)
    {
        try
        {
            logFile.println(obj.toString());
            logFile.flush();
        }
        catch (Exception ex) { Logger.getLogger(ToFileObserver.class.getName()).log(Level.SEVERE, null, ex); }
    }
}