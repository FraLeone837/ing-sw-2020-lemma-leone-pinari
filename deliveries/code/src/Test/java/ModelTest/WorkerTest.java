package ModelTest;

import Model.Cell;
import Model.Index;
import Model.Player;
import Model.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class WorkerTest {
    private Worker worker;

    @Before
    public void setUp() throws Exception {
        worker = new Worker();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSetIdWorker_correctId_expectedEquals(){
        Random rand = new Random();
        int id = rand.nextInt(20);
        worker.setIdWorker(id);
        assertEquals(id, worker.getIdWorker());
    }
    @Test
    public void testSetIdWorker_incorrectId_expectedNotEquals(){
        Random rand = new Random();
        int id = rand.nextInt(20);
        worker.setIdWorker(id);
        int notId;
        do{
            notId = rand.nextInt(20);
        } while(id==notId);
        assertNotEquals(notId, worker.getIdWorker());
    }
    @Test
    public void testSetOwner_correctOwner_expectedEquals(){
        Random rand = new Random();
        int id = rand.nextInt(2);
        Player player = new Player("generic_name", id);
        worker.setOwner(player);
        assertEquals(player, worker.getOwner());
    }

    @Test
    public void testSetOwner_incorrectOwner_expectedNotEquals(){
        Random rand = new Random();
        int id = rand.nextInt(2);
        Player player = new Player("generic_name", id);
        worker.setOwner(player);
        int notId = rand.nextInt(2);
        Player notPlayer = new Player("other_name", notId);
        assertNotEquals(notPlayer, worker.getOwner());
    }

    @Test
    public void testBuild(){
        Random rand = new Random();
        int x = rand.nextInt(5) + 1;
        int y = rand.nextInt(5) + 1;
        int z = rand.nextInt(4) + 1;
        Cell cell = new Cell(x, y, z);
        assertTrue(cell.isEmpty());
        worker.build(cell);
        assertFalse(cell.isEmpty());
    }
    @Test
    public void testBuildDome(){
        Random rand = new Random();
        int x = rand.nextInt(5) + 1;
        int y = rand.nextInt(5) + 1;
        int z = rand.nextInt(4) + 1;
        Cell cell = new Cell(x, y, z);
        assertTrue(cell.isEmpty());
        worker.buildDome(cell);
        assertFalse(cell.isEmpty());
    }
    @Test
    public void testMove(){
        Random rand = new Random();
        int x = rand.nextInt(5) + 1;
        int y = rand.nextInt(5) + 1;
        int z = rand.nextInt(4) + 1;
        Cell cell = new Cell(x, y, z);
        worker.move(cell);
        assertEquals(worker, cell.getWorker());
        assertEquals(cell.getIndex().getX(), worker.getPosition().getX());
        assertEquals(cell.getIndex().getY(), worker.getPosition().getY());
        assertEquals(cell.getIndex().getZ(), worker.getPosition().getZ());
    }

    @Test
    public void testDelete(){
        Random rand = new Random();
        int x = rand.nextInt(5) + 1;
        int y = rand.nextInt(5) + 1;
        int z = rand.nextInt(4) + 1;
        Cell cell = new Cell(x, y, z);

        // Domanda: conviene fare un unico metodo per queste due istruzioni?
        cell.setWorker(worker);
        worker.setPosition(cell.getIndex());

        assertNotNull(cell.getWorker());
        assertNotNull(worker.getPosition());
        worker.delete(cell);
        assertNull(cell.getWorker());
        assertNull(worker.getPosition());
    }

    @Test
    public void testSetPosition_correctPosition_expectedEquals(){
        Random rand = new Random();
        int x = rand.nextInt(5) + 1;
        int y = rand.nextInt(5) + 1;
        int z = rand.nextInt(4) + 1;
        Index pos = new Index(x, y, z);
        worker.setPosition(pos);
        assertEquals(pos, worker.getPosition());
    }
    @Test
    public void testSetPosition_differentPosition_expectedEquals(){
        Random rand = new Random();
        int x = rand.nextInt(5) + 1;
        int y = rand.nextInt(5) + 1;
        int z = rand.nextInt(4) + 1;
        Index posPlayer = new Index(x, y, z);
        worker.setPosition(posPlayer);
        do {
            x = rand.nextInt(5) + 1;
            y = rand.nextInt(5) + 1;
            z = rand.nextInt(4) + 1;
        } while(x==posPlayer.getX() && y==posPlayer.getY() && z==posPlayer.getZ());
        Index notPosPlayer = new Index(x, y, z);
        assertNotEquals(notPosPlayer, worker.getPosition());
    }
}
