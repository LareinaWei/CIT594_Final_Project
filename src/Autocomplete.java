import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class Autocomplete implements IAutocomplete {

    // class field variables
    private int numSuggestions;
    private Node root = new Node("", 0);
    private WordNode nGramRoot = new WordNode("", 0);
    private Map<String, Integer> wordFreq = new HashMap<>();

    /**
     * <parseFile> Parse the document and return a Map of each
     * n-gram phrase and the times it occurred in the file. (punctuation and special characters
     * removed)
     * Create a file that has every word and its occurred time, in the format that the autocomplete
     * buildTrie function needs.
     *
     * @param filename the file to parse
     * @param n the number of words in the n-gram model
     * @return a Map of list of words in each n-gram phrase and the times it occurred in the file
     */
    @Override
    public Map<ArrayList<String>, Integer> parseFile(String filename, int n) {
        
        // initialize variables for file reading
        String s = null;
        BufferedReader r = null;
        // map to store a list containing a n-length phrase and its frequency
        Map<ArrayList<String>, Integer> map = new HashMap<>();

        try {
            // initialize the bufferedreader with a filereader from input String filename
            r = new BufferedReader(new FileReader(filename));

            // read until reaching the end of the file
            while ((s = r.readLine()) != null) {
                
                // break if the line is empty
                if (s.equals("")) {
                    break;
                }

                // split each input line into weight and an n-gram phrase
                String[] spt = s.trim().split("\\s+");

                // parse weight from the above split string
                int weight = Integer.parseInt(spt[0]);

                // files are organized by frequency; once below the frequency limit, stop reading
                if (weight < IAutocomplete.FREQ_LIMIT) {
                    break;
                }

                // create an ArrayList from the String array above and remove the weight element
                ArrayList<String> nGrams = new ArrayList<String>(Arrays.asList(spt));
                nGrams.remove(0);
                
                // loop through all n words in the nGrams array list
                for (int i = 0; i < n; i++) {
                    // get the String at position i
                    String word = nGrams.get(i);
                    // if the word is not yet in the wordFreq map, put it there with value 0
                    if (this.wordFreq.get(word) == null) {
                        this.wordFreq.put(word, 0);
                    }
                    // increment by weight the count of the of the word in the wordFreq map
                    this.wordFreq.put(word, this.wordFreq.get(word) + weight);
                }

                // put the nGrams list to the above defined map with its weight read from the file
                map.put(nGrams, weight);

            }
            
            // close the buffered reader and return the map
            r.close();
            return map;
        } catch (IOException e) {
            // catch any IO Exceptions during reading
            e.printStackTrace();
        }
        
        // return null if the file reading fails
        return null;
    }

    /**
     * Create a file containing all the words in the files. Each word
     * should occupy a line Words should be written in lexicographic order assign the
     * frequency of each word as weight. The method must store the words into a file named
     * autocomplete.txt
     *
     * @param files the list of the filenames
     */
    @Override
    public void createAutoCompleteFile() {
        
        try {
            // create a fileWriter to link to the file "autocomplete.txt"
            FileWriter myWriter = new FileWriter("autocomplete.txt");
            // write and flush an empty String
            myWriter.write("");
            myWriter.flush();
            // create a bufferedwriter with the fileWriter
            BufferedWriter myBufWriter = new BufferedWriter(myWriter);
            
            // initialize a LinkedHashMap to store word frequencies sorted by weight
            LinkedHashMap<String, Integer> sortedWordFreq = new LinkedHashMap<>();
            
            // using a set of the word frequency map from parseFile sorted by weight, 
            // put each ordered (key, value) pair into the sortedWordFreq map 
            this.getWordFreq().entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEachOrdered(x -> sortedWordFreq.put(x.getKey(), x.getValue()));
            
            // write the header of the autocomplete file: total number of words as a String
            String header = this.getWordFreq().size() + "\n";
            myBufWriter.write(header);
            
            // loop through each key in the sorted word frequency map
            for (String word: sortedWordFreq.keySet()) {
                // write the weight of the word and the word as a string on a line
                myBufWriter.append(sortedWordFreq.get(word) + "  " + word + "\n");
                // flush the buffered writer
                myBufWriter.flush();
            }
            
            // close the buffered writer
            myBufWriter.close();
        } catch (IOException e) {
            // catch any IO exceptions encountered during file writing
            e.printStackTrace();
        }

    }

    /**
     * Adds a new word with its associated weight to the autocomplete Trie
     * 
     * @param word the word to be added to the Trie
     * @param weight the weight of the word
     */
    @Override
    public void addWord(String word, long weight) {

        // check if the word is valid
        int length = word.length();
        for (int i = 0; i < length; i++) {
            // all characters should be alphabetical characters
            if (!Character.isLetter(word.charAt(i))) {
                // do not add a word if it is invalid
                return;
            }
        }

        // add a Node with appropriate weight and String converted to lower case
        String lowerWord = word.toLowerCase();
        this.addNode(lowerWord, weight, this.root, lowerWord);

    }

    /**
     * Helper function used to add a node to the Trie
     * Handles incrementing prefixes and words field variables of the Node class
     * Updates Node references when new nodes are added or changed
     * @param str recursive string, decreasing in length for each iteration
     * @param weight of the Term
     * @param node current Node in recursion; traverses over the length of the trie
     * @param word complete word to be added to the Trie at the final step of recursion
     */
    private void addNode(String str, long weight, Node node, String word) {
        
        // get the first letter in the input String str
        char letter = str.charAt(0);
        // map the int value of the char to have 'a' be 0
        int index = letter - 'a';
        // get the length of the string
        int len = str.length();
        
        // increment the number of prefixes this node corresponds to and get its references
        node.setPrefixes(node.getPrefixes() + 1);
        Node[] ref = node.getReferences();
        // if this node is not a leaf node
        if (len > 1) {
            // if the appropriate character of this node's references list is empty
            if (ref[index] == null) {
                // initialize a new node at the appropriate index and update node's references
                ref[index] = new Node();
                node.setReferences(ref);
            }
        } else if (len == 1) { // if it is a leaf node
            // if the appropriate character of this node's references list is empty
            if (ref[index] == null) {
                // initialize a new node with Term equal to the full-length input word
                ref[index] = new Node(word, weight);
                // increment the word and prefix field variable of the node at index
                ref[index].setWords(ref[index].getWords() + 1);
                ref[index].setPrefixes(ref[index].getPrefixes() + 1);
                // update the node's references list
                node.setReferences(ref);
                // reached a leaf node, so we can break from this function
                return;
            } else { // if the character of this node's references list is not empty
                // set the Node's Term to a new term
                ref[index].setTerm(new Term(word, weight));
                // update the prefix and words field variables of the node
                ref[index].setPrefixes(ref[index].getPrefixes() + 1);
                ref[index].setWords(ref[index].getWords() + 1);
                // update the node's references list
                node.setReferences(ref);
                // reached a leaf node, so we can break from this function
                return;
            }
        }

        // recurse into this function, removing the first char from str and moving to the next node
        this.addNode(str.substring(1), weight, node.getReferences()[index], word);
    }

    /**
     * Initializes the word autocomplete Trie
     *
     * @param filename the file to read all the autocomplete data from each line
     *                 contains a word and its weight This method will call the
     *                 addWord method
     * @param k the maximum number of suggestions that should be displayed
     * @return the root of the Trie You might find the readLine() method in
     *         BufferedReader useful in this situation as it will allow you to
     *         read a file one line at a time.
     */
    @Override
    public Node buildTrie(String filename, int k) {
        
        // declare a filereader and buffered reader for reading
        FileReader fr = null;
        BufferedReader br = null;
        
        try {
            // initialize the filereader and bufferedreader from the input autocomplete file
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            // read the header from the autocomplete file
            br.readLine();
            
            // loop until reaching a break statement
            while (true) {
                // read the next line from the bufferedwriter
                String record = br.readLine();
                // if the next line is null, break from the loop
                if (record == null) {
                    break;
                }
                
                // trim the read line and split on spaces
                String[] spt = record.trim().split("\\s+");
                // if the content of the line is empty strings, break from the loop
                if (spt[0].equals("") || spt[1].equals("")) {
                    break;
                }
                
                // parse the weight from the String array
                long w = Long.parseLong(spt[0]);
                // call the addWord function to add the String in spt[1] to the trie
                this.addWord(spt[1], w);
            }
            
            // close the buffered reader
            br.close();
            // return the root
            return this.root;

        } catch (IOException e) {
            // catch any IOExceptions thrown while reading
            e.printStackTrace();
        }
        
        // return null if file reading fails
        return null;
    }

    /**
     * @return k the the maximum number of suggestions that should be displayed
     */
    @Override
    public int numberSuggestions() {
        return this.numSuggestions;
    }

    /**
     * @param prefix
     * @return the root of the subTrie corresponding to the last character of
     *         the prefix.
     */
    @Override
    public Node getSubTrie(String prefix) {
        // edge cases:
        // return null for a null prefix
        if (prefix == null) {
            return null;
        }
        // if the prefix is an empty string, return the root
        if (prefix.trim().equals("")) {
            return this.root;
        }
        // return null if any character in the prefix is not an alphabetical character
        for (int i = 0; i < prefix.length(); i++) {
            if (!Character.isLetter(prefix.charAt(i))) {
                return null;
            }
        }

        // convert the prefix to lowercase; get the root Node
        String lowerPre = prefix.toLowerCase();
        Node node = this.root;

        // loop through the length of the prefix
        for (int i = 0; i < lowerPre.length(); i++) {
            // get each character from the prefix String
            char ch = lowerPre.charAt(i);
            // map the int value of the char to have 'a' as 0
            int index = ch - 'a';
            
            // if the next Node from the current node is null, return null
            if (node.getReferences()[index] == null) {
                return null;
            }
            // move to the next Node in the prefix string
            node = node.getReferences()[index];
        }

        // return the Node represented by the last char of the prefix String
        return node;
    }

    /**
     * @param prefix
     * @return the number of words that start with prefix.
     */
    @Override
    public int countPrefixes(String prefix) {
        // get the subTrie Node of the input prefix String
        Node subTrie = getSubTrie(prefix);
        // if the Node is null, return 0
        if (subTrie == null) {
            return 0;
        }
        // otherwise, return the number of prefix at that node
        return subTrie.getPrefixes();
    }

    /**
     * This method should not throw an exception
     * @param prefix
     * @return a List containing all the ITerm objects with query starting with
     *         prefix. Return an empty list if there are no ITerm object starting
     *         with prefix.
     */
    @Override
    public List<ITerm> getSuggestions(String prefix) {
        // initialize an empty arraylist to be filled with ITerm objects
        List<ITerm> list = new ArrayList<>();
        // get the subTrie Node corresponding to the input prefix
        Node node = this.getSubTrie(prefix);
        
        // call helper function to traverse the subTrie and return all terms
        this.traverseSubTrie(node, list);
        return list;
    }

    /**
     * Helper function used to traverse the subTrie from getSuggestions
     * Adds ITerm objects to the list to be returned while traversing the Trie
     * @param node current Node in the recursion process
     * @param list to add all ITerm objects to
     */
    private void traverseSubTrie(Node node, List<ITerm> list) {
        // if the current Node is null, do nothing
        if (node == null) {
            return;
        }
        
        // if the Node's Term object represents a word
        if (node.getWords() > 0) {
            // get the term from the node object
            Term t = node.getTerm();
            // get the query String from the term
            String query = t.getTerm();
            // get the term's weight
            long weight = t.getWeight();
            
            // add a new Term object, with appropriate String and weight, to the list
            list.add(new Term(query, weight));
        }
        
        // get the references of the current node and loop through all of them
        Node[] ref = node.getReferences();
        for (int i = 0; i < ref.length; i++) {
            // if the Node at index i of the references list is not null, recurse into it
            if (ref[i] != null) {
                this.traverseSubTrie(ref[i], list);
            }
        }
    }

    /**
     * @param docs a map computed by {@parseFile}
     * @return the forward index: a map of the first n-1 words of all phrases and
     *         the last word of that phrase, the value is a map of different last
     *         words and their frequency.
     *         The values (Map<String, Double>) are sorted by
     *         lexicographic order on the key (tag term).
     *         Each inner map is sorted by descending order on the frequency(its value).
     */
    @Override
    public Map<ArrayList<String>, TreeSet<SimpleEntry<String, Integer>>> 
        buildNGramIndex(Map<ArrayList<String>, Integer> map) {

        // call the createComparator helper function to get a new comparator for sorting
        Comparator<SimpleEntry<String, Integer>> c = this.createComparator();
        // indexMap to map an arrayList of n-1 word sequences to a treeSet of 1-word endings
        Map<ArrayList<String>, TreeSet<SimpleEntry<String, Integer>>> indexMap = new HashMap<>();
        
        // loop through the keyset of the input map
        for (ArrayList<String> ngram: map.keySet()) {
            // get the frequency (value) of the ngram key
            int freq = map.get(ngram);
            // get the final word of the ngram key sequence
            String last = ngram.get(ngram.size() - 1);
            // remove the final word of the ngram key sequence
            ngram.remove(ngram.size() - 1);
            
            // check if the indexMap contains the n-1 word sequence stored in the ngram variable
            if (!indexMap.containsKey(ngram)) {
                // if not, create a new TreeSet to store 1-word endings to the n-1 word sequence
                TreeSet<SimpleEntry<String, Integer>> lastSet =
                        new TreeSet<SimpleEntry<String, Integer>>(c);
                // put the ngram sequence and treeSet into the indexMap
                indexMap.put(ngram, lastSet);
            }

            // create a new SimpleEntry object with key equal to the nth word from the sequence
            // and value equal to the frequency of that entire n-word sequence
            SimpleEntry<String, Integer> s = new SimpleEntry<String, Integer>(last, freq);
            
            // add the simpleEntry object to the treeSet corresponding to the n-1 word sequence
            indexMap.get(ngram).add(s);

        }

        // return the completed index map
        return indexMap;
    }

    /**
     * Helper function used to create a comparator to sort SimpleEntry objects by value
     * @return a new Comparator object
     */
    private Comparator<SimpleEntry<String, Integer>> createComparator() {
        // create a new comparator object
        return new Comparator< SimpleEntry<String, Integer>>() {
            // override the compare method
            public int compare(SimpleEntry<String, Integer> e1, SimpleEntry<String, Integer> e2) {
                // compare by the natural ordering of the Int values
                return e2.getValue().compareTo(e1.getValue());
            }
        };
    }


    /**
     * Initializes the N-Gram suggestions Trie
     *
     * @param nGramIndex a list of maps that the Map of the first n-1 words of all phrases and the
     *                   last word of that phrase, the value is a map of different
     *                   last words and their frequency.
     * @param k the maximum number of suggestions that should be displayed
     * @return the root of the N-Gram Trie
     */
    @Override
    public WordNode buildNGramTrie(
            ArrayList<Map<ArrayList<String>, Map<String, Integer>>> nGramIndex,
            int k) {
        
        
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * This method should not throw an exception
     * @param prefix
     * @return a List containing all the ITerm objects with query starting with
     *         prefix. Return an empty list if there are no ITerm object starting
     *         with prefix.
     */
    @Override
    public List<ITerm> getNGramSuggestions(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * This method takes the user input as parameter and call the letter level
     * autocomplete getSuggestions and phrase level getNGramSuggestions, return
     * the combined suggestion of both functions
     * @param prefix
     * @return a List containing all the ITerm objects with query starting with
     *         prefix. Return an empty list if there are no ITerm object starting
     *         with prefix.
     */
    @Override
    public List<ITerm> completeMe(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    
    /**
     * @return word frequency map
     */
    public Map<String, Integer> getWordFreq() {
        return wordFreq;
    }

    /**
     * @param set word frequency map
     */
    public void setWordFreq(Map<String, Integer> wordFreq) {
        this.wordFreq = wordFreq;
    }

}
