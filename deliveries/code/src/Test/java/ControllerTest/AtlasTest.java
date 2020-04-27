package ControllerTest;

import Controller.Atlas;
import Model.Index;
import Model.Match;
import Model.Worker;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class AtlasTest {

    private Match match;
    private Worker myWorker;
    private Atlas atlas;

    @org.junit.Before
    public void setUp(){
        this.match = new Match(1);
        this.myWorker = new Worker();
        this.atlas = new Atlas();
        atlas.getDescription();
        atlas.getName();
        Utils utils = new Utils();
        match.initWorker(myWorker, utils.generateRandomIndex());
    }

    @org.junit.Test
    public void turnTest_buildNormalLevel_CorrectInput(){
        Utils utils = new Utils();
        atlas.setBuildDome(false);
        Index oldPosition = myWorker.getPosition();


        Index moveIndex = utils.getPseudoAdjacent(myWorker);
        Index buildIndex = utils.getPseudoAdjacent(moveIndex);
        atlas.turn(match, myWorker, moveIndex, buildIndex);

        assertEquals(myWorker, match.selectCell(moveIndex).getWorker());

        assertTrue(match.selectCell(buildIndex).isBuilding());
        if(buildIndex.getZ() != 3) {
            assertFalse(match.selectCell(buildIndex).isDome());
        }
        assertNull(match.selectCell(oldPosition).getWorker());

    }

    @org.junit.Test
    public void turnTest_buildDome_CorrectInput(){
        Utils utils = new Utils();
        Index oldPosition = myWorker.getPosition();


        atlas.setBuildDome(true);
        Index moveIndex = utils.getPseudoAdjacent(myWorker);
        Index buildIndex = utils.getPseudoAdjacent(moveIndex);
        atlas.turn(match, myWorker, moveIndex, buildIndex);

        assertEquals(myWorker, match.selectCell(moveIndex).getWorker());

        assertTrue(match.selectCell(buildIndex).isBuilding());
        assertTrue(match.selectCell(buildIndex).isDome());

        assertNull(match.selectCell(oldPosition).getWorker());
    }


}
