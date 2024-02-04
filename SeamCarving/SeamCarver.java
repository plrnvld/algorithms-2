import java.awt.Color;
import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private Picture picture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
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

        return energyColorSquared(colorPrev.getRed(), colorNext.getRed())
                + energyColorSquared(colorPrev.getGreen(), colorNext.getGreen())
                + energyColorSquared(colorPrev.getBlue(), colorNext.getBlue());
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
        return new int[0];
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

    // unit testing (optional)
    public static void main(String[] args) {
        Picture testPicture = new Picture(3, 4);

        testPicture.set(0, 0, new Color(255, 101, 51));
        testPicture.set(1, 0, new Color(255, 101, 153));
        testPicture.set(2, 0, new Color(255, 101, 255));

        testPicture.set(0, 1, new Color(255, 153, 51));
        testPicture.set(1, 1, new Color(255, 153, 153));
        testPicture.set(2, 1, new Color(255, 153, 255));

        testPicture.set(0, 2, new Color(255, 203, 51));
        testPicture.set(1, 2, new Color(255, 204, 153));
        testPicture.set(2, 2, new Color(255, 205, 255));

        testPicture.set(0, 3, new Color(255, 255, 51));
        testPicture.set(1, 3, new Color(255, 255, 153));
        testPicture.set(2, 3, new Color(255, 255, 255));

        SeamCarver sc = new SeamCarver(testPicture);

        for (int row = 0; row < testPicture.height(); row++)
            for (int col = 0; col < testPicture.width(); col++) {
                System.out.println("(" + col + "," + row + ") = " + sc.energy(col, row));
            }
    }
}