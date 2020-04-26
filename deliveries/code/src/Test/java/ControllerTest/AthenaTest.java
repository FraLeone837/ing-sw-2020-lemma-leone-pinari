package ControllerTest;

import Controller.Apollo;
import Controller.Athena;
import Model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class AthenaTest {
    private Match match;
    private Worker myWorker;
    private Athena athena;
    private Worker enemyWorker;
    private Worker enemyWorker2;
    private Player me;
    private Player enemy;
    private Apollo apollo;

    @Before
    public void setUp(){
        match = new Match(1);
        athena = new Athena();
        me = new Player("Me",1);
        enemy = new Player("Enemy",2);
        apollo = new Apollo();

        me.setWorker1Id(1);
        enemy.setWorker1Id(3);
        enemy.setWorker2Id(4);
        myWorker = me.getWorker1();
        enemyWorker = enemy.getWorker1();
        enemyWorker2 = enemy.getWorker2();

        Utils utils = new Utils();

        match.initPlayers(me);
        match.initPlayers(enemy);
        Index ix;
        do{
            ix = utils.generateRandomIndex();
        } while(ix.getZ() == 4);
        match.initWorker(myWorker,utils.generateRandomIndex());
        match.initWorker(enemyWorker,utils.generateRandomIndex());
        match.initWorker(enemyWorker2,utils.generateRandomIndex());
        athena.setup(match,me);
    }

    @After
    public void TearDown(){
        match = null;
        myWorker = null;
        athena = null;
        enemyWorker = null;
        enemyWorker2 = null;
        me = null;
        enemy = null;
        apollo = null;
    }

    @Test
    public void testTurn_NoUp_NoForbiddensExist(){
        Index moveIndex;
        Utils utils = new Utils();
        do{
            moveIndex = utils.getPseudoAdjacent(myWorker.getPosition());
        } while( moveIndex.getZ() > myWorker.getPosition().getZ() || enemyWorker.getPosition().equals(moveIndex) || enemyWorker2.getPosition().equals(moveIndex));

        Index buildIndex = utils.getPseudoAdjacent(moveIndex);
        athena.turn(match,myWorker,moveIndex,buildIndex);
        assertTrue(match.selectCell(buildIndex).isBuilding());
        assertEquals(moveIndex, myWorker.getPosition());

        do{
            moveIndex = utils.getPseudoAdjacent(enemyWorker.getPosition());
        }while(moveIndex.getZ() != enemyWorker.getPosition().getZ() || moveIndex.equals(myWorker.getPosition()));

        buildIndex = utils.getPseudoAdjacent(moveIndex);
        apollo.turn(match,enemyWorker,moveIndex,buildIndex);

        assertEquals(moveIndex,enemyWorker.getPosition());
        assertTrue(match.selectCell(buildIndex).isBuilding());

        ArrayList<Index> inxArray = utils.getAllLevelsAbove(enemyWorker);
        for(Index inx : inxArray) {
            ArrayList<Invisible> invisibles = match.selectCell(inx).getForbidden();
            for(Invisible inv : invisibles)
                assertFalse(inv.isIn(enemyWorker));
        }
        inxArray = utils.getAllLevelsAbove(enemyWorker2);
        for(Index inx : inxArray) {
            ArrayList<Invisible> invisibles = match.selectCell(inx).getForbidden();
            for(Invisible inv : invisibles)
                assertFalse(inv.isIn(enemyWorker2));
        }
    }

    @Test
    public void testTurn_GoUp_ForbiddensExist(){

        Index moveIndex;
        Utils utils = new Utils();
        match.moveWorker(myWorker,new Index(2,3,1));
        do{
            moveIndex = utils.getPseudoAdjacent(myWorker.getPosition());
            if(enemyWorker2.getPosition().equals(myWorker.getPosition())){
                match.moveWorker(enemyWorker2, new Index(1,2,0));
            }
            if(enemyWorker.getPosition().equals(myWorker.getPosition())){
                match.moveWorker(enemyWorker, new Index(3,1,0));
            }
        }while(myWorker.getPosition().getZ()+1 != moveIndex.getZ() || enemyWorker2.getPosition().equals(moveIndex) || enemyWorker.getPosition().equals(moveIndex));

        Index buildIndex = utils.getPseudoAdjacent(moveIndex);

        athena.turn(match,myWorker,moveIndex,buildIndex);

        assertTrue(match.selectCell(buildIndex).isBuilding());
        assertEquals(moveIndex,myWorker.getPosition());

        ArrayList<Index> inxArray = utils.getAllLevelsAbove(enemyWorker);
        for(Index inx : inxArray) {
            ArrayList<Invisible> invisibles = match.selectCell(inx).getForbidden();
            for(Invisible inv : invisibles)
                assertTrue(inv.isIn(enemyWorker));
        }
        inxArray = utils.getAllLevelsAbove(enemyWorker2);
        for(Index inx : inxArray) {
            ArrayList<Invisible> invisibles = match.selectCell(inx).getForbidden();
            for(Invisible inv : invisibles)
                assertTrue(inv.isIn(enemyWorker2));
        }
    }

}
