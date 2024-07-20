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
        calculateNextOptimized(next, t);

        // Given next[] calculate original input
        var curr = first;
        for (var i = 0; i < size; i++) {
            BinaryStdOut.write(sortedT[curr]);
            curr = next[curr];
        }

        BinaryStdOut.close();
    }

    private static void calculateNextOptimized(int[] next, char[] t) {
        int r = 256;

        int[] charCount = new int[r];
        char[] sortedT = new char[t.length];
        ST<Character, Integer> sortedStart = new ST<>();
        ST<Character, Integer> tSeen = new ST<>();

        for (var i = 0; i < r; i++) {
            charCount[i] = 0;
        }

        for (var i = 0; i < t.length; i++) {
            var charIndex = (int) t[i];
            charCount[charIndex] = charCount[charIndex] + 1;
        }

        int sortedTPos = 0;
        for (var i = 0; i < charCount.length; i++) {
            var count = charCount[i];

            if (count > 0) {
                for (var j = 0; j < count; j++) {
                    sortedT[sortedTPos++] = (char) i;
                }
            }
        }

        char curr = sortedT[0];
        // System.out.println("> Offset for '" + curr + "'= 0");
        sortedStart.put(Character.valueOf(curr), 0);

        for (var i = 1; i < sortedT.length; i++) {
            curr = sortedT[i];
            if (curr != sortedT[i - 1]) {
                // System.out.println("> Offset for '" + curr + "'= " + i);
                sortedStart.put(Character.valueOf(curr), i);
            }
        }

        // Init characters that are encountered to 0
        for (var i = 0; i < t.length; i++) {
            var key = Character.valueOf(sortedT[i]);
            // System.out.println("Init sortedSeen: putting: " + key);
            tSeen.put(Character.valueOf(key), 0);
        }

        for (var i = 0; i < t.length; i++) {
            Character cFromT = Character.valueOf(t[i]);
            var indexToStore = i;

            var offsetToStore = (int)sortedStart.get(cFromT);
            var seen = (int)tSeen.get(cFromT);
            next[offsetToStore + seen] = indexToStore;

            tSeen.put(cFromT, seen + 1);
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