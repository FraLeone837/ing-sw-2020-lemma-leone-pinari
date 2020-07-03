package ControllerTest;


import Controller.Communication.Message;
import ControllerTest.UtilClasses.BotPlayer;
import ControllerTest.UtilClasses.GameCreator;
import Model.Index;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PrometheusTest extends GodTest{

    String yesOrNo;
    Message whereToMove;
    int secondCellBuild;

    @Override
    public void setUp() {
        chosenGod = GameCreator.Prometheus;
        super.setUp();
        secondCellBuild = game.CELL_D2;
    }

    @Test
    public void testTurn_normalMove(){
        yesOrNo = "no";
        controller();
        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
    }

    @Test
    public void testTurn_usePower_BuildTwice(){
        yesOrNo = "yes";
        match.selectCell(new Index(game.CELL_B3)).setBuilding();
        controller();
        waitForTurnStart();
//        waitForMoveIndexReq();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        assertFalse(checkIsInside());
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
            if(possiblePosition.equals(new Index(game.CELL_B3))){
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized void controller() {
        playerOne.addInput(yesOrNo);
        if(yesOrNo.equals("no")){
            super.controller();
        }
        else{
            playerOne.addInput(workerId);
            playerOne.addInput(cellWhereToBuild);
            playerOne.addInput(cellWhereToMove);
            playerOne.addInput(secondCellBuild);
        }
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
                this.whereToMove = temp;
        }

    }
}
