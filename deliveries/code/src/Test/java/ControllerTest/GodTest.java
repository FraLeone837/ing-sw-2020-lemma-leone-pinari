package ControllerTest;

import Controller.Gods.Apollo;
import Controller.Gods.God;
import ControllerTest.UtilClasses.Utils;
import Model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class GodTest {
    private Match match;
    private Worker myWorker;
    private Worker enemyWorker;
    private God god;

    @Before
    public void setUp(){
        Utils utils = new Utils();
        Index ix = utils.generateRandomIndex();
        this.god = utils.generateRandomGod();

        System.out.println(god.getDescription());
        System.out.println(god.getName());

        this.myWorker = new Worker();
        this.enemyWorker = new Worker();

        match = new Match(1);
        match.initWorker(myWorker, ix);

        god.setup(match, new Player("x",3));

        Index index = utils.getPseudoAdjacent(myWorker);
        match.initWorker(this.enemyWorker,index);
    }

    @After
    public void tearDown(){
        match = null;
    }

    @Test
    public void whereToMoveTest(){
        ArrayList<Index> listWhereToMove = god.whereToMove(match,myWorker,myWorker.getPosition());
        for(Index ix : listWhereToMove){
            assertTrue(ix.getZ()>=0 && ix.getZ()<4
                        && ix.getX()>=0 && ix.getX()<5
                        && ix.getY()>=0 && ix.getY()<5);
        }

        for(Index ix : listWhereToMove){
            if(!god.equals(new Apollo()))
            assertFalse(ix.equals(myWorker.getPosition()));
            assertTrue((match.selectCell(ix).getWorker() == null));
            assertTrue(ix.getZ() <= 1+myWorker.getPosition().getZ());
            assertTrue(((ix.getX()-1 == myWorker.getPosition().getX() || ix.getX()+1 == myWorker.getPosition().getX())
                    || (ix.getX() == myWorker.getPosition().getX() && (ix.getY()+1 == myWorker.getPosition().getY() || ix.getY()-1 == myWorker.getPosition().getY() ))));
            assertTrue(
                    ((ix.getY()-1 == myWorker.getPosition().getY() || ix.getY()+1 == myWorker.getPosition().getY())
                            || (ix.getY() == myWorker.getPosition().getY() && (ix.getX()+1 == myWorker.getPosition().getX() || ix.getX()-1 == myWorker.getPosition().getX() ))));
        }
    }

    @Test
    public void whereToBuild(){
        Utils utils =new Utils();
        Index toMove;
        do{
            toMove = utils.getPseudoAdjacent(myWorker);

        } while(toMove.getZ() != myWorker.getPosition().getZ());
        ArrayList<Index> listWhereToBuild = god.whereToBuild(match,myWorker,toMove);
        for(Index ix : listWhereToBuild){
            assertTrue(ix.getZ()>=0 && ix.getZ()<4
                    && ix.getX()>=0 && ix.getX()<5
                    && ix.getY()>=0 && ix.getY()<5);
        }

        for(Index ix : listWhereToBuild){

            assertFalse(toMove.equals(ix));
            assertTrue(((ix.getX()-1 == toMove.getX() || ix.getX()+1 == toMove.getX())
                            || (ix.getX() == toMove.getX() && (ix.getY()+1 == toMove.getY() || ix.getY()-1 == toMove.getY() ))));
            assertTrue(
                    ((ix.getY()-1 == toMove.getY() || ix.getY()+1 == toMove.getY())
                            || (ix.getY() == toMove.getY() && (ix.getX()+1 == toMove.getX() || ix.getX()-1 == toMove.getX() ))));
        }
    }

    @Test
    public void checkWin(){
        Index oldPosition = myWorker.getPosition();
        Index nextPosition;
        Utils utils = new Utils();
        do{
            nextPosition = utils.getPseudoAdjacent(myWorker);
        } while(nextPosition.equals(enemyWorker));

        god.turn(match, myWorker, nextPosition, utils.getPseudoAdjacent(nextPosition));

        if(oldPosition.getZ() == 2 && nextPosition.getZ() == 3){
            assertTrue(god.checkWin(match,myWorker));
        }
        else assertFalse(god.checkWin(match,myWorker));
    }
}
