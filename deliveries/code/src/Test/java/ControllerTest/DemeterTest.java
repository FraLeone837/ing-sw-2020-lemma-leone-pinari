package ControllerTest;

import Controller.Gods.Demeter;
import Model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DemeterTest {

    private Worker myWorker;
    private Match match;
    private Demeter demeter;
    Player player;

    @Before
    public void setUp(){
        Utils util = new Utils();
        myWorker = new Worker();
        match = new Match(1);
        demeter = new Demeter();
        demeter.getDescription();
        demeter.getName();
        player = new Player("Costui",1);
        demeter.setup(match, player);
        match.initWorker(myWorker, util.generateRandomIndex());
    }

    @After
    public void tearDown(){
        match = null;
        myWorker = null;
        demeter = null;
    }

    @Test
    public void testTurn_OneBuild_OneBuildingBuilt(){
        Utils utils = new Utils();
        demeter.setBuildAgain(false);
        Index oldPosition = myWorker.getPosition();
        Index moveIndex;
        do{
            moveIndex = utils.getPseudoAdjacent(myWorker);
        }while(moveIndex.equals(oldPosition));

        Index buildIndex = utils.getPseudoAdjacent(moveIndex);
        Index buildOther;
        do{
            buildOther = utils.getPseudoAdjacent(moveIndex);
        } while (buildOther.equals(buildIndex));

        demeter.turn(match, myWorker,moveIndex,buildIndex,buildOther);

        assertEquals(moveIndex, myWorker.getPosition());
        assertTrue(match.selectCell(buildIndex).isBuilding());

        assertFalse(match.selectCell(buildOther).isBuilding());
        assertNull(match.selectCell(oldPosition).getWorker());

    }

    @Test
    public void testTurn_BuildTwice_TwoBuildingsBuilt(){
        Utils utils = new Utils();
        demeter.setBuildAgain(true);
        Index oldPosition = myWorker.getPosition();
        Index moveIndex;
        do{
            moveIndex = utils.getPseudoAdjacent(myWorker);
        }
        while(moveIndex.equals(oldPosition));
        Index buildIndex = utils.getPseudoAdjacent(moveIndex);
        Index buildOther;
        do{
            buildOther = utils.getPseudoAdjacent(moveIndex);
        } while (buildOther.equals(buildIndex));
        demeter.turn(match, myWorker,moveIndex,buildIndex,buildOther);

        assertEquals(moveIndex, myWorker.getPosition());

        assertTrue(match.selectCell(buildIndex).isBuilding());

        assertTrue(match.selectCell(buildOther).isBuilding());

        assertNull(match.selectCell(oldPosition).getWorker());

    }
}
