package ControllerTest;

import Controller.Gods.Apollo;
import Model.Index;
import Model.Match;
import Model.Player;
import Model.Worker;
import org.junit.Test;

import java.util.ArrayList;


import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class ApolloTest {


    private Match match;
    private Worker myWorker;
    private Worker enemyWorker;
    private Apollo apollo;

    @org.junit.Before
    public void setUp(){

        Utils utils = new Utils();
        Index ix = utils.generateRandomIndex();
        this.apollo = new Apollo();

        this.myWorker = new Worker();
        this.enemyWorker = new Worker();

        match = new Match(1);
        match.initWorker(myWorker, ix);

        apollo.setup(match, new Player("nome", 1));


        Index index = utils.getPseudoAdjacent(myWorker);
        match.initWorker(this.enemyWorker,index);
    }

    @org.junit.Test
    public void testTurn_SwitchPlaces_ExpectedSwitchedPlaces(){
        Utils utils = new Utils();
        Index oldPosition = myWorker.getPosition();
        Index buildIndex = utils.getPseudoAdjacent(enemyWorker);

        Index myWorkerLastPos = myWorker.getPosition();
        Index enemyWorkerLastPos = enemyWorker.getPosition();

        System.out.println("New position " + enemyWorkerLastPos + "\nThis position " + myWorkerLastPos);

        apollo.turn(match, myWorker, enemyWorker.getPosition(), buildIndex);

        assertTrue(enemyWorkerLastPos.equals(myWorker.getPosition()));
        assertTrue(myWorkerLastPos.equals(enemyWorker.getPosition()));
        assertTrue(match.selectCell(buildIndex).isBuilding());
        assertNull(match.selectCell(oldPosition).getWorker());
    }



    @org.junit.Test
    public void testTurn_NormalMovement_CorrectOutput(){
        Utils utils = new Utils();
        Index enemyPosition = enemyWorker.getPosition();

        Index oldPosition = myWorker.getPosition();
        Index moveIndex;
        Index buildIndex;
        do{
            moveIndex = utils.getPseudoAdjacent(myWorker);
        }while (moveIndex.equals(enemyPosition));
        do {

            buildIndex = utils.getPseudoAdjacent(myWorker);
        }while(buildIndex.equals(enemyPosition) && buildIndex.equals(moveIndex));

        apollo.turn(match, myWorker, moveIndex, buildIndex);

        assertEquals(moveIndex,myWorker.getPosition());

        assertEquals(enemyPosition,enemyWorker.getPosition());

        assertTrue(match.selectCell(buildIndex).isBuilding());


        assertNull(match.selectCell(oldPosition).getWorker());
    }

    @Test
    public void whereToMoveTest(){

        ArrayList<Index> listWhereToMove = apollo.whereToMove(match,myWorker,myWorker.getPosition());
        for(Index ix : listWhereToMove){
            assertTrue(ix.getZ()>=0 && ix.getZ()<4
                    && ix.getX()>=0 && ix.getX()<5
                    && ix.getY()>=0 && ix.getY()<5);
        }

        for(Index ix : listWhereToMove){
            assertTrue((match.selectCell(ix).getWorker() != myWorker ));
            assertTrue(ix.getZ() <= myWorker.getPosition().getZ());
            assertTrue(((ix.getX()-1 == myWorker.getPosition().getX() || ix.getX()+1 == myWorker.getPosition().getX())
                    || (ix.getX() == myWorker.getPosition().getX() && (ix.getY()+1 == myWorker.getPosition().getY() || ix.getY()-1 == myWorker.getPosition().getY() ))));
            assertTrue(
                    ((ix.getY()-1 == myWorker.getPosition().getY() || ix.getY()+1 == myWorker.getPosition().getY())
                            || (ix.getY() == myWorker.getPosition().getY() && (ix.getX()+1 == myWorker.getPosition().getX() || ix.getX()-1 == myWorker.getPosition().getX() ))));
        }
    }

    @Test
    public void whereToBuildTest(){
        Utils utils =new Utils();
        Index toMove;
        do{
            toMove = utils.getPseudoAdjacent(myWorker);

        } while(toMove.getZ() != myWorker.getPosition().getZ());
        ArrayList<Index> listWhereToMove = apollo.whereToBuild(match,myWorker,toMove);
        for(Index ix : listWhereToMove){
            assertTrue(ix.getZ()>=0 && ix.getZ()<4
                    && ix.getX()>=0 && ix.getX()<5
                    && ix.getY()>=0 && ix.getY()<5);
        }

        for(Index ix : listWhereToMove){

            assertFalse(toMove.equals(ix));
            assertTrue(ix.getZ() <= myWorker.getPosition().getZ());
            assertTrue(((ix.getX()-1 == toMove.getX() || ix.getX()+1 == toMove.getX())
                    || (ix.getX() == toMove.getX() && (ix.getY()+1 == toMove.getY() || ix.getY()-1 == toMove.getY() ))));
            assertTrue(
                    ((ix.getY()-1 == toMove.getY() || ix.getY()+1 == toMove.getY())
                            || (ix.getY() == toMove.getY() && (ix.getX()+1 == toMove.getX() || ix.getX()-1 == toMove.getX() ))));
        }
    }



}
