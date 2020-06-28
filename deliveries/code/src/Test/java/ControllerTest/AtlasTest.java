package ControllerTest;

import Controller.Gods.Atlas;
import ControllerTest.UtilClasses.GameCreator;
import ControllerTest.UtilClasses.Utils;
import Model.Index;
import Model.Match;
import Model.Player;
import Model.Worker;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class AtlasTest extends GodTest implements TestGod {

    public String yesOrNo;

    @org.junit.Before
    @Override
    public void setUp(){
        chosenGod = GameCreator.Atlas;
        super.setUp();
    }

    @org.junit.Test
    public void turnTest_buildNormalLevel_CorrectInput(){
        yesOrNo = "no";
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                     playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        assertFalse(match.selectCell(new Index(cellWhereToBuild)).isDome());
    }

    @Override
    public synchronized void controller(){
        super.controller();
        playerOne.addInput(yesOrNo);
    }

    @org.junit.Test
    public void turnTest_buildDome_CorrectInput(){
        yesOrNo = "yes";
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());

        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isDome());
    }


}
