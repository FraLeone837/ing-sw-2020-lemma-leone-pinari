package ControllerTest;

import ControllerTest.UtilClasses.GameCreator;
import Model.Index;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TritonTest extends GodTest {
    String yesOrNo;
    int cellWhereToMove2;
    int cellWhereToMove3;
    int cellWhereToMove4;

    @Override
    public void setUp() {
        chosenGod = GameCreator.Triton;
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

    @Test
    public void testTurn_usePower_moveAroundPerimeter(){
        yesOrNo = "yes";
        cellWhereToMove = game.CELL_B5;
        cellWhereToMove2 = game.CELL_A5;
        cellWhereToMove3 = game.CELL_A4;
        cellWhereToMove4 = game.CELL_B4;
        controller();
        waitForTurnStart();

        assertEquals(match.selectCell(new Index(cellWhereToMove4)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
    }

    @Override
    public synchronized void controller() {
        playerOne.addInput(workerId);
        playerOne.addInput(cellWhereToMove);
        if(yesOrNo.equals( "yes")){
            playerOne.addInput(cellWhereToMove2);
            playerOne.addInput(cellWhereToMove3);
            playerOne.addInput(cellWhereToMove4);
        }
        playerOne.addInput(cellWhereToBuild);
        playerOne.addInput(yesOrNo);
        playerOne.addInput(yesOrNo);
        playerOne.addInput(yesOrNo);
        playerOne.addInput(yesOrNo);
    }
}
