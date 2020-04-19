package ControllerTest;

import Controller.Apollo;
import Controller.Athena;
import ControllerTest.Utils;
import Model.Index;
import Model.Match;
import Model.Player;
import Model.Worker;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

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
        myWorker = me.getWorker1();
        enemyWorker = enemy.getWorker1();
        Utils utils = new Utils();
        match.initPlayers(me);
        match.initPlayers(enemy);
        System.out.println("Enemy worker ix: " + enemy.getWorker1().getPosition());
        match.initWorker(myWorker,utils.generateRandomIndex());;
        match.initWorker(enemyWorker2,utils.generateRandomIndex());
        match.initWorker(enemyWorker,utils.generateRandomIndex());

        System.out.println("Enemy worker ix: " + enemy.getWorker1().getPosition());
    }

    @Test
    public void testTurn_NoUp_NoForbiddensExist(){

        Index moveIndex;
        Utils utils = new Utils();
        do{
            moveIndex = utils.getPseudoAdjacent(myWorker.getPosition());
        } while(myWorker.getPosition().getZ() != moveIndex.getZ() || enemyWorker.getPosition().equals(moveIndex));
        Index buildIndex = utils.getPseudoAdjacent(moveIndex);
        athena.turn(match,myWorker,moveIndex,buildIndex);
        assertTrue(match.selectCell(buildIndex).isBuilding());
        assertEquals(moveIndex,myWorker.getPosition());
        do{
            moveIndex = utils.getPseudoAdjacent(enemyWorker.getPosition());
        }while(moveIndex.getZ()-1 != enemyWorker.getPosition().getZ() || moveIndex.equals(myWorker.getPosition()));

        buildIndex = utils.getPseudoAdjacent(moveIndex);
        apollo.turn(match,enemyWorker,moveIndex,buildIndex);

        assertEquals(moveIndex,enemyWorker.getPosition());
        assertTrue(match.selectCell(buildIndex).isBuilding());

    }

    @Test
    public void testTurn_GoUp_ForbiddensExist(){

        Index moveIndex;
        Utils utils = new Utils();
        do{
            moveIndex = utils.getPseudoAdjacent(myWorker.getPosition());
        } while(myWorker.getPosition().getZ()+1 != moveIndex.getZ() || enemyWorker.getPosition().equals(moveIndex));
        Index buildIndex = utils.getPseudoAdjacent(moveIndex);
        System.out.println("Enemy worker ix " + enemy.getWorker1().getPosition());

        athena.turn(match,myWorker,moveIndex,buildIndex);

        assertTrue(match.selectCell(buildIndex).isBuilding());
        assertEquals(moveIndex,myWorker.getPosition());

        Index[] inxArray = utils.getAllLevelAboves(enemyWorker);
        for(Index inx : inxArray)
        assertEquals(1,match.selectCell(inx).getForbidden().size());
    }

}
