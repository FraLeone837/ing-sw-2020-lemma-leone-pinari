package ControllerTest;

import Controller.Communication.Message;
import ControllerTest.UtilClasses.BotPlayer;
import ControllerTest.UtilClasses.GameCreator;
import Model.Index;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class HestiaTest extends GodTest {

    Message whereToBuild;
    int counter = 1;
    int secondBuildIndex;
    String yesOrNo;
    @Override
    public void setUp() {
        chosenGod = GameCreator.Hestia;
        super.setUp();
    }
    @Test
    public void testTurn_normalMove(){
        yesOrNo = "no";
        cellWhereToMove = game.CELL_C3;
        controller();
        waitForTurnStart();

        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
    }
    @Test
    public void testTurn_perimeterMove_noPerimeterBuild(){
        yesOrNo = "yes";
        cellWhereToMove = game.CELL_B4;
        controller();

        waitForTurnStart();
        assertEquals(match.selectCell(new Index(cellWhereToMove)).getWorker().getIdWorker(),
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(match.selectCell(new Index(cellWhereToBuild)).isBuilding());
        assertTrue(match.selectCell(new Index(secondBuildIndex)).isBuilding());
        assertFalse(isIn());
    }

    private Boolean isIn() {
        ArrayList<Double> positions = (ArrayList<Double>)this.whereToBuild.getObject();
        Index possiblePosition;
        for(Double position : positions){
            possiblePosition = new Index(position.intValue());
            if(possiblePosition.equals(new Index(game.CELL_B5)) ||
                    possiblePosition.equals(new Index(game.CELL_A3))||
                    possiblePosition.equals(new Index(game.CELL_A4))||
                    possiblePosition.equals(new Index(game.CELL_A5))){
                return true;
            }
        }
        return false;
    }


    @Override
    public synchronized void controller() {
        playerOne.addInput(yesOrNo);
        super.controller();
        secondBuildIndex = game.CELL_B2;
        playerOne.addInput(secondBuildIndex);
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
        if (temp.getType() == Message.MessageType.BUILD_INDEX_REQ) {
            if(counter>0){
                System.out.println("One decrease");
                counter--;
            }
            else
                this.whereToBuild = temp;
        }

    }
}
