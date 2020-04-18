package ModelTest;

import Model.*;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class MatchTest {
    private Match match;

    @org.junit.Before
    public void setUp() throws Exception {
        Random rand = new Random();
        int id = rand.nextInt(10);
        match = new Match(id);
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPlayers(){
        String name = "generic name";
        Random rand = new Random();
        int playerId = rand.nextInt(1);
        Player player = new Player(name, playerId);
        match.initPlayers(player);
        assertTrue(match.getPlayers().contains(player));
        match.removePlayer(player);
        assertFalse(match.getPlayers().contains(player));
    }

    @Test
    public void testMoveWorker(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        int y = rand.nextInt(5);
        int z = rand.nextInt(4);
        Index index = new Index(x, y, z);
        Worker worker = new Worker();
        worker.setPosition(index);
        int x2 = rand.nextInt(5);
        int y2 = rand.nextInt(5);
        int z2 = rand.nextInt(4);
        Index index2 = new Index(x2, y2, z2);
        match.moveWorker(worker, index2);
        assertEquals(index2.getX(), worker.getPosition().getX());
        assertEquals(index2.getY(), worker.getPosition().getY());
        assertEquals(index2.getZ(), worker.getPosition().getZ());
    }

    @Test
    public void testBuild(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        int y = rand.nextInt(5);
        int z = rand.nextInt(4);
        Index index = new Index(x, y, z);
        assertTrue(match.selectCell(index).isEmpty());
        Worker worker = new Worker();
        match.build(worker, index);
        assertFalse(match.selectCell(index).isEmpty());
    }

    @Test
    public void testBuildInvisible(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        int y = rand.nextInt(5);
        int z = rand.nextInt(4);
        Index index = new Index(x, y, z);
        assertTrue(match.selectCell(index).getForbidden().size()==0);
        Player player = new Player("generic_name", 1);
        Invisible invisible = new Invisible(player);
        match.buildInvisible(invisible, index);
        assertFalse(match.selectCell(index).getForbidden().size()==0);

    }

    @Test
    public void testWorker(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        int y = rand.nextInt(5);
        int z = rand.nextInt(4);
        Index index = new Index(x, y, z);
        Worker worker = new Worker();
        match.initWorker(worker,index);
        assertEquals(worker, match.selectCell(index).getWorker());
        match.removeWorker(worker);
        assertNull(match.selectCell(index).getWorker());
    }
}
