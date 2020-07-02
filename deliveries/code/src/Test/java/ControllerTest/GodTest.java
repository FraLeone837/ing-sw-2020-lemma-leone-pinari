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
        playerTwo.stop();
    }

    /**
     * waits for the model to update,
     * which happens before the start of the turn of the adversary
     */
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
