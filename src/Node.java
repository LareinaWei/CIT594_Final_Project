
/**
 * ==== Attributes ====
 * - words: number of words
 * - term: the ITerm object
 * - prefixes: number of prefixes 
 * - references: Array of references to next/children Nodes
 * 
 * ==== Constructor ====
 * Node(String word, long weight)
 * 
 * @author Your_Name
 */
public class Node {
    //TODO
    private int words;
    private Term term;
    private int prefixes;
    private Node[] references;
    
    public Node(String query, int weight) {
        if (query == null || weight < 0) {
            throw new IllegalArgumentException("Not valid input");
        }
        Term t = new Term(query, weight);
        this.term = t;
        this.words = 0;
        this.prefixes = 0;
        this.references = new Node[26];
    }
    
    public Node() {
        this.term = null;
        this.words = 0;
        this.prefixes = 0;
        this.references = new Node[26];
    }

    protected int getWords() {
        return words;
    }

    protected void setWords(int words) {
        this.words = words;
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

    protected Node[] getReferences() {
        return references;
    }

    protected void setReferences(Node[] references) {
        this.references = references;
    }
    
    

}
