import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {
    private final String s;
    private final Integer[] indices;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("Null value not allowed");
        this.s = s;
        this.indices = new Integer[length()];

        for (var i = 0; i < indices.length; i++) {
            indices[i] = i;
        }

        Arrays.sort(indices, new SuffixComparator(s));
    }

    private class SuffixComparator implements Comparator<Integer> {

        private String s;

        public SuffixComparator(String s) {
            this.s = s;
        }

        @Override
        public int compare(Integer shiftLeft1, Integer shiftLeft2) {
            var shift1 = (int) shiftLeft1;
            var shift2 = (int) shiftLeft2;

            if (shift1 == shift2)
                return 0;

            for (var i = 0; i < s.length(); i++) {
                int char1 = s.charAt((shift1 + i) % s.length());
                int char2 = s.charAt((shift2 + i) % s.length());

                if (char1 != char2)
                    return char1 - char2;
            }

            return 0;
        }

    }

    // length of s
    public int length() {
        return s.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length())
            throw new IllegalArgumentException("Index " + i + "not allowed");

        return indices[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        var csa = new CircularSuffixArray("ABRACADABRA!");

        for (var i = 0; i < csa.length(); i++)
            System.out.println("index[" + i + "] = " + csa.index(i));
    }
}