package ControllerTest;

import Controller.Communication.Message;
import Controller.Gods.Apollo;
import Controller.Gods.God;
import Controller.PlayerManager;
import ControllerTest.UtilClasses.BotPlayer;
import ControllerTest.UtilClasses.GameCreator;
import ControllerTest.UtilClasses.Utils;
import Model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class GodTest implements TestGod {
    public Match match;
    public GameCreator game;
    public  BotPlayer playerOne;
    public BotPlayer playerTwo;
    public PlayerManager playerManagerOne;
    public PlayerManager playerManagerTwo;
    public Message message = new Message(Message.MessageType.ZZZ,"...");
    public boolean askingInput;

    int chosenGod;
    int cellWhereToMove;
    int cellWhereToBuild;

    int WORKER_ONE = 1;
    //worker default
    public int workerId = 1;

    /**
     * creates game, connection to game, and saves match, references to the two players managers (playerManagerOne/Two)
     * and the two players connections (playerOne/Two)
     * default to move is c3 and build b2
     */
    @org.junit.Before
    public void setUp(){
        this.askingInput = false;
        this.game = new GameCreator(chosenGod);
        this.match = game.startGame();
        this.playerOne = game.getPlayerOne();
        this.playerTwo = game.getPlayerTwo();
        this.playerOne.setTestGod(this);
        this.playerTwo.setTestGod(this);
        this.playerManagerOne = game.getFirstPlayer();
        this.playerManagerTwo = game.getSecondPlayer();
        cellWhereToMove = game.CELL_C3;
        cellWhereToBuild = game.CELL_B2;
    }

    @org.junit.After
    public void tearDown(){
        System.out.println("Starting tear down");
        synchronized (this){
            try{
                wait(1500);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        playerTwo.stop();
    }

    public synchronized void waitForTurnStart() {
        String name = game.getFirstName();
        if(this.message.getObject().equals(name))
            this.message = new Message(Message.MessageType.ZZZ, "Wait...");
        while(this.message.getType() != Message.MessageType.TURN_START){
            try{
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("Woke up to message " + message.getObject());
            // se e' l'inizio del turno del primo giocatore, allora continuo ad aspettare
            if(message.getObject().equals(name)){
                this.message = new Message(Message.MessageType.ZZZ, "Wait...");
            }
        }


    }

    //
    @Override
    public synchronized void notifyMessage(BotPlayer player) {
        Message temp = player.getLastMessage();
        //discards island info but asks to keep a copy of the match
        if(temp.getType() != Message.MessageType.ISLAND_INFO)
            this.message = temp;
        else{
            this.match = game.matchManager().getMatch();
        }
        notifyAll();

    }

    /**
     * controls if message is asking for movement.
     * if yes gives cells to move. Moves only on cellWhereToMove and builds on cellWhereToBuild
     */
    public synchronized void controller() {
        playerOne.addInput(workerId);
        playerOne.addInput(cellWhereToMove);
        playerOne.addInput(cellWhereToBuild);
    }
}

//    private Match match;
//    private Worker myWorker;
//    private Worker enemyWorker;
//    private God god;
//
//    @Before
//    public void setUp(){
//        Utils utils = new Utils();
//        Index ix = utils.generateRandomIndex();
//        this.god = utils.generateRandomGod();
//
//        System.out.println(god.getDescription());
//        System.out.println(god.getName());
//
//        this.myWorker = new Worker();
//        this.enemyWorker = new Worker();
//
//        match = new Match(1);
//        match.initWorker(myWorker, ix);
//
//        god.setup(match, new Player("x",3));
//
//        Index index = utils.getPseudoAdjacent(myWorker);
//        match.initWorker(this.enemyWorker,index);
//    }
//
//    @After
//    public void tearDown(){
//        match = null;
//    }
//
//    @Test
//    public void whereToMoveTest(){
//        ArrayList<Index> listWhereToMove = god.whereToMove(match,myWorker,myWorker.getPosition());
//        for(Index ix : listWhereToMove){
//            assertTrue(ix.getZ()>=0 && ix.getZ()<4
//                        && ix.getX()>=0 && ix.getX()<5
//                        && ix.getY()>=0 && ix.getY()<5);
//        }
//
//        for(Index ix : listWhereToMove){
//            if(!god.equals(new Apollo()))
//            assertFalse(ix.equals(myWorker.getPosition()));
//            assertTrue((match.selectCell(ix).getWorker() == null));
//            assertTrue(ix.getZ() <= 1+myWorker.getPosition().getZ());
//            assertTrue(((ix.getX()-1 == myWorker.getPosition().getX() || ix.getX()+1 == myWorker.getPosition().getX())
//                    || (ix.getX() == myWorker.getPosition().getX() && (ix.getY()+1 == myWorker.getPosition().getY() || ix.getY()-1 == myWorker.getPosition().getY() ))));
//            assertTrue(
//                    ((ix.getY()-1 == myWorker.getPosition().getY() || ix.getY()+1 == myWorker.getPosition().getY())
//                            || (ix.getY() == myWorker.getPosition().getY() && (ix.getX()+1 == myWorker.getPosition().getX() || ix.getX()-1 == myWorker.getPosition().getX() ))));
//        }
//    }
//
//    @Test
//    public void whereToBuild(){
//        Utils utils =new Utils();
//        Index toMove;
//        do{
//            toMove = utils.getPseudoAdjacent(myWorker);
//
//        } while(toMove.getZ() != myWorker.getPosition().getZ());
//        ArrayList<Index> listWhereToBuild = god.whereToBuild(match,myWorker,toMove);
//        for(Index ix : listWhereToBuild){
//            assertTrue(ix.getZ()>=0 && ix.getZ()<4
//                    && ix.getX()>=0 && ix.getX()<5
//                    && ix.getY()>=0 && ix.getY()<5);
//        }
//
//        for(Index ix : listWhereToBuild){
//
//            assertFalse(toMove.equals(ix));
//            assertTrue(((ix.getX()-1 == toMove.getX() || ix.getX()+1 == toMove.getX())
//                            || (ix.getX() == toMove.getX() && (ix.getY()+1 == toMove.getY() || ix.getY()-1 == toMove.getY() ))));
//            assertTrue(
//                    ((ix.getY()-1 == toMove.getY() || ix.getY()+1 == toMove.getY())
//                            || (ix.getY() == toMove.getY() && (ix.getX()+1 == toMove.getX() || ix.getX()-1 == toMove.getX() ))));
//        }
//    }
//
//    @Test
//    public void checkWin(){
//        Index oldPosition = myWorker.getPosition();
//        Index nextPosition;
//        Utils utils = new Utils();
//        do{
//            nextPosition = utils.getPseudoAdjacent(myWorker);
//        } while(nextPosition.equals(enemyWorker));
//
//        god.turn(match, myWorker, nextPosition, utils.getPseudoAdjacent(nextPosition));
//
//        if(oldPosition.getZ() == 2 && nextPosition.getZ() == 3){
//            assertTrue(god.checkWin(match,myWorker));
//        }
//        else assertFalse(god.checkWin(match,myWorker));
//    }