import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.TST;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.StreamSupport;

public class BoggleSolver {
    TST<Integer> wordsInDictionary;

    // Initializes the data structure using the given array of strings as the
    // dictionary.
    // (You can assume each word in the dictionary contains only the uppercase
    // letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        wordsInDictionary = new TST<>();

        for (String word : dictionary) {
            wordsInDictionary.put(word, wordValue(word));
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        return () -> StreamSupport.stream(getAllBoggleSequences(board).spliterator(), false)
                .filter(boggleSeq -> wordsInDictionary.contains(boggleSeq)).iterator();
    }

    private Iterable<String> getAllBoggleSequences(BoggleBoard board) {
        Queue<Integer> queue = new Queue<>();

        int boardSize = board.cols() * board.rows();
        for (var i = 0; i < boardSize; i++)
            queue.enqueue(i);

        LinkedList<String> sequences = new LinkedList<>();

        // ############## Calculate possibilities

        return sequences;
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

    private Iterable<Integer> getNextNums(int num, Iterable<Integer> path, BoggleBoard board) {
        ArrayList<Integer> sequences = new ArrayList<>(8);

        int boardRows = board.rows();
        int boardCols = board.cols();
        int row = numToRow(num, boardCols);
        int col = numToCol(num, boardCols);

        boolean rowLargerThanZero = row > 0;
        boolean colLargerThanZero = col > 0;
        boolean rowSmallerThanMax = row < boardRows;
        boolean colSmallerThanMax = col < boardCols;

        if (rowLargerThanZero) {
            addWhenOpen(col, row - 1, sequences, path, boardCols);

            if (colLargerThanZero)
                addWhenOpen(col - 1, row - 1, sequences, path, boardCols);

            if (colSmallerThanMax)
                addWhenOpen(col + 1, row - 1, sequences, path, boardCols);
        }

        if (rowSmallerThanMax) {
            addWhenOpen(col, row + 1, sequences, path, boardCols);

            if (colLargerThanZero)
                addWhenOpen(col - 1, row + 1, sequences, path, boardCols);

            if (colSmallerThanMax)
                addWhenOpen(col + 1, row + 1, sequences, path, boardCols);
        }

        if (colLargerThanZero)
            addWhenOpen(col - 1, row, sequences, path, boardCols);

        if (colSmallerThanMax)
            addWhenOpen(col + 1, row, sequences, path, boardCols);

        return sequences;
    }

    private void addWhenOpen(int col, int row, ArrayList<Integer> numList, Iterable<Integer> path, int boardCols) {
        var nextNum = rowColToNum(col - 1, row - 1, boardCols);

        if (!StreamSupport.stream(path.spliterator(), false).anyMatch(pathNum -> pathNum.intValue() == nextNum))
            numList.add(nextNum);
    }

    private static int numToRow(int num, int width) {
        return num / width;
    }

    private static int numToCol(int num, int width) {
        return num % width;
    }

    private static int rowColToNum(int col, int row, int width) {
        return row * width + col;
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

    public static void main(String[] args) {
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
    }
}