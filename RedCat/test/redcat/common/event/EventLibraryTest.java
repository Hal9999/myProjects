package redcat.common.event;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.xml.sax.*;

/**
 * Test di unità della classe EventLibrary.
 * @author Lanzafame Flavio, Russo Giorgia, Spadaro Stefano, Torrelli Denisia
 */
public class EventLibraryTest
{
    /**
     * Test of collection constructor, of class EventLibrary.
     */
    @Test
    public void testCollectionConstructor()
    {
        Set<EventType> set = new HashSet<EventType>();
        EventType t1 = new EventType();
            t1.typeID = 1;
            t1.priority = 10;
            t1.classification = "class1";
            t1.matchString = "match1";
            t1.niceString = "nice1";
            t1.description = "description1";
        set.add(t1);
        
        EventType t2 = new EventType();
            t2.typeID = 2;
            t2.priority = 20;
            t2.classification = "class2";
            t2.matchString = "match2";
            t2.niceString = "nice2";
            t2.description = "description2";
        set.add(t2);
        
        EventLibrary library = new EventLibrary(set);
        
        ExtendedEvent seek1 = library.seek("match1", "");
        assertEquals(seek1.typeID, 1);
        assertEquals(seek1.priority, 10);
        assertEquals(seek1.type, "class1");
        assertEquals(seek1.niceString, "nice1");
        assertEquals(seek1.description, "description1");
        
        ExtendedEvent seek2 = library.seek("match2", "");
        assertEquals(seek2.typeID, 2);
        assertEquals(seek2.priority, 20);
        assertEquals(seek2.type, "class2");
        assertEquals(seek2.niceString, "nice2");
        assertEquals(seek2.description, "description2");
        
        assertNull(library.seek("match3", null));
        assertNull(library.seek("", null));
        assertNull(library.seek(null, null));
    }
    
    /**
     * Test of xml constructor, of class EventLibrary.
     * @throws ParserConfigurationException 
     * @throws SAXException
     * @throws IOException  
     */
    @Test
    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public void testXMLContructor() throws ParserConfigurationException, SAXException, IOException
    {
        new EventLibrary(new File("test/redcat/testfiles/eventTypeLibrary.xml"));
        
        try
        {
            new EventLibrary(new File("test/redcat/testfiles/eventTypeLibrary_NO.xml"));
            fail();
        }
        catch( IOException ex){}
        
        try
        {
            new EventLibrary(new File("test/redcat/testfiles/eventTypeLibraryWrong1.xml"));
            fail();
        }
        catch( SAXException ex){}
        
        try
        {
            new EventLibrary(new File("test/redcat/testfiles/eventTypeLibraryWrong2.xml"));
            fail();
        }
        catch( SAXException ex){}
        
    }
    
    /**
     * Test of seek method, of class EventLibrary.
     * @throws ParserConfigurationException 
     * @throws SAXException
     * @throws IOException  
     */
    @Test
    public void testSeek() throws ParserConfigurationException, SAXException, IOException
    {
        System.out.println("seek");
        
        EventLibrary library = new EventLibrary(new File("test/redcat/testfiles/eventTypeLibrary.xml"));
        
        ExtendedEvent seek1 = library.seek("match1", "value1");
            assertEquals(seek1.priority, 1);
            assertEquals(seek1.type, "class1");
            assertEquals(seek1.typeID, 10);
            assertEquals(seek1.description, "descr1");
            assertEquals(seek1.niceString, "nice1");
            assertEquals(seek1.value, "value1");
        
        ExtendedEvent seek2 = library.seek("match2", "value2");
            assertEquals(seek2.priority, 2);
            assertEquals(seek2.type, "class2");
            assertEquals(seek2.typeID, 20);
            assertEquals(seek2.description, "descr2");
            assertEquals(seek2.niceString, "nice2");
            assertEquals(seek2.value, "value2");
            
        ExtendedEvent seek3 = library.seek("match3", "value3");
            assertEquals(seek3.priority, 3);
            assertEquals(seek3.type, "class3");
            assertEquals(seek3.typeID, 30);
            assertEquals(seek3.description, "descr3");
            assertEquals(seek3.niceString, "nice3");
            assertEquals(seek3.value, "value3");
        
        assertNull(library.seek("match999", "value999"));
    }
}
