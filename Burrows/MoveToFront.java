import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to
    // standard output
    public static void encode() {
        CharLinkedList linked = new CharLinkedList();
        for (var i = 0; i < 256; i++)
            linked.addLast((char) i);

        while (!BinaryStdIn.isEmpty()) {
            var charVal = BinaryStdIn.readChar(8);

            int pos = 0;
            var curr = linked.first;
            while (curr.value != charVal) {
                curr = curr.next;
                pos++;
            }

            BinaryStdOut.write(pos, 8);

            linked.moveToStart(curr);
        }

        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to
    // standard output
    public static void decode() {
        CharLinkedList linked = new CharLinkedList();
        for (var i = 0; i < 256; i++)
            linked.addLast((char) i);

        while (!BinaryStdIn.isEmpty()) {
            var pos = BinaryStdIn.readChar(8);

            var curr = linked.first;
            while (pos > 0) {
                curr = curr.next;
                pos--;
            }

            BinaryStdOut.write(curr.value);

            linked.moveToStart(curr);
        }

        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length != 1 || !(args[0].equals("+") || args[0].equals("-")))
            throw new IllegalArgumentException("One argument required: - for encoding or + for decoding");

        if (args[0].equals("-"))
            encode();
        else
            decode();
    }

    // java MoveToFront - < ./testfiles/abra.txt | java
    // edu.princeton.cs.algs4.HexDump 16

    // java MoveToFront - < ./testfiles/abra.txt | java MoveToFront +
}

class CharLinkedList {
    public CharNode first;
    public CharNode last;

    public void addFirst(char c) {
        var node = new CharNode(c);
        node.next = first;
        if (first != null)
            first.prev = node;

        first = node;
        if (last == null)
            last = node;
    }

    public void moveToStart(CharNode node) {
        if (node == first)
            return;

        remove(node);
        addFirst(node.value);
    }

    public void remove(CharNode node) {
        if (node.prev != null)
            node.prev.next = node.next;

        if (node.next != null)
            node.next.prev = node.prev;
    }

    public void addLast(char c) {
        var node = new CharNode(c);
        node.prev = last;
        if (last != null)
            last.next = node;

        last = node;
        if (first == null)
            first = node;
    }
}

class CharNode {
    public char value;

    public CharNode(char value) {
        this.value = value;
    }

    public CharNode prev;
    public CharNode next;
}