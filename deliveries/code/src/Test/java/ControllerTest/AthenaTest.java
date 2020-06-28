package ControllerTest;

import Controller.Communication.Message;
import Controller.Gods.Apollo;
import Controller.Gods.Athena;
import Controller.PlayerManager;
import ControllerTest.UtilClasses.BotPlayer;
import ControllerTest.UtilClasses.GameCreator;
import ControllerTest.UtilClasses.Utils;
import Model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class AthenaTest extends GodTest implements TestGod {

    public int counter = 1;
    public int enemyMove;
    public Message whereToMove = null;
    public int enemyWorkerId = 3;

    @Before
    public void setUp() {
        chosenGod = GameCreator.Athena;
        super.setUp();
        // puts building where enemy will move next turn
        enemyMove = game.CELL_D3;
        match.selectCell(new Index(enemyMove)).setBuilding();


    }


    @Test
    public void testTurn_NoUp_NoForbiddensExist() {
        boolean isIndexInList = false;
        play();
        assertEquals(match.selectCell(new Index(cellWhereToMove%5,cellWhereToMove/5,0)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        isIndexInList = checkIsInside();
        assertTrue(isIndexInList);

    }

    /**
     * checks if enemyMove (cell) is a possible move
     * @return
     */
    private boolean checkIsInside() {
        ArrayList<Double> positions = (ArrayList<Double>)this.whereToMove.getObject();

        Index possiblePosition;
        for(Double position : positions){
            possiblePosition = new Index(position.intValue());
            if(possiblePosition.equals(new Index(enemyMove))){
                return true;
            }
        }
        return false;
    }

    private void play(){
        controller();
        waitForTurnStart();
        waitForMoveIndexReq();

    }
    @Test
    public void testTurn_GoUp_ForbiddensExist() {
        match.selectCell(new Index(cellWhereToMove)).setBuilding();
        boolean isIndexInList;
        play();
        assertEquals(match.selectCell(new Index(cellWhereToMove%5,cellWhereToMove/5,1)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        isIndexInList = checkIsInside();
        assertFalse(isIndexInList);



    }

    private synchronized void waitForMoveIndexReq() {
        while(this.whereToMove == null){
            try{
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void controller() {
        super.controller();
        playerTwo.addInput(enemyWorkerId);
    }



    @Override
    public synchronized void notifyMessage(BotPlayer player) {

        Message temp = player.getLastMessage();
        //discards island info but asks to keep a copy of the match
        if (temp.getType() != Message.MessageType.ISLAND_INFO)
            this.message = temp;
        else {
            this.match = game.matchManager().getMatch();
        }
        notifyAll();
        //accepts only the second move_index_request which will be used to check if the cell where is one level above is in the list of move_index_req
        if (temp.getType() == Message.MessageType.MOVE_INDEX_REQ) {
            if (counter > 0) {
                counter--;
            } else {
                this.whereToMove = temp;
            }
        }

    }
}
