package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * this class represents the game board
 */
public class Island {
    private Cell[][][] cells;
    private ArrayList<Worker> workers;

    public Island()
    {
        cells= new Cell[5][5][4];
        workers= new ArrayList<Worker>();
        for(int x=0; x<5; x++)
        {
            for(int y=0; y<5; y++)
            {
                for(int z=0; z<4; z++)
                {
                    cells[x][y][z]= new Cell(x,y,z);   //may be +1
                }
            }
        }
    }

    /**
     * provide the reference of a cell with a specific index. this method is useful in order to make the controller works only with index
     * @param i the index of the cell we are interested in
     * @return the cell with the wanted index
     */
    public Cell getCellByIndex(Index i){
        return cells[i.getX()][i.getY()][i.getZ()];
    }
    /**
     * Set the worker in a cell of the game board
     * @param w the current worker to be setted
     * @param c the cell where the worker is to be put
     */
    private void initWorker(Worker w, Cell c)
    {
        c.setWorker(w);
        w.setPosition(c.getIndex());
        workers.add(w);
    }

}
