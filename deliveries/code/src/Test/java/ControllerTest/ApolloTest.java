package ControllerTest;

import Controller.Communication.Message;
import Controller.Gods.Apollo;
import Controller.PlayerManager;
import ControllerTest.UtilClasses.BotPlayer;
import ControllerTest.UtilClasses.GameCreator;
import ControllerTest.UtilClasses.Utils;
import ControllerTest.UtilClasses.WriterClass;
import Model.*;
import View.Communication.ServerAdapter;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;


import static Controller.Communication.ClientHandler.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class ApolloTest implements TestGod{


    private Match match;
    private GameCreator game;
    private BotPlayer playerOne;
    private BotPlayer playerTwo;
    private Message message = new Message(Message.MessageType.ZZZ,"...");

    private boolean askingInput;

    int cellWhereToMove;
    int cellWhereToBuild;

    int WORKER_ONE = 1;
    //worker default
    private int workerId = 1;
    private int numberTurns = 2;


    @org.junit.Before
    public void setUp(){
        askingInput = false;
        game = new GameCreator(GameCreator.Apollo);
        match = game.startGame();
        playerOne = game.getPlayerOne();
        playerTwo = game.getPlayerTwo();
        playerOne.setTestGod(this);
        playerTwo.setTestGod(this);
    }

    @org.junit.After
    public void tearDown(){
        playerTwo.stop();

    }
    @org.junit.Test
    public void testTurn_SwitchPlaces_ExpectedSwitchedPlaces(){
        System.out.println("test. 1");
        cellWhereToMove = game.CELL_D4;
        cellWhereToBuild = game.CELL_C3;
        controller();
        waitForTurnStart();
        assertTrue(match.selectCell(new Index(game.CELL_C3)).isBuilding());
        assertTrue(
                match.selectCell(new Index(game.CELL_D4)).getWorker().getIdWorker()
                        ==
                        game.getFirstPlayer().getPlayer().getWorker1().getIdWorker());
        assertTrue(
                match.selectCell(new Index(game.CELL_C4)).getWorker().getIdWorker()
                        ==
                        game.getSecondPlayer().getPlayer().getWorker1().getIdWorker());
        System.out.println("Passed tests?");


    }

    private synchronized void waitForTurnStart() {
        String name = game.getFirstName();
        while(this.message.getType() != Message.MessageType.TURN_START){
            try{
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            // se e' l'inizio del turno del primo giocatore, allora continuo ad aspettare
            if(message.getObject().equals(name)){
                this.message = new Message(Message.MessageType.ZZZ, "Wait...");
            }
            if(numberTurns>0 && message.getType() != Message.MessageType.ZZZ){
                this.message = new Message(Message.MessageType.ZZZ, "Wait...");
                numberTurns--;
            }
        }
    }


    @org.junit.Test
    public void testTurn_NormalMovement_CorrectOutput(){
        cellWhereToBuild = game.CELL_B2;
        cellWhereToMove = game.CELL_C3;

        controller();
        waitForTurnStart();

        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker()
                ,
                game.getFirstPlayer().getPlayer().getWorker1().getIdWorker());

        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());

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

    public synchronized void notifyMessage(BotPlayer player){
        this.message = player.getLastMessage();
        notifyAll();
    }

}
