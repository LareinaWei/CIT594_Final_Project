import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.AbstractMap.SimpleEntry;

import org.junit.Before;
import org.junit.Test;



public class AutocompleteTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testParseFile() {
        Autocomplete atc = new Autocomplete();
        Map<ArrayList<String>, Integer> m = atc.parseFile("test.txt", 2);
        assertEquals(m.size(), 6);
        
        Map<String, Integer> wordFreq = atc.wordFreq;
        
//        for (String word: wordFreq.keySet()) {
//            System.out.println(word + " :" + wordFreq.get(word));
//        }
        
        m = atc.parseFile("ngrams_words_2.txt", 2);
        

//        for (ArrayList<String> key : m.keySet()) {
//            for (int i = 0; i < key.size(); i++) {
//                System.out.println(key.get(i));
//            }
//        }
        ArrayList<String> lst = new ArrayList<>();
        lst.add("a");
        lst.add("Half");
        assertNull(m.get(lst));
        ArrayList<String> lst2 = new ArrayList<>();
        lst2.add("with");
        lst2.add("actor");
        assertNotNull(m.get(lst2));
    }
    
    @Test
    public void testCreateAutoCompleteFile() {
        Autocomplete atc = new Autocomplete();
        Map<ArrayList<String>, Integer> m = atc.parseFile("test.txt", 2);
        atc.parseFile("test2.txt", 2);
        atc.createAutoCompleteFile();
    }
    
    @Test
    public void testBuildNGramIndex() {
        Autocomplete atc = new Autocomplete();
        Map<ArrayList<String>, Integer> m = atc.parseFile("testNGramIndex.txt", 2);
        Map<ArrayList<String>, TreeSet<SimpleEntry<String, Integer>>> indexMap = atc.buildNGramIndex(m);
        for (ArrayList<String> phrase: indexMap.keySet()) {
            TreeSet<SimpleEntry<String, Integer>> lastMap = indexMap.get(phrase);
            for (int i = 0; i < phrase.size(); i++) {
                System.out.print(phrase.get(i) + " ");
            }
            System.out.println();
            for (SimpleEntry<String, Integer> entry: lastMap) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

}
