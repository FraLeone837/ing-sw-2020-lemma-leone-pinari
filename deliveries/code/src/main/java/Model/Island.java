package Model;

import java.util.ArrayList;
import java.util.List;

public class Island {
    private Cell[][][] cells;
    private ArrayList<Worker> workers;

    public Island()
    {
        cells= new Cell[5][5][4];
        workers= new ArrayList<Worker>();
    }

    /**
     * Initializes all 5*5*4 empty cells in setup phase
     */
    public void initCells(){
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

//    /**
//     * Set the player's workers on the game board
//     * @param p the current player whose workers are to be setted
//     */
//    public void initPlayer(Player p)
//    {
//
//    }

    /**
     * Set the worker in a cell of the game board
     * @param w the current worker to be setted
     * @param c the cell where the worker is to be put
     */
    private void initWorker(Worker w, Cell c)
    {
        c.setWorker(w);
        w.setPosition(c);
        workers.add(w);
    }

}
