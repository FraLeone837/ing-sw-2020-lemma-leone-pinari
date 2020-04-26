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



    @Test
    public void testPlayers_InsertAndRemove_isInsertedIsRemoved(){
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
    public void testMoveWorker_MoveFromFirstPosition_True(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        int y = rand.nextInt(5);
        int z = rand.nextInt(4);
        Index index = new Index(x, y, z);
        Worker worker = new Worker();

        match.initWorker(worker,index);
        assertEquals(x, worker.getPosition().getX());
        assertEquals(y, worker.getPosition().getY());
        assertEquals(z, worker.getPosition().getZ());

        x = rand.nextInt(5);
        y = rand.nextInt(5);
        z = rand.nextInt(4);
        Index index2 = new Index(x, y, z);
        match.moveWorker(worker, index2);
        assertEquals(x, worker.getPosition().getX());
        assertEquals(y, worker.getPosition().getY());
        assertEquals(z, worker.getPosition().getZ());

        assertNull(match.selectCell(index).getWorker());

    }

    @Test
    public void testBuild_BuildEverwhere_isBuildingAndisDomeInLevel3(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        int y = rand.nextInt(5);
        int z = rand.nextInt(4);
        Index index = new Index(x, y, z);
        assertTrue(match.selectCell(index).isEmpty());
        Worker worker = new Worker();
        match.build(worker, index);
        assertFalse(match.selectCell(index).isEmpty());
        if(z==3)
            assertTrue(match.selectCell(index).isDome());
    }

    @Test
    public void testBuildInvisible_BuildOneCell_CorrectOutput(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        int y = rand.nextInt(5);
        int z = rand.nextInt(4);
        Index index = new Index(x, y, z);
        assertTrue(match.selectCell(index).getForbidden().size()==0);
        Player player = new Player("generic_name", 1);
        Invisible invisible = new Invisible(player);
        match.buildInvisible(invisible, index);
        assertTrue(match.selectCell(index).getForbidden().size()==1);

    }

    @Test
    public void testInitWorker_CorrectIn_CorrectOut(){
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
