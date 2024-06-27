import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;

import java.util.ArrayList;
import java.util.Date;

public class BoggleSolver {
    private final TST<Integer> wordsInDictionary;
    private TST<Integer> lastWordTST;
    private String lastBoardIdentifier;

    // Initializes the data structure using the given array of strings as the
    // dictionary.
    // (You can assume each word in the dictionary contains only the uppercase
    // letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        wordsInDictionary = new TST<>();

        for (String word : dictionary) {
            if (word.length() >= 3)
                wordsInDictionary.put(word, wordValue(word));
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        var boardIdentifier = getBoardIdentifier(board);
        if (boardIdentifier == lastBoardIdentifier) {
            return lastWordTST.keys();
        }

        boolean allTheSame = allTheSame(board);

        if (allTheSame) {
            var num = board.rows() * board.cols();
            var firstChar = board.getLetter(0, 0);
            var charText = firstChar == 'Q' ? "QU" : (firstChar + "");

            TST<Integer> allTheSameTST = new TST<>();
            for (var i = 1; i <= num; i++) {

                var word = charText.repeat(i);
                var score = wordsInDictionary.get(word);
                if (score != null)
                    allTheSameTST.put(word, score);
            }

            lastBoardIdentifier = boardIdentifier;
            lastWordTST = allTheSameTST;
            return allTheSameTST.keys();
        }

        Queue<PathPart> possiblePaths = new Queue<>();
        Queue<PathPart> paths = new Queue<>();

        int boardSize = board.cols() * board.rows();
        for (var i = 0; i < boardSize; i++) {
            possiblePaths.enqueue(new PathPart(i, getLetter(i, board)));
        }

        while (!possiblePaths.isEmpty()) {
            PathPart currPathPart = possiblePaths.dequeue();

            if (currPathPart.length >= 2) { // A Qu counts for 2, so the minimum number of parts is 2.
                paths.enqueue(currPathPart);
            }

            for (var nextPathPart : getNextPathParts(currPathPart, board)) {
                possiblePaths.enqueue(nextPathPart);
            }
        }

        TST<Integer> wordTST = new TST<>();

        while (!paths.isEmpty()) {
            var nextPath = paths.dequeue();
            var word = nextPath.wordSoFar;
            var score = wordsInDictionary.get(word);
            if (score != null)
                wordTST.put(word, score);
        }

        lastBoardIdentifier = boardIdentifier;
        lastWordTST = wordTST;
        return wordTST.keys();
    }

    private boolean allTheSame(BoggleBoard board) {
        char firstChar = board.getLetter(0, 0);

        for (var j = 0; j < board.cols(); j++)
            for (var i = 0; i < board.rows(); i++) {
                if (board.getLetter(i, j) != firstChar)
                    return false;
            }

        return true;
    }

    private static String getBoardIdentifier(BoggleBoard board) {
        StringBuilder builder = new StringBuilder();
        for (var j = 0; j < board.cols(); j++)
            for (var i = 0; i < board.rows(); i++) {
                char c = board.getLetter(i, j);
                var charText = c == 'Q' ? "QU" : (c + "");
                builder.append(charText);
            }

        return builder.toString();
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

    private Iterable<PathPart> getNextPathParts(PathPart pathPart, BoggleBoard board) {
        ArrayList<PathPart> nextPathParts = new ArrayList<>(8);
        if (pathPart.length >= 16 || !wordsInDictionary.keysWithPrefix(pathPart.wordSoFar).iterator().hasNext()) {
            return new ArrayList<>();
        }

        int boardRows = board.rows();
        int boardCols = board.cols();
        int row = numToRow(pathPart.num, boardCols);
        int col = numToCol(pathPart.num, boardCols);

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
                wordSoFar = String.valueOf(c);
        }

        public PathPart(PathPart prev, int num, char c) {
            this.prev = prev;
            this.num = num;
            length = prev.length + 1;

            if (c == 'Q')
                wordSoFar = prev.wordSoFar + "QU";
            else
                wordSoFar = prev.wordSoFar + c;
        }

        public boolean containsNum(int n) {
            return num == n || (prev != null && prev.containsNum(n));
        }
    }

    // Example:
    // java BoggleSolver ./testfiles/dictionary-algs4.txt ./testfiles/board4x4.txt
    // java BoggleSolver ./testfiles/dictionary-algs4.txt ./testfiles/board-q.txt
    public static void main(String[] args) {
        Date start = new Date();

        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        Date finish = new Date();

        StdOut.println("Score = " + score);

        printDuration("full run", start, finish);
    }

    private static void printDuration(String step, Date start, Date finish) {
        printDuration(step, start, finish, 0);
    }

    private static void printDuration(String step, Date start, Date finish, int indent) {
        long diff = finish.getTime() - start.getTime();
        double seconds = ((double) diff / 1000.0);
        String prefix = indent == 0
                ? ""
                : " " + "> ".repeat(indent);
        System.out.println(prefix + "Step [" + step + "]: duration was " + seconds + " s.");
    }
}