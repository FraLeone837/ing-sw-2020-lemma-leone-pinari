package ModelTest;

import Model.Cell;
import Model.Invisible;
import Model.Player;
import Model.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class InvisibleTest {
    private Invisible invisible;
    private int id;
    private Player creator;
    @Before
    public void setUp() throws Exception {
        Random rand = new Random();
        id = rand.nextInt(2);
        creator = new Player("generic_name", id);
        invisible = new Invisible(creator);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetCreator_samePlayer_expectedEquals(){
        assertEquals(creator, invisible.getCreator());
    }

    @Test
    public void testGetCreator_differentPlayer_expectedNotEquals(){
        Random rand = new Random();
        int notId;
        do {
            notId = rand.nextInt(2);
        } while(id == notId);
        Player otherPlayer = new Player("other_name", notId);
        assertNotEquals(otherPlayer, invisible.getCreator());
    }

    @Test
    public void testWorker(){
        Worker worker = new Worker();
        invisible.addWorker(worker);
        assertTrue(invisible.isIn(worker));
        invisible.removeWorkers();
        assertFalse(invisible.isIn(worker));
    }

}
