package ControllerTest;

import Controller.Communication.Message;
import Controller.Gods.Apollo;
import ControllerTest.UtilClasses.GameCreator;
import ControllerTest.UtilClasses.Utils;
import Model.*;
import View.Communication.ServerAdapter;
import org.junit.Test;

import java.util.ArrayList;


import static Controller.Communication.ClientHandler.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class ApolloTest {


    private Match match;
    private GameCreator game;
    private ServerAdapter playerOne;
    private ServerAdapter playerTwo;

    int cellWhereToMove;
    int cellWhereToBuild;

    int WORKER_ONE = 1;
    //build default
    int CELL_D4 = 18; //3,3,0
    //move default
    int CELL_C3 = 12; //2,2,0

    int CELL_B2 = 6; //1,1,0

    @org.junit.Before
    public void setUp(){
        System.out.println(ANSI_CYAN + "Set up" + ANSI_RESET);
        game = GameCreator.getGameCreator();
        match = game.startGame();
        playerOne = game.getPlayerOne();
        playerTwo = game.getPlayerTwo();
        game.getFirstPlayer().setGod(new Apollo());
        System.out.println(ANSI_CYAN + "Calling initialize workers" + ANSI_RESET);
        synchronized (this){
            try{
                wait(200);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        game.initializeWorkers(1);
        game.initializeWorkers(1);
        game.initializeWorkers(2);
        game.initializeWorkers(2);
        cellWhereToMove = CELL_D4;
        cellWhereToBuild = CELL_C3;

    }

    @org.junit.After
    public void tearDown(){
        match = null;
        game = null;
        playerOne.stop();
        playerTwo.stop();
        playerOne = null;
        playerTwo = null;

    }
    @org.junit.Test
    public void testTurn_SwitchPlaces_ExpectedSwitchedPlaces(){
        controller();

        assertTrue(match.selectCell(new Index(2,2,0)).isBuilding());
        assertTrue(
                match.selectCell(new Index(3,3,0)).getWorker().getIdWorker()
                        ==
                        game.getFirstPlayer().getPlayer().getWorker1().getIdWorker());
        assertTrue(
                match.selectCell(new Index(3,2,0)).getWorker().getIdWorker()
                        ==
                        game.getSecondPlayer().getPlayer().getWorker1().getIdWorker());


    }


    @org.junit.Test
    public void testTurn_NormalMovement_CorrectOutput(){

        Message message = game.getLastMessage(1);
        Message.MessageType mt = message.getType();
        cellWhereToMove = CELL_C3;
        cellWhereToBuild = CELL_B2;

        controller();


        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker()
                ,
                game.getFirstPlayer().getPlayer().getWorker1().getIdWorker());

        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());

    }


    /**
     * controls if message is asking for movement.
     * if yes gives cells to move. Moves only on cellWhereToMove and builds on cellWhereToBuild
     */
    public void controller() {
        Message message = game.getLastMessage(1);
        Message.MessageType mt = message.getType();

        while (game.isMovement(message)){
            System.out.println(ANSI_YELLOW + game.getLastMessage(1) + ANSI_RESET);
            switch (mt) {
                case MOVEMENT:
                    playerOne.requestSending(new Message(mt, WORKER_ONE));
                    break;
                case MOVE_INDEX_REQ:
                    playerOne.requestSending(new Message(mt, CELL_D4));
                    break;
                case BUILD_INDEX_REQ:
                    playerOne.requestSending(new Message(mt, CELL_C3));
                    return;
            }
            message = game.getLastMessage(1);

        }
    }


    //    @Test
    //    public void whereToMoveTest(){
    //        /*
    //        ArrayList<Index> listWhereToMove = apollo.whereToMove(match,myWorker,myWorker.getPosition());
    //        for(Index ix : listWhereToMove){
    //            assertTrue(ix.getZ()>=0 && ix.getZ()<4
    //                    && ix.getX()>=0 && ix.getX()<5
    //                    && ix.getY()>=0 && ix.getY()<5);
    //        }
    //
    //        for(Index ix : listWhereToMove){
    //            assertTrue((match.selectCell(ix).getWorker() != myWorker ));
    //            assertTrue(ix.getZ() <= myWorker.getPosition().getZ());
    //            assertTrue(((ix.getX()-1 == myWorker.getPosition().getX() || ix.getX()+1 == myWorker.getPosition().getX())
    //                    || (ix.getX() == myWorker.getPosition().getX() && (ix.getY()+1 == myWorker.getPosition().getY() || ix.getY()-1 == myWorker.getPosition().getY() ))));
    //            assertTrue(
    //                    ((ix.getY()-1 == myWorker.getPosition().getY() || ix.getY()+1 == myWorker.getPosition().getY())
    //                            || (ix.getY() == myWorker.getPosition().getY() && (ix.getX()+1 == myWorker.getPosition().getX() || ix.getX()-1 == myWorker.getPosition().getX() ))));
    //        }
    //        */
//    }
//
//    @Test
//    public void whereToBuildTest(){
//        /*
//        ArrayList<Index> listWhereToMove = apollo.whereToBuild(match,myWorker,toMove);
//        for(Index ix : listWhereToMove){
//            assertTrue(ix.getZ()>=0 && ix.getZ()<4
//                    && ix.getX()>=0 && ix.getX()<5
//                    && ix.getY()>=0 && ix.getY()<5);
//        }
//
//        for(Index ix : listWhereToMove){
//
//            assertFalse(toMove.equals(ix));
//            assertTrue(ix.getZ() <= myWorker.getPosition().getZ());
//            assertTrue(((ix.getX()-1 == toMove.getX() || ix.getX()+1 == toMove.getX())
//                    || (ix.getX() == toMove.getX() && (ix.getY()+1 == toMove.getY() || ix.getY()-1 == toMove.getY() ))));
//            assertTrue(
//                    ((ix.getY()-1 == toMove.getY() || ix.getY()+1 == toMove.getY())
//                            || (ix.getY() == toMove.getY() && (ix.getX()+1 == toMove.getX() || ix.getX()-1 == toMove.getX() ))));
//        }
//         */
//    }

}
