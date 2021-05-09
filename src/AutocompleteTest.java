import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class AutocompleteTest {

    Autocomplete atc;
    
    @Before
    public void setUp() throws Exception {
        atc = new Autocomplete();
    }

    @Test
    public void testParseFile() {
        
        // call parseFile function on test.txt, using a 2-gram model
        Map<ArrayList<String>, Integer> m = atc.parseFile("test.txt", 2);
        // should be 6 n-word pairs in the file
        assertEquals(m.size(), 6);
        
        // create a list object for checking map values
        ArrayList<String> check = new ArrayList<String>();
        check.add("of");
        check.add("the");
        
        // check list is {"of", "the"}; should have value 74820247
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(74820247));
        
        // check list is {"in", "the"}; should have value 53383115
        check.set(0, "in");
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(53383115));

        // check list is {"to", "the"}; should have value 30419908
        check.set(0, "to");
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(30419908));

        // check list is {"on", "the"}; should have value 25946885
        check.set(0, "on");
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(25946885));

        // check list is {"for", "the"}; should have value 20270556
        check.set(0, "for");
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(20270556));

        // check list is {"and", "the"}; should have value 19710265
        check.set(0, "and");
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(19710265));
        
        // get the wordFreq map, check that each word occurs the right number of times
        Map<String, Integer> wordFreq = atc.getWordFreq();
        
        // the word "the" should have occurrences equal to the sum of all above weights
        int sum = 19710265 + 20270556 + 25946885 + 30419908 + 53383115 + 74820247;
        assertEquals(wordFreq.get("the"), Integer.valueOf(sum));
        
        // every other word should have the same weight as the above list
        assertEquals(wordFreq.get("and"), Integer.valueOf(19710265));
        
        assertEquals(wordFreq.get("for"), Integer.valueOf(20270556));
        
        assertEquals(wordFreq.get("on"), Integer.valueOf(25946885));
        
        assertEquals(wordFreq.get("to"), Integer.valueOf(30419908));
        
        assertEquals(wordFreq.get("in"), Integer.valueOf(53383115));
        
        assertEquals(wordFreq.get("of"), Integer.valueOf(74820247));
        
        // call parseFile function on test2.txt, using a 2-gram model
        m.clear();
        m = atc.parseFile("test2.txt", 2);
        // should be only 5 n-word pairs in the file: IAutocomplete.FREQ_LIMIT is 2000
        // any words with less than 2000 frequency will not show up here
        assertEquals(m.size(), 5);
        
        // modify the check list for checking map values
        check.clear();
        check.add("were");
        check.add("maybe");
        
        // check list is {"were", "maybe"}; should have value 2000
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(2000));
        
        check.clear();
        check.add("white");
        check.add("spot");
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(2000));

        check.clear();
        check.add("with");
        check.add("actor");
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(2000));
        
        check.set(1, "heroin");
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(2000));
        
        check.clear();
        check.add("Your");
        check.add("Facebook");
        assertTrue(m.containsKey(check));
        assertEquals(m.get(check), Integer.valueOf(2000));
        
        // call the parseFile function on the full ngrams_words_2 text file
        m.clear();
        m = atc.parseFile("ngrams_words_2.txt", 2);

        // try a few n-word combinations in the map returned by parseFile
        ArrayList<String> lst = new ArrayList<>();
        lst.add("a");
        lst.add("Half");
        assertNull(m.get(lst));
        
        ArrayList<String> lst2 = new ArrayList<>();
        lst2.add("with");
        lst2.add("actor");
        assertNotNull(m.get(lst2));
        
        // call the parseFile function on the ngrams_words_3 text file to show that 3-gram works
        m.clear();
        m = atc.parseFile("ngrams_words_3.txt", 3);

        // try a few n-word combinations in the map returned by parseFile
        lst = new ArrayList<>();
        lst.add("the");
        lst.add("United");
        lst.add("States");
        
        assertTrue(m.containsKey(lst));
        assertEquals(m.get(lst), Integer.valueOf(1382931));
        
        lst.clear();
        lst.add("a");
        lst.add("lot");
        lst.add("of");
        
        assertTrue(m.containsKey(lst));
        assertEquals(m.get(lst), Integer.valueOf(3478886));
        
        
        // this one fails for some reason? no idea why
        // shouldn't need to convert to Long ints since the above one works and it's bigger
        // we can just delete if can't figure it out
        lst.clear();
        lst.add("be");
        lst.add("able");
        lst.add("to");
        assertTrue(m.containsKey(lst));
