import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Stack;

public class BoggleSolver {
    private final TST<Integer> wordsInDictionary;

    // Initializes the data structure using the given array of strings as the
    // dictionary.
    // (You can assume each word in the dictionary contains only the uppercase
    // letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        wordsInDictionary = new TST<>();

        Instant dictStart = Instant.now();

        for (String word : dictionary) {
            wordsInDictionary.put(word, wordValue(word));
        }

        Instant dictEnd = Instant.now();

        printDuration("loading dictionary", dictStart, dictEnd);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        Queue<PathPart> possiblePaths = new Queue<>();
        Queue<PathPart> paths = new Queue<>();

        Instant startPossiblePaths = Instant.now();

        int boardSize = board.cols() * board.rows();
        for (var i = 0; i < boardSize; i++) {
            possiblePaths.enqueue(new PathPart(i, getLetter(i, board)));
        }

        while (!possiblePaths.isEmpty()) {
            PathPart currPathPart = possiblePaths.dequeue();
            int lastNum = currPathPart.num;

            if (currPathPart.length >= 3) {
                paths.enqueue(currPathPart);
            }

            for (var nextPathPart : getNextPathParts(lastNum, currPathPart, board)) {
                possiblePaths.enqueue(nextPathPart);
            }
        }

        Instant endPossiblePaths = Instant.now();

        printDuration("find possible paths", startPossiblePaths, endPossiblePaths, 1);

        Instant convertPathsToWordsStart = Instant.now();

        TST<Boolean> wordTST = new TST<>(); 

        while (!paths.isEmpty()) {
            var nextPath = paths.dequeue();
            if (wordsInDictionary.contains(nextPath.wordSoFar))
                wordTST.put(nextPath.wordSoFar, true);
        }

        Instant convertPathsToWordsEnd = Instant.now();

        printDuration("word conversions", convertPathsToWordsStart, convertPathsToWordsEnd, 1);

        return wordTST.keys();
    }

    // Returns the score of the given word if it is in the dictionary, zero
    // otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        Integer scoreValue = wordsInDictionary.get(word);

        if (scoreValue == null)
            return 0;

        return scoreValue;
    }

    private Iterable<PathPart> getNextPathParts(int num, PathPart pathPart, BoggleBoard board) {
        ArrayList<PathPart> nextPathParts = new ArrayList<>(8);
        if (pathPart.length >= 16) {
            return new ArrayList<>();
        }

        int boardRows = board.rows();
        int boardCols = board.cols();
        int row = numToRow(num, boardCols);
        int col = numToCol(num, boardCols);

        boolean rowLargerThanZero = row > 0;
        boolean colLargerThanZero = col > 0;
        boolean rowSmallerThanMax = row < boardRows - 1;
        boolean colSmallerThanMax = col < boardCols - 1;

        if (rowLargerThanZero) {
            if (colLargerThanZero)
                addWhenOpen(col - 1, row - 1, nextPathParts, pathPart, board);

            addWhenOpen(col, row - 1, nextPathParts, pathPart, board);

            if (colSmallerThanMax)
                addWhenOpen(col + 1, row - 1, nextPathParts, pathPart, board);
        }

        if (rowSmallerThanMax) {
            if (colLargerThanZero)
                addWhenOpen(col - 1, row + 1, nextPathParts, pathPart, board);

            addWhenOpen(col, row + 1, nextPathParts, pathPart, board);

            if (colSmallerThanMax)
                addWhenOpen(col + 1, row + 1, nextPathParts, pathPart, board);
        }

        if (colLargerThanZero)
            addWhenOpen(col - 1, row, nextPathParts, pathPart, board);

        if (colSmallerThanMax)
            addWhenOpen(col + 1, row, nextPathParts, pathPart, board);

        return nextPathParts;
    }

    private void addWhenOpen(int col, int row, ArrayList<PathPart> nexPathParts, PathPart pathPart, BoggleBoard board) {
        int boardCols = board.cols();
        var nextNum = colRowToNum(col, row, boardCols);

        if (!pathPart.containsNum(nextNum))
            nexPathParts.add(new PathPart(pathPart, nextNum, getLetter(nextNum, board)));
    }

    private static int numToRow(int num, int width) {
        return num / width;
    }

    private static int numToCol(int num, int width) {
        return num % width;
    }

    private static int colRowToNum(int col, int row, int width) {
        return row * width + col;
    }

    private static char getLetter(int num, BoggleBoard board) {
        int width = board.cols();
        int row = numToRow(num, width);
        int col = numToCol(num, width);

        return board.getLetter(row, col);
    }

    // Give the score for a word, but don't check if it's in the dictionary
    private int wordValue(String word) {
        int numChars = word.length();
        switch (numChars) {
            case 0:
            case 1:
            case 2:
                return 0;
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }

    private class PathPart {
        int num;
        int length;
        PathPart prev;
        String wordSoFar;

        public PathPart(int startNum, char c) {
            num = startNum;
            length = 1;

            if (c == 'Q')
                wordSoFar = "QU";
            else
                wordSoFar = "" + c;
        }

        public PathPart(PathPart prev, int num, char c) {
            this.prev = prev;
            this.num = num;
            length = prev.length + 1;

            if (c == 'Q')
                wordSoFar = prev.wordSoFar +"QU";
            else
                wordSoFar = prev.wordSoFar + c;
        }

        public boolean containsNum(int n) {
            return num == n || (prev != null && prev.containsNum(n));
        }

        public Iterable<Integer> nums() {
            Stack<Integer> stack = new Stack<>();

            PathPart curr = this;
            stack.push(curr.num);

            while (curr.prev != null) {
                curr = curr.prev;
                stack.push(curr.num);
            }

            return stack;
        }
    }

    private class NumStringTST {
        private TST<String> tst;

        public NumStringTST() {
            tst = new TST<>();
        }

        public String get(int[] key) {
            return tst.get(numsToKey(key));
        }

        public void put(int[] key, String val) {
            tst.put(numsToKey(key), val);
        }

        private String numsToKey(int[] nums) {
            StringBuilder builder = new StringBuilder();
            for (var num : nums) {
                char keyPart = (char) ((int) 'A' + num);
                builder.append(keyPart);
            }

            return builder.toString();
        }
    }

    // Example:
    // java BoggleSolver ./testfiles/dictionary-algs4.txt ./testfiles/board4x4.txt
    public static void main(String[] args) {
        Instant start = Instant.now();

        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
        Instant finish = Instant.now();

        printDuration("full run", start, finish);
    }

    private static void printDuration(String step, Instant start, Instant finish) {
        printDuration(step, start, finish, 0);
    }

    private static void printDuration(String step, Instant start, Instant finish, int indent) {
        long diff = finish.toEpochMilli() - start.toEpochMilli();
        double seconds = ((double) diff / 1000.0);
        String prefix = indent == 0
                ? ""
                : " " + "> ".repeat(indent);
        System.out.println(prefix + "Step [" + step + "]: duration was " + seconds + " s.");
    }
}