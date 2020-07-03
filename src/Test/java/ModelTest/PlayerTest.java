package ModelTest;

import Model.Cell;
import Model.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PlayerTest {
    Player player;
    private int id;
    private String name;
    @Before
    public void setUp() throws Exception {
        Random rand = new Random();
        id = rand.nextInt(2) + 1;
        name = "generic_name";
        player = new Player(name, id);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetIdPlayer_sameId_expectedEquals(){
        assertEquals(id, player.getIdPlayer());
    }

    @Test
    public void testGetName_sameName_expectedEquals(){
        assertEquals(name, player.getName());
    }

    @Test
    public void testGetName_differentName_expectedNotEquals(){
        String notName = "other_name";
        assertNotEquals(notName, player.getName());
    }
    @Test
    public void testIdWorker1_sameId_expectedEquals(){
        Random rand = new Random();
        int workerId = rand.nextInt(4) + 1;
        player.setWorker1Id(workerId);
        assertEquals(workerId, player.getWorker1().getIdWorker());
    }
    @Test
    public void testIdWorker1_differentId_expectedNotEquals(){
        Random rand = new Random();
        int workerId = rand.nextInt(4) + 1;
        player.setWorker1Id(workerId);
        int notWorkerId;
        do{
            notWorkerId = rand.nextInt(4) + 1;
        } while(workerId == notWorkerId);
        assertNotEquals(notWorkerId, player.getWorker1().getIdWorker());
    }
    @Test
    public void testIdWorker2_sameId_expectedSame(){
        Random rand = new Random();
        int workerId = rand.nextInt(4) + 1;
        player.setWorker2Id(workerId);
        assertEquals(workerId, player.getWorker2().getIdWorker());
    }
    @Test
    public void testIdWorker2_differentId_expectedNotEquals(){
        Random rand = new Random();
        int workerId = rand.nextInt(4) + 1;
        player.setWorker2Id(workerId);
        int notWorkerId;
        do{
            notWorkerId = rand.nextInt(4) + 1;
        } while(workerId == notWorkerId);
        assertNotEquals(notWorkerId, player.getWorker2().getIdWorker());
    }
}
