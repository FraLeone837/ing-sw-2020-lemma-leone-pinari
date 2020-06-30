package ControllerTest;

import Controller.Gods.Demeter;
import ControllerTest.UtilClasses.GameCreator;
import ControllerTest.UtilClasses.Utils;
import Model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DemeterTest extends GodTest implements TestGod {


    private int secondCellToBuild;
    private String yesOrNo;

    /**
     * moves in CELL_C3 and builds in CELL_B2 and CELL_B3
     */

    @Before
    @Override
    public void setUp(){

        this.chosenGod = GameCreator.Demeter;
        super.setUp();
        secondCellToBuild = game.CELL_B3;
        workerId = 1;
    }


    @Test
    public void testTurn_OneBuild_OneBuildingBuilt(){
        yesOrNo = "no";
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        assertFalse(match.selectCell(new Index(secondCellToBuild)).isBuilding());
    }

    @Override
    public synchronized void controller(){
        playerOne.addInput(workerId);
        playerOne.addInput(cellWhereToMove);
        playerOne.addInput(cellWhereToBuild);
        playerOne.addInput(secondCellToBuild);
        playerOne.addInput(yesOrNo);
    }

    @Test
    public void testTurn_BuildTwice_TwoBuildingsBuilt(){
        yesOrNo = "yes";
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        assertTrue(match.selectCell(new Index(secondCellToBuild)).isBuilding());
    }
}
