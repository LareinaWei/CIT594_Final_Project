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
    private Term term;
    private int prefixes;
    private ArrayList<WordNode> references;
    private String nthWord;

    public WordNode(String query, long weight) {
        if (query == null || weight < 0) {
            throw new IllegalArgumentException("Not valid input");
        }
        Term t = new Term(query, weight);
        this.term = t;
        this.phrases = 0;
        this.prefixes = 0;
        this.references = new ArrayList<WordNode>();
        this.setNthWord(query);
    }

    public WordNode(String ref) {
        this.nthWord = ref;
        this.term = null;
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

    protected Term getTerm() {
        return term;
    }

    protected void setTerm(Term term) {
        this.term = term;
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
