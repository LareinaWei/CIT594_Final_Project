import java.util.ArrayList;

/**
 * ==== Attributes ====
 * - phrases: number of phrases
 * - term: the ITerm object
 * - prefixes: number of prefixes
 * - references: Array of references to next/children Nodes
 *
 * ==== Constructor ====
 * WordNode(String query, long weight)
 *
 * @author Your_Name
 */
public class WordNode {
    //TODO
    private int phrases;
    private WordTerm wordTerm;
    private int prefixes;
    private ArrayList<WordNode> references;
    private String nthWord;

    public WordNode(String ref, ArrayList<String> query, int weight) {
        if (query == null || weight < 0) {
            throw new IllegalArgumentException("Not valid input");
        }
        this.nthWord = ref;
        this.wordTerm = new WordTerm(query, weight);
        this.phrases = 0;
        this.prefixes = 0;
        this.references = new ArrayList<WordNode>();
    }

    public WordNode(String ref) {
        this.nthWord = ref;
        this.wordTerm = null;
        this.phrases = 0;
        this.prefixes = 0;
        this.references = new ArrayList<WordNode>();
    }
    

    protected int getPhrases() {
        return phrases;
    }

    protected void setPhrases(int phrases) {
        this.phrases = phrases;
    }

    protected WordTerm getTerm() {
        return wordTerm;
    }

    protected void setTerm(WordTerm term) {
        this.wordTerm = term;
    }

    protected int getPrefixes() {
        return prefixes;
    }

    protected void setPrefixes(int prefixes) {
        this.prefixes = prefixes;
    }

    protected ArrayList<WordNode> getReferences() {
        return references;
    }

    protected void setReferences(ArrayList<WordNode> references) {
        this.references = references;
    }


    protected String getNthWord() {
        return nthWord;
    }


    protected void setNthWord(String nthWord) {
        this.nthWord = nthWord;
    }

    
    

}
