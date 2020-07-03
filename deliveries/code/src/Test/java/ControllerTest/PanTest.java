package ControllerTest;


import Controller.Communication.Message;
import ControllerTest.UtilClasses.BotPlayer;
import ControllerTest.UtilClasses.GameCreator;
import Model.Index;
import Model.Worker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class PanTest extends GodTest {

    @Override
    public void setUp() {
        chosenGod = GameCreator.Pan;
        super.setUp();
    }

    @Test
    public void testTurn_normalMove(){
        super.controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
    }

    @Test
    public void testTurn_moveDownTwoLevels_WinMessageExpected(){
        match.selectCell(new Index(game.CELL_C4)).setBuilding();
        match.selectCell(new Index(game.CELL_C4%5,game.CELL_C4/5,1)).setBuilding();
        controller();
        //plays two turns
        waitForTurnStart();
        waitForTurnStart();
        //means that you win message has arrived if it does not go in deadlock
        waitForYouWinMessage();
        assertTrue(true);
    }

    @Override
    public void controller(){
        int secondWorkerId = 3;
        int enemyMove = game.CELL_E4;
        int enemyBuild = game.CELL_E5;
        playerOne.addInput(workerId);
        playerOne.addInput(game.CELL_C4);
        playerOne.addInput(cellWhereToBuild);
        playerTwo.addInput(secondWorkerId);
        playerTwo.addInput(enemyMove);
        playerTwo.addInput(enemyBuild);
        playerTwo.addInput("no");
        playerOne.addInput(workerId);
        playerOne.addInput(game.CELL_C4);
        playerOne.addInput(cellWhereToBuild);

    }

    private synchronized void waitForYouWinMessage() {
        while(this.message.getType() != Message.MessageType.PLAYER_WON){
            try{
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            System.out.println("Woke up to message " + message.getObject());
        }
    }

    @Override
    public synchronized void notifyMessage(BotPlayer player) {
        Message temp = player.getLastMessage();
        //discards island info but asks to keep a copy of the match
        if(temp.getType() != Message.MessageType.ISLAND_INFO){
            this.message = temp;
        }
        else if(temp.getType() == Message.MessageType.ISLAND_INFO){
            this.match = game.matchManager().getMatch();
        }
        notifyAll();

    }
}
