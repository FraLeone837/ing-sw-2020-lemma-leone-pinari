package ControllerTest;

import Controller.Artemis;
import Model.Index;
import Model.Match;
import Model.Worker;

import java.io.IOException;
import java.util.Random;

import static org.junit.Assert.*;

public class ArtemisTest {


    private Artemis artemis;
    private Worker myWorker;
    private Match match;

    @org.junit.Before
    public void setUp(){
        Utils utils = new Utils();
        this.artemis = new Artemis();
        artemis.getDescription();
        artemis.getName();
        this.myWorker = new Worker();
        this.match = new Match(1);
        Index index = utils.generateRandomIndex();
        match.initWorker(myWorker,index);
    }

    @org.junit.Test
    public void turnTest_MovesTwice_ChecksPosition(){
        artemis.setMoveAgain(true);
        Utils util = new Utils();
        Index oldPosition = myWorker.getPosition();


        Index firstMove = util.generateRandomIndex();
        Index secondMove = util.generateRandomIndex();
        Index firstBuild = util.generateRandomIndex();
        while (firstMove.equals(secondMove)) {
            secondMove = util.generateRandomIndex();
        }
        artemis.turn(match, myWorker, firstMove, secondMove, firstBuild);

        assertEquals(myWorker,match.selectCell(secondMove).getWorker());

        assertFalse(match.selectCell(firstBuild).isEmpty());
        assertNull(match.selectCell(oldPosition).getWorker());
    }

    @org.junit.Test
    public void turnTest_MovesOnce_ChecksPosition(){
        artemis.setMoveAgain(false);
        Utils util = new Utils();
        Index oldPosition = myWorker.getPosition();


        Index firstMove = util.generateRandomIndex();
        Index secondMove = util.generateRandomIndex();
        Index firstBuild = util.generateRandomIndex();
        while (firstMove.equals(secondMove)) {
            secondMove = util.generateRandomIndex();
        }
        artemis.turn(match, myWorker, firstMove, secondMove, firstBuild);

        assertEquals(myWorker,match.selectCell(firstMove).getWorker());

        assertTrue(match.selectCell(firstBuild).isBuilding());

        assertNull(match.selectCell(oldPosition).getWorker());
    }


    //Not yet implemented but there should be a test in which the God checks if I'm moving in the same position
//    @org.junit.Test
//    public void turnTest_MovesTwiceSamePosition_GivesError(){
//        artemis.setMoveAgain(true);
//        Utils util = new Utils();
//        Index firstMove = util.generateRandomIndex();
//        Index firstBuild = util.generateRandomIndex();
//        try{
//            artemis.turn(match, myWorker, firstMove, firstMove, firstBuild);
//
//        } catch (IOException Io){
//            assertTrue(true);
//        }
//
//    }

}
