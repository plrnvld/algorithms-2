import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

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
        BinaryStdOut.close();
    }

    // java BurrowsWheeler - < ./testfiles/abra.txt | java
    // edu.princeton.cs.algs4.HexDump 16

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