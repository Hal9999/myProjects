package redcat.common.event;

import redcat.common.*;
import redcat.manager.*;

import java.util.*;
import java.util.concurrent.*;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test di unità della classe EventListener.
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class EventListenerTest
{
    private Queue<ExtendedEvent> receivedEvents = new LinkedBlockingQueue<ExtendedEvent>();
    private ExtendedEvent e1, e2, e3, e4;
    
    private AsynchronousQueueModel<ExtendedEvent> model;
    private redcat.common.Observer<ExtendedEvent> view;
    private ManagerController controller;
    
    /**
     * Inizializza le variabili che verranno utilizzate nei test.
     */
    @Before
    public void setUp()
    {
        model = new AsynchronousQueueModel<ExtendedEvent>();
        view = new redcat.common.Observer<ExtendedEvent>()
                {
                    @Override
                    public void report(ExtendedEvent obj) {}
                };
    
        controller = new ManagerController(model, view)
                {
                    @Override
                    public void enqueue(ExtendedEvent event) throws InterruptedException
                    {
                        receivedEvents.add(event);
                    }
                };
        
        e1 = new ExtendedEvent();
            e1.description = "descrizione1";
            e1.niceString = "traduzione1";
            e1.priority = 1000;
            e1.sensorID = 1;
            e1.time = "oggi1";
            e1.type = "test1";
            e1.typeID = 1;
            e1.value = "value1";
        e2 = new ExtendedEvent();
            e2.description = "descrizione2";
            e2.niceString = "traduzione2";
            e2.priority = 2000;
            e2.sensorID = 2;
            e2.time = "oggi2";
            e2.type = "test2";
            e2.typeID = 2;
            e2.value = "value2";
        e3 = new ExtendedEvent();
            e3.description = "descrizione3";
            e3.niceString = "traduzione3";
            e3.priority = 3000;
            e3.sensorID = 3;
            e3.time = "oggi3";
            e3.type = "test3";
            e3.typeID = 3;
            e3.value = "value3";
        e4 = new ExtendedEvent();
            e4.description = "descrizione4";
            e4.niceString = "traduzione4";
            e4.priority = 4000;
            e4.sensorID = 4;
            e4.time = "oggi4";
            e4.type = "test4";
            e4.typeID = 4;
            e4.value = "value4";
    }

    /**
     * Test of go method, of class EventListener.
     * @throws InterruptedException 
     */
    @Test
    public void testGo() throws InterruptedException
    {
        System.out.println("go");
        
        EventListener eventListener = new EventListener(3000, 3001, controller);
        eventListener.go();
        
        Thread.sleep(1000);
        
        Thread tcpThread = new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                TCPProxy tcpSender = new TCPProxy("localhost", 3000);
                tcpSender.report(e1.serializeToEvent());
                tcpSender.report(e2.serializeToEvent());
            }
        });
        
        Thread udpThread = new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                UDPProxy udpSender = new UDPProxy("localhost", 3001);
                udpSender.report(e3.serializeToEvent());
                udpSender.report(e4.serializeToEvent());
            }
        });
        
        tcpThread.start();
        udpThread.start();
        
        tcpThread.join();
        udpThread.join();
        
        Thread.sleep(2000);
        
        assertEquals(receivedEvents.size(), 4);
        
        Map<String, ExtendedEvent> map1 = new HashMap<String, ExtendedEvent>();
        map1.put(e1.type, e1);
        map1.put(e2.type, e2);
        map1.put(e3.type, e3);
        map1.put(e4.type, e4);
        
        Map<String, ExtendedEvent> map2 = new HashMap<String, ExtendedEvent>();
        for(ExtendedEvent ex : receivedEvents)
            map2.put(ex.type, ex);
        
        for(String type : map1.keySet())
            assertTrue( map1.get(type).equals(map2.get(type)) );
        for(String type : map2.keySet())
            assertTrue( map2.get(type).equals(map1.get(type)) );
    }
}