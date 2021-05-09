import java.util.ArrayList;
import java.util.Comparator;

public class WordTerm {
    
    ArrayList<String> phrase;
    int weight;
    
    public WordTerm(ArrayList<String> phrase, int weight) {
        if (phrase == null || phrase.size() == 0 || weight < 0) {
            throw new IllegalArgumentException("Not valid input");
        }
        this.phrase = phrase;
        this.weight = weight;
    }
    
    
    
    protected ArrayList<String> getPhrase() {
        return phrase;
    }


    protected void setPhrase(ArrayList<String> phrase) {
        this.phrase = phrase;
    }

    protected int getWeight() {
        return weight;
    }

    protected void setWeight(int weight) {
        this.weight = weight;
    }

    
    public String toString() {
        if (this.phrase == null || this.phrase.size() == 0) {
            return "";
        }
        String p = "";
        for(int i = 0; i < this.phrase.size() - 1; i++) {
            p += phrase.get(i);
            p += " ";
        }
        p += phrase.get(phrase.size() - 1);
        return p;
    }
    
    public int compareTo(WordTerm that) {        
        
        return this.phrase.toString().compareTo(((WordTerm)that).getPhrase().toString());
    }
    
    
    /**
     * Compares the two terms in descending order by weight.
     * 
     * @return comparator Object
     */
    public static Comparator<WordTerm> byReverseWeightOrder() {
        Comparator<WordTerm> c = new Comparator<WordTerm>() {
            public int compare(WordTerm wordTerm1, WordTerm wordTerm2) {
                return (int) ((wordTerm2).getWeight() - (wordTerm1).getWeight());
            }
        };
        return c;
    }



}
