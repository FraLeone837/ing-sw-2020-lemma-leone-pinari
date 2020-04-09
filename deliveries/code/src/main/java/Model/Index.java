package Model;

/**
 * this class contains the coordinates (x[0...4], y[0...4], z[0...3]) of a cell in the game board.
 * index is unmodifiable: the position of a cell cannot change and the workers have a copy of the index of the cell where they are
 */
public class Index {
    private int x;
    private int y;
    private int z;

    public Index(int x,int y,int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
