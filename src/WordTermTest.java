import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class WordTermTest {

    WordTerm wt;
    
    @Before
    public void setUp() throws Exception {
        ArrayList<String> arr = new ArrayList<>();
        arr.add("test");
        arr.add("phrase");
        wt = new WordTerm(arr, 2000);
    }
    
    @Test
    public void testGetPhrase() {
        ArrayList<String> arr = new ArrayList<>();
        arr.add("test");
        arr.add("phrase");
        assertEquals(wt.getPhrase(), arr);
    }
    
    @Test
    public void testSetPhrase() {
        ArrayList<String> arr = new ArrayList<>();
        arr.add("test");
        arr.add("change");
        wt.setPhrase(arr);
        assertEquals(wt.getPhrase(), arr);
    }
    
    @Test
    public void testGetWeight() {
        assertEquals(wt.getWeight(), 2000);
    }
    
    @Test
    public void testSetWeight() {
        int w = 3000;
        wt.setWeight(w);
        assertEquals(wt.getWeight(), 3000);
    }
    
    @Test
    public void testToString() {
        ArrayList<String> ngram = new ArrayList<>();
        ngram.add("a");
        ngram.add("lot");
        ngram.add("of");
        wt = new WordTerm(ngram, 1);
        assertEquals(wt.toString(), "a lot of");
    }

}
