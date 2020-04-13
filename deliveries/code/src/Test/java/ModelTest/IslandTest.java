package ModelTest;

import Model.Cell;
import Model.Index;
import Model.Island;
import Model.Worker;
import org.junit.Assert;
import java.util.Random;


import static org.junit.Assert.*;

public class IslandTest {
    private int x;
    private int y;
    private int z;
    private Island island;


    @org.junit.Before
    public void setUp() throws Exception {
        Random rand = new Random();
        this.x = rand.nextInt(5);
        this.y = rand.nextInt(5);
        this.z = rand.nextInt(4);
        this.island = new Island();
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void testGetCell_correctIndex_correctCell(){
        //Cell getCell(Index i)
        Index ix = new Index(x,y,z);
        Cell c1 = new Cell(x,y,z);
        Cell c2=island.getCell(ix);
        assertEquals(c1, c2);
    }

    @org.junit.Test
    public void testGetCell_wrongIndex_OutOfBoundException(){
        //Cell getCell(Index i)
        Index ix = new Index(5,5,5);
        Cell c1 = new Cell(x,y,z);
        Cell c2=island.getCell(ix);

    }

    @org.junit.Test
    public void testInitWorker(){
        //void initWorker(Worker w, Cell c)
        Cell c= new Cell(x,y,z);
        Worker w = new Worker();
        island.initWorker(w,c);
    }

}