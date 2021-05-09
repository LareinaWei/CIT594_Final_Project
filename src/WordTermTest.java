import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class WordTermTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        ArrayList<String> ngram = new ArrayList<>();
        ngram.add("a");
        ngram.add("lot");
        ngram.add("of");
        WordTerm wt = new WordTerm(ngram, 1);
        System.out.println(wt.toString());
    }

}
