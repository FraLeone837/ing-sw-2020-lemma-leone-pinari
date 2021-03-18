package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Model.*;

import java.util.ArrayList;

public class Leone extends God {

    @Override
    public String getName() {
        return "Leone";
    }

    @Override
    public String getDescription() {
        return "Goddess of Bro\n" +
                "Your Turn:\n" +
                "You cannot build.\n";
    }

    @Override
    public void turn(Match match, CommunicationProxy communicationProxy, Worker worker){
        ArrayList<Index> possibleMove = whereToMove(match, worker, worker.getPosition());
        if(possibleMove.isEmpty()){
            setInGame(false);
            return;
        }
        manageMove(match, communicationProxy, worker, possibleMove);
        if(checkWin(match, worker)){
            setWinner(true);
            return;
        }
    }
}
