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
        if (island == null)
            this.island = new Island();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        island = null;
    }

    @org.junit.Test
    public void testGetCell_correctIndex_correctCell(){
        //Cell getCell(Index i)
        Index ix = new Index(x,y,z);
        Cell c2=island.getCell(ix);
        assertEquals(c2.getIndex().getZ(), z);
        assertEquals(c2.getIndex().getY(), y);
        assertEquals(c2.getIndex().getX(), x);
    }

    @org.junit.Test
    public void testGetCell_wrongIndex_OutOfBoundException(){
        //Cell getCell(Index i)
        boolean x = false;
        try{
            Index ix = new Index(5,5,4);
            Cell c1 = new Cell(this.x,this.y,this.z);
            Cell c2=island.getCell(ix);
        } catch (ArrayIndexOutOfBoundsException IO){
            x = true;
        }
        assertTrue(x);

    }



}