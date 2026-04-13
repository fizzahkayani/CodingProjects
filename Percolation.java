import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import java.util.Arrays;

public class Percolation {
    private int numOpen;
    private final int N;
    private final boolean[] openArr;
    private final WeightedQuickUnionUF grid;
    private final WeightedQuickUnionUF gridVb;
    private final int vt;
    private final int vb;
    private WeightedQuickUnionUF uf;

    public Percolation(int N) {
        this.N = N;
        //Corner Case
        if (N < 0) {
            throw new java.lang.IllegalArgumentException("Cannot be negative length!");
        }

        openArr = new boolean[N * N];

        //Initialize Array
        Arrays.fill(openArr, false);

        grid = new WeightedQuickUnionUF(N * N + 1);
        gridVb = new WeightedQuickUnionUF(N * N + 2);
        vt = N * N;
        vb = N * N + 1;
    }

    public void open(int row, int col) {
        /*
        //Corner Case
        if (row < 0 && row > N - 1 && col < 0 && col > N - 1) {
            throw new java.lang.IndexOutOfBoundsException("Index out of Bounds!");
        }

        if (row - 1 < 0 && row + 1 > N - 1 && col - 1 < 0 && col + 1 > N - 1) {
            throw new java.lang.IndexOutOfBoundsException("Index out of Bounds!");
        }

         */

        if (!isOpen(row, col)) {

            //Connect all top to Virtual Top first
            if (row == 0) {
                gridVb.union(vt, xyTo1D(row, col));
                grid.union(vt, xyTo1D(row, col));
            }

            if (row == N - 1) {
                gridVb.union(xyTo1D(row, col), vb);
            }

            //Union right
            if (col + 1 <= N - 1 && isOpen(row, col + 1)) {
                gridVb.union(xyTo1D(row, col), xyTo1D(row, col + 1));
                grid.union(xyTo1D(row, col), xyTo1D(row, col + 1));
            }

            //Union left
            if (col - 1 >= 0 && isOpen(row, col - 1)) {
                gridVb.union(xyTo1D(row, col), xyTo1D(row, col - 1));
                grid.union(xyTo1D(row, col), xyTo1D(row, col - 1));
            }

            //Union up
            if (row - 1 >= 0 && isOpen(row - 1, col)) {
                gridVb.union(xyTo1D(row, col), xyTo1D(row - 1, col));
                grid.union(xyTo1D(row, col), xyTo1D(row - 1, col));
            }

            //Union down
            if (row + 1 <= N - 1 && isOpen(row + 1, col)) {
                gridVb.union(xyTo1D(row, col), xyTo1D(row + 1, col));
                grid.union(xyTo1D(row, col), xyTo1D(row + 1, col));
            }

            openArr[xyTo1D(row, col)] = true;
            numOpen += 1;
        }
    }

    private void connectToAdjacentOpenSites(int row, int col) {
        int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}}; // Right, Left, Down, Up
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (newRow >= 1 && newRow <= N && newCol >= 1 && newCol <= N && isOpen(newRow, newCol)) {
                uf.union(xyTo1D(row, col), xyTo1D(newRow, newCol));
            }
        }
        // If the site is in the first row, connect it to the virtual top site
        if (row == 1) {
            uf.union(xyTo1D(row, col), 0);
        }
        // If the site is in the last row, connect it to the virtual bottom site
        if (row == N) {
            uf.union(xyTo1D(row, col), N * N + 1);
        }
    }

    public boolean isOpen(int row, int col) {
        if (row < 0 && row > N - 1 && col < 0 && col > N - 1) {
            throw new java.lang.IndexOutOfBoundsException("Index out of Bounds!");
        }
        return (openArr[xyTo1D(row, col)]);
    }

    public boolean isFull(int row, int col) {
        //Corner Case
        if (row < 0 && row > N - 1 && col < 0 && col > N - 1) {
            throw new java.lang.IndexOutOfBoundsException("Index out of Bounds!");
        }

        if (isOpen(row, col)) {
            // Check if it is connected to the virtual top
            if (grid.connected(xyTo1D(row, col), vt)) {
                return true;
            }
        }
        return false;
    }

    public int numberOfOpenSites() {
        return numOpen;
    }

    public boolean percolates() {
        return (gridVb.connected(vt, vb));
    }
    public int xyTo1D(int row, int col) {
        return row * N + col;
    }

}