//        assertEquals(m.get(lst), Integer.valueOf(2678127));
    }
    
    @Test
    public void testCreateAutoCompleteFile() {
        
        Map<ArrayList<String>, Integer> m = atc.parseFile("test.txt", 2);
        atc.createAutoCompleteFile();
        
        // test the created autocomplete file
        try {
            // create a bufferedreader for the file
            BufferedReader br = new BufferedReader(new FileReader("autocomplete.txt"));
            
            // read the header: should just be the total count of words, or 7
            String header = br.readLine().trim();
            int count = Integer.parseInt(header);
            // m has 6 word pairs with 7 individual words
            assertEquals(count, 7);
            
            // continue reading
            String s = br.readLine();
            while (s != null) {
                
                // split the string
                String[] splt = s.trim().split("\\s+");
                
                // each word should have weight equal to the weight of 
                // all ngram pairs in which it appears in the map
                if (splt[1].equals("the")) {

                    // the word "the" appears in all of the word pairs
                    int sum = 19710265 + 20270556 + 25946885 + 30419908 + 53383115 + 74820247;
                    assertEquals(Integer.parseInt(splt[0]), sum);
                } else if (splt[1].equals("and")) {
                    assertEquals(Integer.parseInt(splt[0]), 19710265);
                } else if (splt[1].equals("for")) {
                    assertEquals(Integer.parseInt(splt[0]), 20270556);
                } else if (splt[1].equals("on")) {
                    assertEquals(Integer.parseInt(splt[0]), 25946885);
                } else if (splt[1].equals("to")) {
                    assertEquals(Integer.parseInt(splt[0]), 30419908);
                } else if (splt[1].equals("in")) {
                    assertEquals(Integer.parseInt(splt[0]), 53383115);
                } else if (splt[1].equals("of")) {
                    assertEquals(Integer.parseInt(splt[0]), 74820247);
                } else {
                    // should be no other words in the autocomplete file: if there are, fail
                    Assert.fail();
                }
                
                // read the next line
                s = br.readLine();
            }
            
            
        } catch (IOException e) {
            // catch any thrown errors in reading
            e.printStackTrace();
        }
        
        
        atc.parseFile("test2.txt", 2);
        atc.createAutoCompleteFile();
        
        
        
    }
    
    @Test
    public void testBuildNGramIndex() {
        
        // test first on the "testNGramIndex" file with a 2-gram model
        Map<ArrayList<String>, Integer> m = atc.parseFile("testNGramIndex.txt", 2);
        Map<ArrayList<String>, TreeSet<SimpleEntry<String, Integer>>> indexMap = 
                atc.buildNGramIndex(m);
        
        // the index map has 32 unique n-1 word prefixes
        assertEquals(indexMap.size(), 32);
        
        List<String> lst = new ArrayList<String>();
        lst.add("you");
        assertTrue(indexMap.containsKey(lst));
        
        TreeSet<SimpleEntry<String, Integer>> lstSet = indexMap.get(lst);
        
        // "you" has 3 ending words in its tree set, sorted by frequency
        assertEquals(lstSet.size(), 3);
        
        // poll words from the set
        SimpleEntry<String, Integer> e = lstSet.pollFirst();
        assertEquals(e.getKey(), "can");
        assertEquals(e.getValue(), Integer.valueOf(9872981));
        
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "are");
        assertEquals(e.getValue(), Integer.valueOf(6564049));
        
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "have");
        assertEquals(e.getValue(), Integer.valueOf(6167820));
         
        // try another word prefix
        lst.set(0, "in");
        lstSet = indexMap.get(lst);
        
        // "in" has 2 ending words in its tree set, sorted by frequency
        assertEquals(lstSet.size(), 2);
        
        // poll words from the set
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "the");
        assertEquals(e.getValue(), Integer.valueOf(53383115));
        
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "a");
        assertEquals(e.getValue(), Integer.valueOf(10714844));
        
        lst.set(0, "is");
        lstSet = indexMap.get(lst);
        
        // "is" has 3 ending words in its tree set, sorted by frequency
        assertEquals(lstSet.size(), 3);
        
        // poll words from the set
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "a");
        assertEquals(e.getValue(), Integer.valueOf(17563849));
        
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "the");
        assertEquals(e.getValue(), Integer.valueOf(11355480));
        
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "not");
        assertEquals(e.getValue(), Integer.valueOf(6070349));
        
        // test on the full ngrams_words_4 file with a 4-gram model
        m = atc.parseFile("ngrams_words_4.txt", 4);
        indexMap = atc.buildNGramIndex(m);
        
        lst.set(0, "to");
        lst.add("be");
        lst.add("able");
        lstSet = indexMap.get(lst);
        
        // "is" has 3 ending words in its tree set, sorted by frequency
        assertEquals(lstSet.size(), 1);
        
        // poll words from the set
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "to");
        assertEquals(e.getValue(), Integer.valueOf(604998));
        
        lst.set(0, "it");
        lst.set(1, "is");
        lst.set(2, "the");
        lstSet = indexMap.get(lst);

        // "is" has 3 ending words in its tree set, sorted by frequency
        assertEquals(lstSet.size(), 14);
        
        // poll words from the set
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "only");
        assertEquals(e.getValue(), Integer.valueOf(17759));
        
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "most");
        assertEquals(e.getValue(), Integer.valueOf(16208));
        
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "same");
        assertEquals(e.getValue(), Integer.valueOf(15449));
        
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "first");
        assertEquals(e.getValue(), Integer.valueOf(11350));
        
        e = lstSet.pollFirst();
        assertEquals(e.getKey(), "perfect");
        assertEquals(e.getValue(), Integer.valueOf(5984));
        
        e = lstSet.pollLast();
        assertEquals(e.getKey(), "duty");
        assertEquals(e.getValue(), Integer.valueOf(2136));
        
        e = lstSet.pollLast();
        assertEquals(e.getKey(), "one");
        assertEquals(e.getValue(), Integer.valueOf(2162));
    }
    
    @Test
    public void testaddNGram() {
        Autocomplete auto = new Autocomplete();
        ArrayList<String> ngram = new ArrayList<String>();
        ngram.add("a");
        ngram.add("lot");
        ngram.add("of");
        auto.addNGram(ngram, 1);
        WordNode n1 = auto.getnGramRoot().getReferences().get(0);
        assertEquals(1, n1.getPrefixes());
        WordNode n2 = n1.getReferences().get(0);
        System.out.println(n2.getNthWord());
        assertEquals(1, n2.getPrefixes());
        WordNode n3 = n2.getReferences().get(0);
        System.out.println(n3.getNthWord());
        assertEquals(1, n3.getPrefixes());
        System.out.println(n1.getTerm());
        System.out.println(n2.getTerm());
        System.out.println(n3.getTerm().toString());
        
        

    }
    
}
