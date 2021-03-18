package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class Lemma extends God {

    @Override
    public String getName() {
        return "Lemma";
    }

    @Override
    public String getDescription() {
        return "Goddess of Goddesses\n" +
                "Your Turn:\n" +
                "If your worker goes down \n" +
                "you skip the next turn.\n";
    }

    private boolean wentDown;

    public void setWentDown(boolean wentDown){this.wentDown=wentDown;}

    @Override
    public void turn(Match match, CommunicationProxy communicationProxy, Worker worker){
        if(wentDown) {
            setWentDown(false);
            return;
        }
        super.turn(match, communicationProxy, worker);
    }

    @Override
    public void manageMove(Match match, CommunicationProxy communicationProxy, Worker worker, ArrayList<Index> possibleMove){
        super.manageMove(match, communicationProxy, worker, possibleMove);
        if(worker.getPosition().getZ() < prevIndex.getZ())
            setWentDown(true);
    }

    @Override
    public void setup(Match match, Player player) {
        setWentDown(false);
    }
}
