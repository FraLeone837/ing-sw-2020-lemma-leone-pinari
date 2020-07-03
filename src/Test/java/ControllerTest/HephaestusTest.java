package ControllerTest;

import ControllerTest.UtilClasses.GameCreator;
import Model.Index;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class HephaestusTest extends GodTest implements TestGod {
    public String yesOrNo;

    @Before
    @Override
    public void setUp() {
        chosenGod = GameCreator.Hephaestus;
        super.setUp();
    }
    @org.junit.Test
    public void testTurn_buildOnce(){
        yesOrNo = "no";
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        assertFalse(match.selectCell(new Index(cellWhereToBuild%5,cellWhereToBuild/5,1)).isBuilding());
    }

    @Test
    public void testTurn_buildTwice_NoDome(){
        yesOrNo = "yes";
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        assertTrue(match.selectCell(new Index(cellWhereToBuild%5,cellWhereToBuild/5,1)).isBuilding());

    }

    @Test
    public void testTurn_buildTwiceSecondLevel_NoDome(){
        match.selectCell(new Index(cellWhereToBuild)).setBuilding();
        match.selectCell(new Index(cellWhereToBuild%5, cellWhereToBuild/5,1)).setBuilding();
        //should not ask whether or not I want to build another level, cause that would be building a dome
        yesOrNo = "yes";
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        assertTrue(match.selectCell(new Index(cellWhereToBuild%5,cellWhereToBuild/5,1)).isBuilding());
        assertTrue(match.selectCell(new Index(cellWhereToBuild%5,cellWhereToBuild/5,2)).isBuilding());
        assertFalse(match.selectCell(new Index(cellWhereToBuild%5,cellWhereToBuild/5,2)).isDome());
        assertTrue(match.selectCell(new Index(cellWhereToBuild%5,cellWhereToBuild/5,3)).isEmpty());


    }

    @Override
    public synchronized void controller() {
        super.controller();
        playerOne.addInput(yesOrNo);
    }
}
