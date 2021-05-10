import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TermTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testNullConstructor() {
        try {
            Term term = new Term(null, -1);
            term.getWeight();
            
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testCompare() {
        Term term1 = new Term("a", 1);
        Term term2 = new Term("b", 1);
        assertEquals(term1.compareTo(term2), -1);
    }

    @Test
    public void testToString() {
        Term term = new Term("a", 1);
        assertEquals(term.toString(), "1\ta");
    }
    
    @Test
    public void testGetQuery() {
        Term term = new Term("a", 1);
        assertEquals(term.getTerm(), "a");
    }
    
    @Test
    public void testSetQuery() {
        Term term = new Term("a", 1);
        term.setTerm("b");
        assertEquals(term.getTerm(), "b");
    }
    
    @Test
    public void testGetWeight() {
        Term term = new Term("a", 1);
        assertEquals(term.getWeight(), 1);
    }
    
    @Test
    public void testSetWeight() {
        Term term = new Term("a", 1);
        term.setWeight(2);
        assertEquals(term.getWeight(), 2);
    }
}
