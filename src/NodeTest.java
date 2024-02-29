import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class NodeTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testNullConsturctor() {
        try {
            Node newNode = new Node(null, -1);
            
        } catch (Exception e) {
            
        }
    }
    
    @Test
    public void testConstructor() {
        Node node = new Node();
        assertNull(node.getTerm());
        assertEquals(0, node.getWords());    
        assertEquals(0, node.getPrefixes());    
    }

    @Test
    public void testGetWords() {
        Node node = new Node("", 0);
        assertEquals(0, node.getWords());    
    }
    
    @Test
    public void testSetWords() {
        Node node = new Node("", 0);
        node.setWords(2);
        assertEquals(2, node.getWords());    
    }
    
    @Test
    public void testGetPrefixes() {
        Node node = new Node("", 0);
        assertEquals(0, node.getPrefixes());    
    }
    
    @Test
    public void testSetPrefixes() {
        Node node = new Node("", 0);
        node.setPrefixes(3);
        assertEquals(3, node.getPrefixes());    
    }
    
    @Test
    public void testGetReference() {
        Node node = new Node("", 0);
        Node[] ref = node.getReferences();
        assertEquals(ref.length, 26);    
    }
    
    @Test
    public void testSetReference() {
        Node node = new Node("", 0);
        Node[] ref = node.getReferences();
        Node node2 = new Node("a", 1);
        ref[0] = node2;
        node.setReferences(ref);
        assertEquals(node2, node.getReferences()[0]);    
    }
    @Test
    public void testSetTerm() {
        Node node = new Node("", 0);
        Term term = new Term("a", 1);
        node.setTerm(term);
        assertEquals(term.getTerm(), ((Term)node.getTerm()).getTerm()); 
        assertEquals(term.getWeight(), ((Term)node.getTerm()).getWeight());    
    }
    
    @Test
    public void testGetTerm() {
        Node node = new Node("", 0);
        Term term = new Term("", 0);
        assertEquals(term.getTerm(), ((Term)node.getTerm()).getTerm()); 
        assertEquals(term.getWeight(), ((Term)node.getTerm()).getWeight());    
    }
}
