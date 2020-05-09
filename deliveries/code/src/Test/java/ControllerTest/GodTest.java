package ControllerTest;

import Controller.*;
import Model.*;
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

        god.getDescription();
        System.out.println(god.getName());

        this.myWorker = new Worker();
        this.enemyWorker = new Worker();

        match = new Match(1);
        match.initWorker(myWorker, ix);

        god.setup(match, new Player("x",3));

        Index index = utils.getPseudoAdjacent(myWorker);
        match.initWorker(this.enemyWorker,index);
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
            assertFalse(ix.equals(myWorker.getPosition()));
            assertTrue((match.selectCell(ix).getWorker() == null));
            assertTrue(ix.getZ() <= myWorker.getPosition().getZ());
            assertTrue(((ix.getX()-1 == myWorker.getPosition().getX() || ix.getX()+1 == myWorker.getPosition().getX())
                    || (ix.getX() == myWorker.getPosition().getX() && (ix.getY()+1 == myWorker.getPosition().getY() || ix.getY()-1 == myWorker.getPosition().getY() ))));
            assertTrue(
                    ((ix.getY()-1 == myWorker.getPosition().getY() || ix.getY()+1 == myWorker.getPosition().getY())
                            || (ix.getY() == myWorker.getPosition().getY() && (ix.getX()+1 == myWorker.getPosition().getX() || ix.getX()-1 == myWorker.getPosition().getX() ))));
        }
    }

    @Test
    public void whereToBuild(){
        ArrayList<Index> listWhereToMove = god.whereToBuild(match,myWorker,myWorker.getPosition());
        for(Index ix : listWhereToMove){
            assertTrue(ix.getZ()>=0 && ix.getZ()<4
                    && ix.getX()>=0 && ix.getX()<5
                    && ix.getY()>=0 && ix.getY()<5);
        }
        for(Index ix : listWhereToMove){
            assertFalse(ix.equals(myWorker.getPosition()));
            assertTrue((match.selectCell(ix).getWorker() == null));
            assertTrue(ix.getZ() <= myWorker.getPosition().getZ());
            assertTrue(((ix.getX()-1 == myWorker.getPosition().getX() || ix.getX()+1 == myWorker.getPosition().getX())
                            || (ix.getX() == myWorker.getPosition().getX() && (ix.getY()+1 == myWorker.getPosition().getY() || ix.getY()-1 == myWorker.getPosition().getY() ))));
            assertTrue(
                    ((ix.getY()-1 == myWorker.getPosition().getY() || ix.getY()+1 == myWorker.getPosition().getY())
                            || (ix.getY() == myWorker.getPosition().getY() && (ix.getX()+1 == myWorker.getPosition().getX() || ix.getX()-1 == myWorker.getPosition().getX() ))));
        }
    }

    @Test
    public void checkWin(){

    }
}
