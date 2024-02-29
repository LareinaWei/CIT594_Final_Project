import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class WordNodeTest {
    
    WordNode wn;

    @Before
    public void setUp() throws Exception {
        String ref = "test";
        ArrayList<String> query = new ArrayList<>();
        query.add("test");
        query.add("phrase");
        int weight = 2000;
        wn = new WordNode(ref, query, weight);
    }

    @Test
    public void testGetNthWord() {
        assertEquals(wn.getNthWord(), "test");
    }
    
    @Test
    public void testSetNthWord() {
        wn.setNthWord("change");
        assertEquals(wn.getNthWord(), "change");
    }
    
    @Test
    public void testGetPhrases() {
        assertEquals(wn.getPhrases(), 0);
    }
    
    @Test
    public void testSetPhrases() {
        wn.setPhrases(2);
        assertEquals(wn.getPhrases(), 2);
    }
    
    @Test
    public void testGetPrefixes() {
        assertEquals(wn.getPrefixes(), 0);
    }
    
    @Test
    public void testSetPrefixes() {
        wn.setPrefixes(2);
        assertEquals(wn.getPrefixes(), 2);
    }
    
    @Test
    public void testGetTerm() {
        ArrayList<String> query = new ArrayList<>();
        query.add("test");
        query.add("phrase");
        assertEquals(wn.getTerm().getPhrase(), query);
        assertEquals(wn.getTerm().getWeight(), 2000);
    }
    
    @Test
    public void testSetTerm() {
        ArrayList<String> query = new ArrayList<>();
        query.add("test");
        query.add("set");
        WordTerm t = new WordTerm(query, 2000);
        wn.setTerm(t);
        assertEquals(wn.getTerm(), t);
    }
    
    @Test
    public void testGetReferences() {
        assertEquals(wn.getReferences().size(), 0);
    }
    
    @Test
    public void testSetReferences() {
        WordNode wn2 = new WordNode("ref");
        ArrayList<WordNode> arr = new ArrayList<>();
        arr.add(wn2);
        wn.setReferences(arr);
        assertEquals(wn.getReferences().size(), 1);
        assertEquals(wn.getReferences().get(0), wn2);
    }
    

}
