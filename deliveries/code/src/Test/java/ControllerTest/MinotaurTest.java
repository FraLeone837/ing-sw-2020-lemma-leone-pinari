package ControllerTest;

import ControllerTest.UtilClasses.GameCreator;
import Model.Index;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class MinotaurTest extends GodTest implements TestGod {

    @Override
    @Before
    public void setUp(){
        chosenGod = GameCreator.Minotaur;
        super.setUp();
    }
    @Test
    public void testTurn_normalMove(){
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
    }
    @Test
    public void testTurn_pushOtherWorker(){
        Index otherPlayerPos = playerManagerTwo.getPlayer().getWorker1().getPosition();
        cellWhereToMove = otherPlayerPos.getX() + 5*otherPlayerPos.getY();
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertEquals(match.selectCell(new Index(cellWhereToMove+5)).getWorker().getIdWorker(),
                playerManagerTwo.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
    }
}
