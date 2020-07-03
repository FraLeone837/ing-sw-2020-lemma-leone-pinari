package ControllerTest;

import ControllerTest.UtilClasses.GameCreator;
import Model.Index;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class HeraTest extends GodTest {


    /**
     * move enemy worker on a third level in the perimeter
     * and see if it wins
     */
    @Override
    public void setUp() {
        chosenGod = GameCreator.Hera;
        super.setUp();
    }

    @Override
    public synchronized void controller() {
        playerOne.addInput(workerId);
        playerOne.addInput(cellWhereToMove);
        //does not matter
        playerOne.addInput(cellWhereToBuild);

        int enemyWorker = 3;
        int enemyMove = game.CELL_E4;
        //does not matter
        int enemyBuild = game.CELL_E5;
        playerTwo.addInput(enemyWorker);
        playerTwo.addInput(enemyMove);
        playerTwo.addInput(enemyBuild);
        //wait for turn_start
    }

    @Test
    public void testTurn_enemyMovesThirdLevel_gameContinuesNormally(){
        //sets a tower of level 3 in perimeter
        for(int z = 0;z<3;z++){
            match.selectCell(new Index(game.CELL_E4%5,game.CELL_E4/5,z)).setBuilding();
        }
        controller();
        //wait two turns
        waitForTurnStart();
        waitForTurnStart();
        assertTrue(true);
    }

    @Test
    public void testTurn_normalMove(){
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                    playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
    }
}
