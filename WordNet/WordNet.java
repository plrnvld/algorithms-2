import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.DirectedDFS;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;

public class WordNet {

    private final ST<String, List<Integer>> nounTable;
    private final Digraph digraph;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        String[] synsetLines = new In(synsets).readAllLines();
        nounTable = new ST<>();

        digraph = new Digraph(synsetLines.length);

        for (int i = 0; i < synsetLines.length; i++) {
            String[] nouns = synsetLines[i].split(",")[1].split(" ");
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

        int rootIndex = -1;
        int rootCount = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if (digraph.outdegree(i) == 0) {
                rootCount++;
                rootIndex = i;
            }
        }

        if (rootCount != 1)
            throw new IllegalArgumentException("not allowed: digraph contains " + rootCount + " roots");

        DirectedDFS didfs = new DirectedDFS(digraph.reverse(), rootIndex);
        for (int i = 0; i < digraph.V(); i++) {
            if (!didfs.marked(i))
                throw new IllegalArgumentException("not allowed: not all vertices are connected");
        }

        DirectedCycle dicycle = new DirectedCycle(digraph);
        if (dicycle.hasCycle())
            throw new IllegalArgumentException("not allowed: digraph contains cycle");
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
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        SAP sap = new SAP(digraph);
        return sap.length(nounTable.get(nounA), nounTable.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA
    // and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB))
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
        } else if (args.length == 0) {
            final String synsetsFile = "./testfiles/synsets.txt";
            final String hypernymsFile = "./testfiles/hypernyms.txt";
            System.out.println("Using default files: " + synsetsFile + ", " + hypernymsFile);
            WordNet wordNet = new WordNet(synsetsFile, hypernymsFile);

            System.out.println(wordNet.digraph.toString());
        }
    }
}
