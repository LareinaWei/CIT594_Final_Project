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

    private int numSuggestions;
    private Node root = new Node("", 0);
    private WordNode nGramRoot = new WordNode("", 0);
    Map<String, Integer> wordFreq = new HashMap<>();


    @Override
    public Map<ArrayList<String>, Integer> parseFile(String filename, int n) {
        String s = null;
        BufferedReader r = null;
        Map<ArrayList<String>, Integer> map = new HashMap<>();

        try {
            r = new BufferedReader(new FileReader(filename));

            while ((s = r.readLine()) != null) {

                if (s.equals("")) {
                    break;
                }

                String[] spt = s.trim().split("\\s+");


                int weight = 0;

                weight = Integer.valueOf(spt[0]);

                if (weight < FREQ_LIMIT) {
                    break;
                }

                ArrayList<String> nGrams = new ArrayList<String>(Arrays.asList(spt));

                nGrams.remove(0);

                for (int i = 0; i < n; i++) {
                    String word = nGrams.get(i);
                    if (wordFreq.get(word) == null) {
                        wordFreq.put(word, 0);
                    }
                    wordFreq.put(word, wordFreq.get(word) + 1);
                }


                map.put(nGrams, weight);

            }
            r.close();
            return map;
        } catch (Exception e) {

            e.printStackTrace();
        }


        return null;
    }

    @Override
    public void createAutoCompleteFile() {
        try {
            FileWriter myWriter = new FileWriter("autocomplete.txt");
            myWriter.write("");
            myWriter.flush();
            BufferedWriter myBufWriter = new BufferedWriter(myWriter);
            LinkedHashMap<String, Integer> sortedWordFreq = new LinkedHashMap<>();

            wordFreq.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .forEachOrdered(x -> sortedWordFreq.put(x.getKey(), x.getValue()));

            String header = wordFreq.size() + "\n";
            myBufWriter.write(header);
            for (String word: sortedWordFreq.keySet()) {
                myBufWriter.append(sortedWordFreq.get(word) + "  " + word + "\n");
                myBufWriter.flush();
            }
            myBufWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void addWord(String word, long weight) {

        // check if the word is valid
        int length = word.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isLetter(word.charAt(i))) {
                return;
            }
        }

        // turn the word to lower case
        String lowerWord = word.toLowerCase();
        this.addNode(lowerWord, weight, this.root, lowerWord);

    }


    private void addNode(String str, long weight, Node node, String word) {

        char letter = str.charAt(0);
        int index = letter - 'a';
        int len = str.length();
        node.setPrefixes(node.getPrefixes() + 1);
        Node[] ref = node.getReferences();
        // If there is no node corresponds to the first char and
        // this is not a leaf node
        if (len > 1) {
            if (ref[index] == null) {
                ref[index] = new Node();
                node.setReferences(ref);
            }
        } else if (len == 1) { //if it is a leaf node
            if (ref[index] == null) {
                // if there is no node corresponds to the char
                ref[index] = new Node(word, weight);
                ref[index].setWords(ref[index].getWords() + 1);
                ref[index].setPrefixes(ref[index].getPrefixes() + 1);
                node.setReferences(ref);
                return;
            } else {
                // if there is a corresponding node
                // set the term, prefixes and words of the node
                ref[index].setTerm(new Term(word, weight));
                ref[index].setPrefixes(ref[index].getPrefixes() + 1);
                ref[index].setWords(ref[index].getWords() + 1);
                node.setReferences(ref);
                return;
            }
        }

        this.addNode(str.substring(1), weight, node.getReferences()[index], word);
    }

    @Override
    public Node buildTrie(String filename, int k) {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            String head = br.readLine();
            while (true) {
                String record = br.readLine();
                if (record == null) {
                    break;
                }
                String[] spt = record.trim().split("\\s+");
                if (spt[0].equals("") || spt[1].equals("")) {
                    break;
                }
                long w = Long.parseLong(spt[0]);
                this.addWord(spt[1], w);
            }
            br.close();
            return this.root;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int numberSuggestions() {
        return this.numSuggestions;
    }

    @Override
    public Node getSubTrie(String prefix) {
        if (prefix == null) {
            return null;
        }
        if (prefix.trim().equals("")) {
            return this.root;
        }
        for (int i = 0; i < prefix.length(); i++) {
            if (!Character.isLetter(prefix.charAt(i))) {
                return null;
            }
        }

        String lowerPre = prefix.toLowerCase();
        Node node = this.root;

        for (int i = 0; i < lowerPre.length(); i++) {
            char ch = lowerPre.charAt(i);
            int index = ch - 'a';
            if (node.getReferences()[index] == null) {
                return null;
            }
            node = node.getReferences()[index];
        }

        return node;
    }

    @Override
    public int countPrefixes(String prefix) {
        Node subTrie = getSubTrie(prefix);
        if (subTrie == null) {
            return 0;
        }
        return subTrie.getPrefixes();
    }

    @Override
    public List<ITerm> getSuggestions(String prefix) {
        List<ITerm> list = new ArrayList<>();
        Node node = this.getSubTrie(prefix);
        // call helper function to traverse the subTrie and return all terms
        this.traverseSubTrie(node, list);
        return list;
    }

    private void traverseSubTrie(Node node, List<ITerm> list) {
        if (node == null) {
            return;
        }
        // if the word already represents a word
        if (node.getWords() > 0) {
            Term t = node.getTerm();
            String query = t.getTerm();
            long weight = t.getWeight();
            list.add(new Term(query, weight));
        }
        Node[] ref = node.getReferences();
        for (int i = 0; i < ref.length; i++) {
            if (ref[i] != null) {
                this.traverseSubTrie(ref[i], list);
            }
        }
    }

    @Override
    public Map<ArrayList<String>, TreeSet<SimpleEntry<String, Integer>>> buildNGramIndex(Map<ArrayList<String>, Integer> map) {

        Comparator c = this.createComparator();
        Map<ArrayList<String>, TreeSet<SimpleEntry<String, Integer>>> indexMap = new HashMap<>();
        for (ArrayList<String> ngram: map.keySet()){
            int freq = map.get(ngram);
            String last = ngram.get(ngram.size()-1);
            ngram.remove(ngram.size()-1);
            if (!indexMap.containsKey(ngram)) {
                TreeSet<SimpleEntry<String, Integer>> lastSet =
                new TreeSet<SimpleEntry<String, Integer>>(c);
                indexMap.put(ngram, lastSet);
            }

            SimpleEntry<String, Integer> s = new SimpleEntry<String, Integer>(last, freq);
            indexMap.get(ngram).add(s);

        }

        return indexMap;
    }

    private Comparator<SimpleEntry<String, Integer>> createComparator() {
           return new Comparator< SimpleEntry<String, Integer>>() {
               public int compare(SimpleEntry<String, Integer> e1, SimpleEntry<String, Integer> e2) {
                   return e2.getValue().compareTo(e1.getValue());
                   }
               };
       }




    @Override
    public WordNode buildNGramTrie(ArrayList<Map<ArrayList<String>, Map<String, Integer>>> nGramIndex,
            int k) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ITerm> getNGramSuggestions(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ITerm> completeMe(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

}
