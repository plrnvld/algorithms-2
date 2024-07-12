import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to
    // standard output
    public static void encode() {
        int[] chars = new int[256];
        for (var i = 0; i < chars.length; i++)
            chars[i] = i;

        while (!BinaryStdIn.isEmpty()) {
            var charVal = BinaryStdIn.readChar(8);

            var pos = 0;
            while (chars[pos] != charVal)
                pos++;

            // ################# Move to the front needed 

            BinaryStdOut.write(pos, 8);
        }

        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to
    // standard output
    public static void decode() {

    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length != 1 || !(args[0].equals("+") || args[0].equals("-")))
            throw new IllegalArgumentException("One argument required: + for encoding or - for decoding");

        if (args[0].equals("-"))
            encode();
        else
            decode();
    }

    // java MoveToFront - < ./testfiles/abra.txt | java edu.princeton.cs.algs4.HexDump 16
}