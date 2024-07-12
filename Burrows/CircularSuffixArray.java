import edu.princeton.cs.algs4.TrieSET;
import edu.princeton.cs.algs4.TrieST;

public class CircularSuffixArray {
    private String s;
    private int[] indices;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("Null value not allowed");
        this.s = s;
        this.indices = new int[length()];

        TrieST<Integer> trie = new TrieST<>();

        int startPos = 0;
        for (var i = 0; i < length(); i++) {
            StringBuilder builder = new StringBuilder();
                
            for (var n = 0; n < length(); n++) {
                var pos = (startPos + n) % length();

                builder.append(s.charAt(pos));
            }

            var suffix = builder.toString();

            trie.put(suffix, i);
        }

        TrieSET set = new TrieSET();
        for (var key : trie.keys()) {
            set.add(key);
        }

        int n = 0;
        for (var item: set) {
            indices[n] = trie.get(item);

            n++;
        }
    }

    // length of s
    public int length() {
        return s.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        return indices[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        var csa = new CircularSuffixArray("ABRACADABRA!");

        for (var i = 0; i < csa.length(); i++)
            System.out.println("index[" + i + "] = " + csa.index(i));
    }
}