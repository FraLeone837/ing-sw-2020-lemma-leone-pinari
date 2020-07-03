package ControllerTest;

import Controller.Communication.Message;
import Controller.Gods.Artemis;
import ControllerTest.UtilClasses.BotPlayer;
import ControllerTest.UtilClasses.GameCreator;
import ControllerTest.UtilClasses.Utils;
import Model.Index;
import Model.Match;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ArtemisTest extends GodTest implements TestGod {

    public int secondCellToMove;
    public String yesOrNo;
    public Message whereToMove;
    public int counter = 1;

    /**
     * checks if CELL_C4 is given as possible index to move
     * moves to CELL_C3 and CELL_C2 builds CELL_B2
     */

    @org.junit.Before
    public void setUp(){
        this.chosenGod = GameCreator.Artemis;
        super.setUp();
        secondCellToMove = game.CELL_C2;

    }

    @org.junit.Test
    public void turnTest_MovesTwice_ChecksPosition(){
        yesOrNo = "yes";
        boolean flag = false;
        Index oldPosition = playerManagerOne.getPlayer().getWorker1().getPosition();
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(secondCellToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());

        ArrayList<Double> positions = (ArrayList<Double>)this.whereToMove.getObject();

        Index possiblePosition;
        for(Double position : positions){
            possiblePosition = new Index(position.intValue());
            if(possiblePosition.equals(oldPosition)){
                flag = true;
            }
        }
        assertFalse(flag);
    }
    @org.junit.Test
    public void turnTest_MovesOnce_ChecksPosition(){
        yesOrNo = "no";
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                    playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
    }


    @Override
    public synchronized void controller(){
        playerOne.addInput(workerId);
        playerOne.addInput(cellWhereToMove);
        if(yesOrNo.equals("yes"))
        playerOne.addInput(secondCellToMove);
        playerOne.addInput(cellWhereToBuild);
        playerOne.addInput(yesOrNo);
    }

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
        //accepts only the second move_index_request which will be used to check if the initial cell is in the list of move_index_req
        if(temp.getType() == Message.MessageType.MOVE_INDEX_REQ){
            if(counter>0){
                counter--;
            }
            else{
                this.whereToMove = temp;
            }
        }

    }
}
