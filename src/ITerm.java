import java.util.Comparator;

/**
 * @author ericfouh
 */
public interface ITerm
    extends Comparable<ITerm> {
    
    /**
     * Compares the two terms in descending order by weight.
     * 
     * @return comparator Object
     */
    public static Comparator<ITerm> byReverseWeightOrder() {
        Comparator<ITerm> c = new Comparator<ITerm>() {
            public int compare(ITerm term1, ITerm term2) {
                return (int) (((Term)term2).getWeight() - ((Term)term1).getWeight());
            }
        };
        return c;
    }


    /**
     * Compares the two terms in lexicographic order but using only the first r
     * characters of each query.
     * 
     * @param r
     * @return comparator Object
     */
    public static Comparator<ITerm> byPrefixOrder(int r) {
        if (r < 0) {
            throw new IllegalArgumentException();
        }
        
        Comparator<ITerm> c = new Comparator<ITerm>() {
            public int compare(ITerm term1, ITerm term2) {
                String q1 = ((Term)term1).getTerm();
                String q2 = ((Term)term2).getTerm();
                int l1 = q1.length();
                int l2 = q2.length();
                int min = Math.min(l1, l2);
                int l = r;
                if (r > min) {
                    l = min;
                }
                return q1.substring(0, l).compareTo(q2.substring(0, l));
            }
        };
        
        return c;
    }

    // Compares the two terms in lexicographic order by query.
    public int compareTo(ITerm that);


    // Returns a string representation of this term in the following format:
    // the weight, followed by a tab, followed by the query.
    public String toString();

}
