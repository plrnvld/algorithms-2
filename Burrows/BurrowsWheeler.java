import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;

import java.util.Arrays;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        var builder = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) {
            var charVal = BinaryStdIn.readChar(8);
            builder.append(charVal);

        }

        var text = builder.toString();
        var circularSuffixArray = new CircularSuffixArray(text);

        var originalIndex = -1;
        for (var i = 0; i < circularSuffixArray.length() && originalIndex == -1; i++) {
            if (circularSuffixArray.index(i) == 0)
                originalIndex = i;
        }

        BinaryStdOut.write(originalIndex);

        for (var i = 0; i < circularSuffixArray.length(); i++) {
            var index = circularSuffixArray.index(i);
            var pos = Math.floorMod(index - 1, circularSuffixArray.length());

            BinaryStdOut.write(text.charAt(pos));
        }

        BinaryStdOut.flush();

    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        // Read input
        var first = BinaryStdIn.readInt();
        var queue = new Queue<Character>();

        while (!BinaryStdIn.isEmpty()) {
            var nextChar = BinaryStdIn.readChar(8);
            queue.enqueue(Character.valueOf(nextChar));
        }

        // Initialize data
        var size = queue.size();
        char[] t = new char[size];
        int[] next = new int[size];

        int pos = 0;
        for (var c : queue) {
            t[pos++] = c;
        }

        char[] copiedT = Arrays.copyOf(t, size);
        Arrays.sort(copiedT);
        char[] sortedT = copiedT;

        // Calculate next[]
        calculateNext(next, t, sortedT);

        // Given next[] calculate original input
        var curr = first;
        for (var i = 0; i < size; i++) {
            BinaryStdOut.write(sortedT[curr]);
            curr = next[curr];
        }

        BinaryStdOut.close();
    }

    private static void calculateNext(int[] next, char[] t, char[] sortedT) {
        var charCount = new ST<Character, Integer>();
        for (var i = 0; i < sortedT.length; i++) {
            var c = sortedT[i];
            var key = Character.valueOf(c);
            var entry = charCount.get(key);

            int skip = entry == null ? 0 : entry;
            int skipCount = 0;

            int scan = 0;
            while (t[scan] != c || skipCount < skip) {
                if (t[scan] == c) {
                    skipCount++;
                }
                
                scan++;
            }

            next[i] = scan;

            charCount.put(key, skip + 1);
        }
    }

    private static void calculateNextNext(int[] next, char[] t, char[] sortedT) {
        int r = 256;

        int[] charCount = new int[r];
        int[] charCumulative = new int[r];
        int[] charsSeen = new int[r];

        for (var i = 0; i < r; i++) {
            charCount[i] = 0;
            charCumulative[i] = 0;
            charsSeen[i] = 0;
        }

        for (var i = 0; i < t.length; i++) {
            var charIndex = (int)t[i];
            charCount[charIndex] = charCount[charIndex] + 1;
        }

        for (var i = 1; i < charCumulative.length; i++) {
            charCumulative[i] = charCumulative[i - 1] + charCount[i];
        }
        
    }

    // java BurrowsWheeler - < ./testfiles/abra.txt | java
    // edu.princeton.cs.algs4.HexDump 16

    // java BurrowsWheeler - < ./testfiles/abra.txt | java BurrowsWheeler +

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length != 1 || !(args[0].equals("+") || args[0].equals("-")))
            throw new IllegalArgumentException(
                    "One argument required: - for transforming or + for inverse transforming");

        if (args[0].equals("-"))
            transform();
        else
            inverseTransform();
    }
}