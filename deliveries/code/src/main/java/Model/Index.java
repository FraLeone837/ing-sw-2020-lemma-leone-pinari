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

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Index)){
            System.out.println("Object other is not instance of Index ");
            return false;
        }
        Index in = (Index) other;
        if (this.x == in.x){
            if (this.y == in.y) {
                if (this.z == in.z) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "X position is: " + this.x + ". Y position is: " + this.y + ". Z position is: " + this.z;
    }
}
