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

public class ApolloTest extends GodTest implements TestGod{

    @org.junit.Before
    @Override
    public void setUp(){
        this.chosenGod = GameCreator.Apollo;
        super.setUp();
    }


    @org.junit.Test
    public void testTurn_SwitchPlaces_ExpectedSwitchedPlaces(){
        cellWhereToMove = game.CELL_D4;
        cellWhereToBuild = game.CELL_C3;
        Cell cellWhereWas = match.selectCell(playerManagerOne.getPlayer().getWorker1().getPosition());
        controller();
        waitForTurnStart();
        Cell cellWhereMoved = match.selectCell(new Index(cellWhereToMove));
        Cell cellWhereBuilt = match.selectCell(new Index(cellWhereToBuild));
        assertTrue(cellWhereBuilt.isBuilding());
        assertTrue(
                cellWhereMoved.getWorker().getIdWorker()
                        ==
                        playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(
                cellWhereWas.getWorker().getIdWorker()
                        ==
                        playerManagerTwo.getPlayer().getWorker1().getIdWorker());
        System.out.println("Passed tests?");


    }



    @org.junit.Test
    public void testTurn_NormalMovement_CorrectOutput(){

        controller();
        waitForTurnStart();
        Cell cellWhereMoved = match.selectCell(new Index(cellWhereToMove));
        Cell cellWhereBuilt = match.selectCell(new Index(cellWhereToBuild));
        assertEquals(cellWhereMoved.getWorker().getIdWorker()
                ,
                playerManagerOne.getPlayer().getWorker1().getIdWorker());
        assertTrue(cellWhereBuilt.isBuilding());

    }

    public synchronized void notifyMessage(BotPlayer player){
        super.notifyMessage(player);
    }

}
