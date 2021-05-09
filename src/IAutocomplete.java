import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface IAutocomplete {

    
    public static final int FREQ_LIMIT = 2000;
    
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
   public Map<ArrayList<String>, Integer> parseFile(String filename, int n);

   /**
     * Create a file containing all the words in the files. Each word
     * should occupy a line Words should be written in lexicographic order assign the
     * frequency of each word as weight. The method must store the words into a file named
     * autocomplete.txt
     *
     * @param files the list of the filenames
     */
   public void createAutoCompleteFile();



   /**
   * Adds a new word with its associated weight to the Trie
   *
   * @param word the word to be added to the Trie
   * @param weight the weight of the word
   */
   public void addWord(String word, long weight);


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
    public Node buildTrie(String filename, int k);


    /**
     * @return k the the maximum number of suggestions that should be displayed
     */
    public int numberSuggestions();

    /**
     * @param prefix
     * @return the root of the subTrie corresponding to the last character of
     *         the prefix.
     */
    public Node getSubTrie(String prefix);


    /**
     * @param prefix
     * @return the number of words that start with prefix.
     */
    public int countPrefixes(String prefix);


    /**
     * This method should not throw an exception
     * @param prefix
     * @return a List containing all the ITerm objects with query starting with
     *         prefix. Return an empty list if there are no ITerm object starting
     *         with prefix.
     */
    public List<ITerm> getSuggestions(String prefix);

    /**
      * @param docs a map computed by {@parseFile}
      * @return the forward index: a map of the first n-1 words of all phrases and
      *         the last word of that phrase, the value is a map of different last
      *         words and their frequency.
      *         The values (Map<String, Double>) are sorted by
      *         lexicographic order on the key (tag term).
      *         Each inner map is sorted by descending order on the frequency(its value).
      */
    public Map<ArrayList<String>, TreeSet<SimpleEntry<String, Integer>>> buildNGramIndex(Map<ArrayList<String>, Integer> map);


    /**
     * Initializes the N-Gram suggestions Trie
     *
     * @param nGramIndex a list of maps that the Map of the first n-1 words of all phrases and the
     *                   last word of that phrase, the value is a map of different
     *                   last words and their frequency.
     * @param k the maximum number of suggestions that should be displayed
     * @return the root of the N-Gram Trie
     */
    public WordNode buildNGramTrie(ArrayList<Map<ArrayList<String>, Integer>> nGramIndex, int k);


    /**
     * This method should not throw an exception
     * @param prefix
     * @return a List containing all the ITerm objects with query starting with
     *         prefix. Return an empty list if there are no ITerm object starting
     *         with prefix.
     */
    public List<WordTerm> getNGramSuggestions(String prefix);


    /**
     * This method takes the user input as parameter and call the letter level
     * autocomplete getSuggestions and phrase level getNGramSuggestions, return
     * the combined suggestion of both functions
     * @param prefix
     * @return a List containing all the ITerm objects with query starting with
     *         prefix. Return an empty list if there are no ITerm object starting
     *         with prefix.
     */
     public List<ITerm> completeMe(String prefix);





}
