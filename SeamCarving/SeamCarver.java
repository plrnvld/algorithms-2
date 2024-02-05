import java.awt.Color;

import edu.princeton.cs.algs4.DijkstraSP;
import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private Picture picture;
    private int startEdge;
    private int targetEdge;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null || picture.width() == 0 || picture.height() == 0)
            throw new IllegalArgumentException();

        this.picture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IllegalArgumentException();

        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1)
            return 1000.0; // Border energy

        return Math.sqrt(energy(x - 1, y, x + 1, y) + energy(x, y - 1, x, y + 1));
    }

    private double energy(int xLow, int yLow, int xHigh, int yHigh) {
        Color colorPrev = picture.get(xLow, yLow);
        Color colorNext = picture.get(xHigh, yHigh);

        double energy = energyColorSquared(colorPrev.getRed(), colorNext.getRed())
                + energyColorSquared(colorPrev.getGreen(), colorNext.getGreen())
                + energyColorSquared(colorPrev.getBlue(), colorNext.getBlue());

        return energy;
    }

    private double energyColorSquared(int prevVal, int nextVal) {
        int delta = prevVal - nextVal;
        return delta * delta;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return new int[0];
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] verticalSeam = new int[picture.height()];

        DigraphWithEndPoints digraphWithEndPoints = buildVerticalDigraph();
        DijkstraSP dijkstra = new DijkstraSP(digraphWithEndPoints.digraph, digraphWithEndPoints.startEdge);
        Iterable<DirectedEdge> pathForSeam = dijkstra.pathTo(digraphWithEndPoints.targetEdge);

        int count = 0;

        for (DirectedEdge edge : pathForSeam) {
            if (count < verticalSeam.length) { // Last step to target is not part of the seam
                verticalSeam[count] = edgeIdToCol(edge.to());
                count += 1;
            }
        }

        return verticalSeam;
    }

    private DigraphWithEndPoints buildVerticalDigraph() {
        int numPictureEdges = picture.width() * picture.height();
        startEdge = numPictureEdges;
        targetEdge = numPictureEdges + 1;

        int numVerticesVertical = picture.width() * (picture.height() + 2);
        EdgeWeightedDigraph digraph = new EdgeWeightedDigraph(numVerticesVertical);

        // Connect startEdge to first row of picture
        int firstRow = 0;
        for (int col = 0; col < picture.width(); col++) {
            int edgeId = verticalEdgeId(col, firstRow);
            digraph.addEdge(new DirectedEdge(startEdge, edgeId, energy(col, firstRow)));
        }

        for (int row = 0; row < picture.height() - 1; row++) // Connect every row to the next one, last row can be
                                                             // skipped here
            for (int col = 0; col < picture.width(); col++) {
                int fromId = verticalEdgeId(col, row);
                int nextRow = row + 1;

                // Down and left
                if (col - 1 >= 0) {
                    int toId1 = verticalEdgeId(col - 1, nextRow);
                    digraph.addEdge(new DirectedEdge(fromId, toId1, energy(col - 1, nextRow)));
                }

                // Straight down
                int toId2 = verticalEdgeId(col, nextRow);
                digraph.addEdge(new DirectedEdge(fromId, toId2, energy(col, nextRow)));

                // Down and right
                if (col + 1 < picture.width()) {
                    int toId3 = verticalEdgeId(col + 1, row + 1);
                    digraph.addEdge(new DirectedEdge(fromId, toId3, energy(col + 1, nextRow)));
                }
            }

        // Connect last row of picture to targetEdge
        int lastRow = picture.height() - 1;
        for (int col = 0; col < picture.width(); col++) {
            int edgeId = verticalEdgeId(col, lastRow);
            digraph.addEdge(new DirectedEdge(edgeId, targetEdge, 0));
        }

        return new DigraphWithEndPoints(digraph, startEdge, targetEdge);
    }

    private int verticalEdgeId(int col, int row) {
        return col + row * picture.width();
    }

    private int edgeIdToCol(int edgeId) {
        int wholeRows = edgeId / picture.width();
        return edgeId - wholeRows * picture.width();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || height() <= 1 || seam.length != width())
            throw new IllegalArgumentException();

        Picture newPicture = new Picture(picture.width(), picture.height() - 1);
        for (int col = 0; col < newPicture.width(); col++) {
            int removeIndex = seam[col];

            for (int row = 0; row < newPicture.height(); row++) {
                int skip = row >= removeIndex ? 1 : 0;

                int originalRGB = picture.getRGB(col, row + skip);
                newPicture.setRGB(col, row, originalRGB);
            }
        }

        picture = newPicture;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || width() <= 1 || seam.length != height())
            throw new IllegalArgumentException();

        Picture newPicture = new Picture(picture.width() - 1, picture.height());
        for (int row = 0; row < newPicture.height(); row++) {
            int removeIndex = seam[row];

            for (int col = 0; col < newPicture.width(); col++) {
                int skip = col >= removeIndex ? 1 : 0;

                int originalRGB = picture.getRGB(col + skip, row);
                newPicture.setRGB(col, row, originalRGB);
            }
        }

        picture = newPicture;
    }

    private class DigraphWithEndPoints {
        final EdgeWeightedDigraph digraph;
        final int startEdge;
        final int targetEdge;

        public DigraphWithEndPoints(EdgeWeightedDigraph digraph, int startEdge, int targetEdge) {
            this.digraph = digraph;
            this.startEdge = startEdge;
            this.targetEdge = targetEdge;
        }
    }

    // unit testing (optional)
    public static void main(String[] args) {

        Picture picture = new Picture("./testfiles/6x5.png");
        SeamCarver seamCarver = new SeamCarver(picture);

        int[] seam = seamCarver.findVerticalSeam();

        for (int i = 0; i < seam.length; i++)
            System.out.print(seam[i] + " ");
    }
}