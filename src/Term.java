
public class Term implements ITerm {

    private String query;
    private long weight;
    
    public Term(String query, long weight) {
        if (query == null || weight < 0) {
            throw new IllegalArgumentException("Not valid input");
        }
        this.query = query;
        this.weight = weight;
    }

    @Override
    public int compareTo(ITerm that) {        
        
        return this.query.compareTo(((Term)that).getTerm());
    }
    
    public String toString() {
        return this.getWeight() + "\t" + this.getTerm();
    }

    public String getTerm() {
        return query;
    }

    public void setTerm(String query) {
        this.query = query;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }
    

}
