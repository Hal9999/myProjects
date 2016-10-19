package redcat.common.event;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test di unità della classe ExtendedEvent.
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ExtendedEventTest
{
    /**
     * Test of serializeToEvent method, of class ExtendedEvent.
     */
    @Test
    public void testSerializeToEvent()
    {
        System.out.println("serializeToEvent");
        
        ExtendedEvent exEvent = new ExtendedEvent();
            exEvent.description = "descrizione";
            exEvent.niceString = "traduzione";
            exEvent.priority = 1000;
            exEvent.sensorID = 1;
            exEvent.time = "oggi";
            exEvent.type = "test";
            exEvent.typeID = 0;
            exEvent.value = "value";
        Event result = exEvent.serializeToEvent();
        
        long expectedID = 1;
        int expectedPriority = 1000;
        String expectedType = "test";
        String expectedContent = "0" + "<::>" + "oggi" + "<::>" + "descrizione" + "<::>" + "traduzione" + "<::>" + "value";
        
        assertEquals(expectedID, result.ID);
        assertEquals(expectedPriority, result.priority);
        assertEquals(expectedType, result.type);
        assertEquals(expectedContent, result.content);
    }

    /**
     * Test of deserializeFromEvent method, of class ExtendedEvent.
     */
    @Test
    public void testDeserializeFromEvent()
    {
        System.out.println("deserializeFromEvent");
        
        long ID = 50;
        int priority = -150;
        String type = "test2";
        String content = "50" + "<::>" + "ieri" + "<::>" + "descriz." + "<::>" + "translation" + "<::>" + "valore";
        
        Event event = new Event(ID, priority, type, content);
        ExtendedEvent expResult = new ExtendedEvent();
            expResult.sensorID = 50;
            expResult.priority = -150;
            expResult.type = "test2";
            expResult.typeID = 50;
            expResult.time = "ieri";
            expResult.description = "descriz.";
            expResult.niceString = "translation";
            expResult.value = "valore";
            
        ExtendedEvent result = ExtendedEvent.deserializeFromEvent(event);
        assertTrue(expResult.equals(result));
        assertEquals(expResult.toString(), result.toString());
    }
    
    /**
     * Test of equals method, of class ExtendedEvent.
     */
    @Test
    public void testEquals()
    {
        System.out.println("equals");
        
        ExtendedEvent e1 = new ExtendedEvent();
            e1.sensorID = 50;
            e1.priority = -150;
            e1.type = "test2";
            e1.typeID = 50;
            e1.time = "ieri";
            e1.description = "descriz.";
            e1.niceString = "translation";
            e1.value = "valore";
        
        ExtendedEvent e2 = new ExtendedEvent();
            e2.sensorID = 50;
            e2.priority = -150;
            e2.type = "test2";
            e2.typeID = 50;
            e2.time = "ieri";
            e2.description = "descriz.";
            e2.niceString = "translation";
            e2.value = "valore";
        
        ExtendedEvent e3 = new ExtendedEvent();
            e3.sensorID = 50;
            e3.priority = -150;
            e3.type = "test3";
            e3.typeID = 50;
            e3.time = "ieri";
            e3.description = "descriz.";
            e3.niceString = "translation";
            e3.value = "valore";
            
        assertTrue(e1.equals(e2));
        assertFalse(e1.equals(e3));
        assertFalse(e2.equals(e3));
    }
}