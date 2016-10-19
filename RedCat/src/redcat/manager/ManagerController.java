package redcat.manager;

import redcat.common.*;
import redcat.common.event.*;
import redcat.ui.ManagerViewGUIMain;

import java.text.*;
import java.util.Date;

/**
 * Classe che implementa il Controller sul Manager.
 *
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ManagerController implements Observer<String>
{
    private AsynchronousQueueModel<ExtendedEvent> model;
    private ManagerViewGUIMain view;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Crea un ManagerController associandolo al Model e alla ManagerViewGUIMain indicategli.
     * @param model il Model da associare a questo ManagerController
     * @param view la View da associare a questo ManagerController
     */
    public ManagerController(AsynchronousQueueModel<ExtendedEvent> model, ManagerViewGUIMain view)
    {
        this.model = model;
        this.view = view;
        
        view.registerObserver(this);
        model.registerObserver(view);
    }
    
    /**
     * Crea un ManagerController associandolo al Model e all'Observer (che agisce da View) indicategli.
     * @param model il Model da associare a questo ManagerController
     * @param observer l'Observer che agisce da View da associare a questo ManagerController
     */
    public ManagerController(AsynchronousQueueModel<ExtendedEvent> model, Observer<ExtendedEvent> observer)
    {
        this.model = model;
        
        model.registerObserver(observer);
    }
    
    @Override
    public void report(String action)
    {
        //in base al contenuto della stringa action faccio qualcosa sul model
        /*
        * go()
        * stop()
        * emptyQueue()
        * queueLength()
        * enqueue(ExtendedEvent myEvent)
        */
        
        ExtendedEvent event = new ExtendedEvent();
            event.typeID = -1;
            event.type = "Manager";
            event.description = "Model changed its status.";
            event.priority = 1;
            event.time = dateFormat.format(new Date());
            
        if( action.equalsIgnoreCase("go") )
            model.go(event);
        else if( action.equalsIgnoreCase("pause") )
            model.stop(event);
        else if( action.equalsIgnoreCase("clear"))
            model.emptyQueue();
    }
    
    /**
     * Accoda sul Model l'ExdendedEvent passato
     * @param event l'ExendedEvent da accodare
     * @throws InterruptedException 
     */
    public void enqueue(ExtendedEvent event) throws InterruptedException
    {
        model.enqueue(event);
    }
}