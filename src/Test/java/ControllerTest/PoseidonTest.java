package ControllerTest;

import ControllerTest.UtilClasses.GameCreator;
import Model.Index;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class PoseidonTest extends GodTest {
    String yesOrNo;
    int otherCellToBuild;
    int lastCellToBuild;

    @Override
    public void setUp() {
        chosenGod = GameCreator.Poseidon;
        super.setUp();
    }

    @Test
    public void testTurn_normalMove(){
        yesOrNo = "no";
        controller();
        waitForTurnStart();

        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
    }

    @Override
    public synchronized void controller() {
        playerOne.addInput(yesOrNo);
        super.controller();
        //3 yes-es or no-es
        playerOne.addInput(yesOrNo);
        playerOne.addInput(yesOrNo);
        playerOne.addInput(yesOrNo);
        otherCellToBuild = game.CELL_B5;
        playerOne.addInput(otherCellToBuild);

        lastCellToBuild = game.CELL_B4;
        playerOne.addInput(lastCellToBuild);
        playerOne.addInput(lastCellToBuild);
    }

    @Test
    public void testTurn_usePower_buildThreeTimes(){
        yesOrNo = "yes";
        controller();
        waitForTurnStart();

        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        assertTrue(match.selectCell(new Index(otherCellToBuild)).isBuilding());
        assertTrue(match.selectCell(new Index(lastCellToBuild)).isBuilding());
        assertTrue(match.selectCell(new Index(lastCellToBuild%5,lastCellToBuild/5,1)).isBuilding());
    }
}
