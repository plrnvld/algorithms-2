import java.util.*;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

public class WordNet {

    private String[][] nounsSets;
    private ST<String, List<Integer>> nounTable;

    public Digraph digraph;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        String[] synsetLines = new In(synsets).readAllLines();
        nounsSets = new String[synsetLines.length][];
        nounTable = new ST<>();

        digraph = new Digraph(synsetLines.length);

        for (int i = 0; i < synsetLines.length; i++) {
            String[] nouns = synsetLines[i].split(",")[1].split(" ");
            nounsSets[i] = nouns;

            for (String noun : nouns) {
                if (nounTable.contains(noun))
                    nounTable.get(noun).add(i);
                else {
                    List<Integer> ids = new ArrayList<Integer>();
                    ids.add(i);
                    nounTable.put(noun, ids);
                }
            }
        }

        String[] hypLines = new In(hypernyms).readAllLines();

        for (int i = 0; i < hypLines.length; i++) {
            String[] splits = hypLines[i].split(",");
            String[] destinations = Arrays.copyOfRange(splits, 1, splits.length);

            for (String dest : destinations) {
                digraph.addEdge(i, Integer.parseInt(dest));
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounTable.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException();

        return nounTable.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException();

        return -1;

        // SAP.length
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA
    // and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException();

        return null;

        // Sap.ancestor.join nouns
    }

    // do unit testing of this class
    public static void main(String[] args) {
        System.out.println("Let's work");

        System.out.println(args.length);

        if (args.length == 2) {
            System.out.println("Using arguments: " + args[0] + ", " + args[1]);
            WordNet wordNet = new WordNet(args[0], args[1]);

            System.out.println(wordNet.digraph.toString());
        }
        else if (args.length == 0) {
            final String synsetsFile = "./testfiles/synsets.txt";
            final String hypernymsFile = "./testfiles/hypernyms.txt";
            System.out.println("Using default files: " + synsetsFile + ", " + hypernymsFile);
            WordNet wordNet = new WordNet(synsetsFile, hypernymsFile);

            System.out.println(wordNet.digraph.toString());
        }

    }
}
