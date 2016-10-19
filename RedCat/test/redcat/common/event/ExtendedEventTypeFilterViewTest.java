package redcat.common.event;

import redcat.common.Observable;

import org.junit.*;
import static org.junit.Assert.*;
import redcat.common.*;

/**
 * Test di unità della classe ExtendedEventTypeFilterView.
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class ExtendedEventTypeFilterViewTest
{
    /**
     * Test of isInteresting method, of class ExtendedEventTypeFilterView.
     */
    @Test
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public void testIsInteresting()
    {
        System.out.println("isInteresting");
        Observable<ExtendedEvent> observable = new Observable<ExtendedEvent>()
                                               {
                                                   @Override
                                                   public void registerObserver(Observer<ExtendedEvent> observer) { }
                                                   @Override
                                                   public void unregisterObserver(Observer<ExtendedEvent> observer) { }
                                                   @Override
                                                   public void reportObservers(ExtendedEvent obj) { }
                                               };
        
        ExtendedEvent e1 = new ExtendedEvent();
        ExtendedEvent e2 = new ExtendedEvent();
        ExtendedEvent e3 = new ExtendedEvent();
        ExtendedEvent e4 = new ExtendedEvent();
        e1.type = "type1";
        e2.type = "type2";
        e3.type = "type3";
        e4.type = "type4";
        
        ExtendedEventTypeFilterView filter1 = new ExtendedEventTypeFilterView(observable, "type1");
        assertTrue(filter1.isInteresting(e1));
        assertFalse(filter1.isInteresting(e2));
        assertFalse(filter1.isInteresting(e3));
        assertFalse(filter1.isInteresting(e4));
        
        ExtendedEventTypeFilterView filter2 = new ExtendedEventTypeFilterView(observable, "type5");
        assertFalse(filter2.isInteresting(e1));
        assertFalse(filter2.isInteresting(e2));
        assertFalse(filter2.isInteresting(e3));
        assertFalse(filter2.isInteresting(e4));
        
        try
        {
            new ExtendedEventTypeFilterView(null, "type");
            fail();
        }
        catch(NullPointerException ex) { }
        
        try
        {
            new ExtendedEventTypeFilterView(observable, null);
            fail();
        }
        catch(NullPointerException ex) { }
        
        try
        {
            new ExtendedEventTypeFilterView(null, null);
            fail();
        }
        catch(NullPointerException ex) { }
    }
}
