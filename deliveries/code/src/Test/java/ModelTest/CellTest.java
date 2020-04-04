package ModelTest;

import Model.Cell;
import Model.Worker;
import org.junit.Assert;

import static org.junit.Assert.*;

public class CellTest {

    Cell cell;

    @org.junit.Before
    public void setUp() throws Exception {
        this.cell = new Cell(1,2,3);
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
        cell.setWorker(new Worker(2));

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