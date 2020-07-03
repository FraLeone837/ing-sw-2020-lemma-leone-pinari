package ControllerTest;

import ControllerTest.UtilClasses.GameCreator;
import Model.Index;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ZeusTest extends GodTest {

    String yesOrNo;
    @Override
    public void setUp() {
        chosenGod = GameCreator.Zeus;
        super.setUp();
    }

    @Test
    public void testTurn_normalMove(){
        cellWhereToMove = game.CELL_C3;
        yesOrNo = "no";
        controller();
        waitForTurnStart();

        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
    }

    @Override
    public synchronized void controller() {
        playerOne.addInput(workerId);
        playerOne.addInput(yesOrNo);
        playerOne.addInput(cellWhereToMove);
        if(yesOrNo.equals("yes"))
            playerOne.addInput(cellWhereToMove);
        playerOne.addInput(cellWhereToBuild);
    }

    @Test
    public void testTurn_usePower_moveOneLevelUp(){
        yesOrNo = "yes";
        controller();
        waitForTurnStart();

        assertTrue(match.selectCell(new Index(cellWhereToMove)).isBuilding());
        assertEquals(match.selectCell(new Index(cellWhereToMove%5,cellWhereToMove/5,1)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
    }
}
