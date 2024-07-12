public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to
    // standard output
    public static void encode() {

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

        if (args[0].equals("+"))
            encode();
        else
            decode();
    }
}