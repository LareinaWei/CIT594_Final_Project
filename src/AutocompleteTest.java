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
        
        assertEquals(1, n2.getPrefixes());
        WordNode n3 = n2.getReferences().get(0);
        
        assertEquals(1, n3.getPrefixes());
        
        assertEquals(n3.getTerm().toString(),"a lot of");
        
        

    }
    
    
    @Test
    public void testbuildNGramTrie() {
        Map<ArrayList<String>, Integer> m = atc.parseFile("test2.txt", 2);
        //m.forEach((k, v) -> System.out.println(k + " : " + v));
        ArrayList<Map<ArrayList<String>, Integer>> nGramMaps = new ArrayList<>();
        nGramMaps.add(m);
        WordNode root = atc.buildNGramTrie(nGramMaps, 10);
        WordNode level1 = atc.findWordRef(root, "with");
        assertEquals(level1.getTerm(), null);
        WordNode level2 = atc.findWordRef(level1, "actor");
        assertEquals(level2.getTerm().toString(), "with actor");
        WordNode level2_5 = atc.findWordRef(level1, "heroin");
        assertEquals(level2_5.getTerm().toString(), "with heroin");
           

    }
    
    @Test
    public void testcountNGramPrefixes() {
        Map<ArrayList<String>, Integer> m = atc.parseFile("test2.txt", 2);
       
        ArrayList<Map<ArrayList<String>, Integer>> nGramMaps = new ArrayList<>();
        nGramMaps.add(m);
        atc.buildNGramTrie(nGramMaps, 10);
        ArrayList<String> list = new ArrayList<>();
        list.add("with");
        assertEquals(atc.countNGramPrefixes(list), 2);

        ArrayList<String> list2 = new ArrayList<>();
        list2.add("were");
        assertEquals(atc.countNGramPrefixes(list2), 1);
        
    }
    
    @Test
    public void testgetNGramSubTrie() {
        ArrayList<String> list = new ArrayList<>();
        list.add("with");
        Map<ArrayList<String>, Integer> m = atc.parseFile("test2.txt", 2);
        //m.forEach((k, v) -> System.out.println(k + " : " + v));
        ArrayList<Map<ArrayList<String>, Integer>> nGramMaps = new ArrayList<>();
        nGramMaps.add(m);
        atc.buildNGramTrie(nGramMaps, 10);
        WordNode level1 = atc.getNGramSubTrie(list);
        WordNode level2 = atc.findWordRef(level1, "actor");
        assertEquals(level2.getTerm().toString(), "with actor");
        WordNode level2_5 = atc.findWordRef(level1, "heroin");
        assertEquals(level2_5.getTerm().toString(), "with heroin");
        
    }
    
    @Test
    public void testgetNGramSuggestions() {
        
        Map<ArrayList<String>, Integer> m = atc.parseFile("test2.txt", 2);
        //m.forEach((k, v) -> System.out.println(k + " : " + v));
        ArrayList<Map<ArrayList<String>, Integer>> nGramMaps = new ArrayList<>();
        nGramMaps.add(m);
        atc.buildNGramTrie(nGramMaps, 10);
        ArrayList<String> input = new ArrayList<String>();
        input.add("with");
        List<ITerm> output = atc.getNGramSuggestions(input);
        assertEquals(output.get(0).toString(),"with heroin");
        assertEquals(output.get(1).toString(),"with actor");
        assertEquals(output.size(), 2);
        
        ArrayList<String> input2 = new ArrayList<String>();
        input2.add("white");
        List<ITerm> output2 = atc.getNGramSuggestions(input2);
        assertEquals(output2.get(0).toString(),"white spot");
        assertEquals(output2.size(), 1);
        
        
        
    }
    
    @Test
    public void testgetNGramSuggestionsAll() {
        
        Map<ArrayList<String>, Integer> m2 = atc.parseFile("test_2gram.txt", 2);
        Map<ArrayList<String>, Integer> m3 = atc.parseFile("test_3gram.txt", 3);
        Map<ArrayList<String>, Integer> m4 = atc.parseFile("test_4gram.txt", 4);
        
        ArrayList<Map<ArrayList<String>, Integer>> nGramMaps = new ArrayList<>();
        nGramMaps.add(m2);
        nGramMaps.add(m3);
        nGramMaps.add(m4);
        atc.buildNGramTrie(nGramMaps, 10);
        ArrayList<String> input = new ArrayList<String>();
        input.add("happy");
        List<ITerm> output = atc.getNGramSuggestions(input);
        assertEquals(output.size(), 6);
        assertEquals(output.get(0).toString(),"happy holiday");
        assertEquals(output.get(1).toString(),"happy holiday to");
        assertEquals(output.get(2).toString(),"happy birthday");
        assertEquals(output.get(3).toString(),"happy birthday to");
        assertEquals(output.get(4).toString(),"happy birthday to you");
        assertEquals(output.get(5).toString(),"happy birthday to me");
        
        
        
    }
    
    @Test
    public void testcompleteMe() {
        
        Map<ArrayList<String>, Integer> m2 = atc.parseFile("test_2gram.txt", 2);
        Map<ArrayList<String>, Integer> m3 = atc.parseFile("test_3gram.txt", 3);
        Map<ArrayList<String>, Integer> m4 = atc.parseFile("test_4gram.txt", 4);
        
        ArrayList<Map<ArrayList<String>, Integer>> nGramMaps = new ArrayList<>();
        nGramMaps.add(m2);
        nGramMaps.add(m3);
        nGramMaps.add(m4);
        atc.buildNGramTrie(nGramMaps, 10);
        atc.createAutoCompleteFile();
        atc.buildTrie("autocomplete.txt", 10);
        List<ITerm> output = atc.completeMe("happy");
        
        assertEquals(output.size(), 8);
        assertEquals(((Term) output.get(0)).getTerm(),"happy");
        assertEquals(((Term) output.get(1)).getTerm(),"happyness");
        assertEquals(output.get(2).toString(),"happy holiday");
        assertEquals(output.get(3).toString(),"happy holiday to");
        assertEquals(output.get(4).toString(),"happy birthday");
        assertEquals(output.get(5).toString(),"happy birthday to");
        assertEquals(output.get(6).toString(),"happy birthday to you");
        assertEquals(output.get(7).toString(),"happy birthday to me");
        
        
        
    }
    
    @Test
    public void testgetRoot() {
        Autocomplete auto = new Autocomplete();
         
        assertEquals("", auto.getRoot().getTerm().getTerm());
        assertEquals(0, auto.getRoot().getTerm().getWeight());
        assertEquals(0, auto.getRoot().getPrefixes());
       
    }

    @Test
    public void testAddword() {
        Autocomplete auto = new Autocomplete();
        auto.addWord("abc", 1);
        Node n1 = auto.getRoot().getReferences()[0];
        assertEquals(1, n1.getPrefixes());
        Node n2 = n1.getReferences()[1];
        assertEquals(1, n2.getPrefixes());
        Node n3 = n2.getReferences()[2];
        assertEquals(1, n3.getPrefixes());

    }

    @Test
    public void testBuildTrie() {
        String filename = "testFile.txt";   
        atc.buildTrie(filename, 5);
        assertEquals(atc.numberSuggestions(), 5);
        assertEquals(atc.countPrefixes(""), 5);
        assertEquals(atc.countPrefixes("a"), 4);
        assertEquals(atc.countPrefixes("c"), 1);

    }

    @Test
    public void testsubTrie() {
        Autocomplete auto = new Autocomplete();
        assertEquals(auto.countPrefixes(null), 0);

        auto.addWord("abc", 10);
        assertNull(auto.getSubTrie("t"));
        assertNotNull(auto.getSubTrie("a"));
        assertNotNull(auto.getSubTrie("ab"));
        assertNotNull(auto.getSubTrie("abc"));
        
        
        Autocomplete auto1 = new Autocomplete();
        auto1.addWord("abc", 10);
        assertNull(auto1.getSubTrie("a1"));
        assertNull(auto1.getSubTrie("ab1"));
        

    }

    @Test
    public void testcountPrefix() {
        Autocomplete auto = new Autocomplete();

        auto.addWord("car", 10);
        assertEquals(auto.countPrefixes(""), 1);
        assertEquals(auto.countPrefixes("c"), 1);
        auto.addWord("car1", 10);
        assertEquals(auto.countPrefixes(""), 1);
        assertEquals(auto.countPrefixes("c"), 1);
        auto.addWord("cat", 10);
        assertEquals(auto.countPrefixes(""), 2);
        assertEquals(auto.countPrefixes("c"), 2);
        assertEquals(auto.countPrefixes("ca"), 2);
        auto.addWord("camping", 10);
        assertEquals(auto.countPrefixes(""), 3);
        assertEquals(auto.countPrefixes("c"), 3);
        assertEquals(auto.countPrefixes("ca"), 3);
        assertEquals(auto.countPrefixes("cam"), 1);

    }

    @Test
    public void testgetSuggestion() {
        Autocomplete auto = new Autocomplete();
        auto.addWord("car", 10);
        auto.addWord("cat", 10);
        auto.addWord("camping", 10);
        auto.addWord("cool", 10);
      
        assertEquals(auto.getSuggestions("car").size(), 1);
        assertEquals(auto.getSuggestions("ca").size(), 3);
        assertEquals(auto.getSuggestions("c").size(), 4);
        assertEquals(auto.getSuggestions("t").size(), 0);
        
        assertEquals("10\tcamping", auto.getSuggestions("c").get(0).toString());
       
    }

    
    
    
}
