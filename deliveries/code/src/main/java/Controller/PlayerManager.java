package Controller;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class PlayerManager {
    private Player player;
    private God god;
    private CommunicationProxy communicationProxy;

    public PlayerManager(Player player, CommunicationProxy communicationProxy){
        this.player = player;
        this.communicationProxy = communicationProxy;
    }

    public void setGod(God god){
        this.god = god;
    }


    /**
     * has to be invoked at the beginning of the match in order to set a block Invisible in each cell of the
     * game board, if the god's power foresees that
     *
     * @param match the current match
     */
    public void setup(Match match){
        god.setup(match, player);
    }

    /**
     * manages the progress of the turn after you select a worker, according to what god's power makes you able/unable to do
     *
     * @param match the current match
     */
    public void turn(Match match){
        if(god.canMove(match, player.getWorker1()) || god.canMove(match, player.getWorker2())) {
            //ask what worker to move
            Worker worker = (Worker)communicationProxy.sendMessage(Message.MessageType.MOVEMENT, "Choose a worker");
            god.turn(match, communicationProxy, worker);
        }
        else{
            communicationProxy.sendMessage(Message.MessageType.PLAYER_LOST, "YOU LOST!");
            match.removeWorker(player.getWorker1());
            match.removeWorker(player.getWorker2());
        }
    }

    public God getGod() {
        return god;
    }

    public Player getPlayer() {
        return player;
    }



    @Override
    public boolean equals(Object obj) {
        PlayerManager pmg = (PlayerManager) obj;
        Player thisOne = this.getPlayer();
        Player other = pmg.getPlayer();
        if(thisOne.getName() == other.getName()) {
            return true;
        }
        return false;
    }
}

