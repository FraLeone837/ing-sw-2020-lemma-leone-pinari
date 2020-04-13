package ModelTest;

import Model.Cell;
import Model.Index;
import Model.Worker;
import org.junit.Assert;
import java.util.Random;


import static org.junit.Assert.*;

public class CellTest {

    private int x;
    private int y;
    private int z;
    private Cell cell;

    @org.junit.Before
    public void setUp() throws Exception {
        Random rand = new Random();
        this.x = rand.nextInt(5);
        this.y = rand.nextInt(5);
        this.z = rand.nextInt(4);
        this.cell = new Cell(x, y, z);
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void testIsEmpty_containsNothing_expectedEmpty() {
        cell.setWorker(null);

        if(cell.isEmpty()) {
            System.out.println("Cell is empty.");
        }
        else System.out.println("Cell is not empty.");
        assertTrue(cell.isEmpty());
    }

    @org.junit.Test
    public void testIsEmpty_containsWorker_expectedNotEmpty() {
        cell.setWorker(new Worker());

        if(cell.isEmpty()) {
            System.out.println("Cell is empty.");
        }
        else System.out.println("Cell is not empty.");
        assertFalse(cell.isEmpty());

    }

    @org.junit.Test
    public void testIsEmpty_containsBuilding_expectedNotEmpty(){
        cell.setBuilding();

        assertFalse(cell.isEmpty());
    }

    @org.junit.Test
    public void testIsEmpty_containsDome_expectedNotEmpty(){
        cell.setDome();
        assertFalse(cell.isEmpty());
    }

    @org.junit.Test
    public void testGetIndex(){
        Index ix = cell.getIndex();
        assertEquals(x, ix.getX());
        assertEquals(y, ix.getY());
        assertEquals(z, ix.getZ());
    }

//    @org.junit.Test
//    public void testSetBuilding() {
//    }
//
//    @org.junit.Test
//    public void testSetDome() {
//    }
//
//    @org.junit.Test
//    public void testSetWorker() {
//    }
//
//    @org.junit.Test
//    public void testGetWorker() {
//    }
//
//    @org.junit.Test
//    public void testAddForbidden() {
//    }
}