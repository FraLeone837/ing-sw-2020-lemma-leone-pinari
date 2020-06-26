package Controller;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Controller.Gods.God;
import Model.*;

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
        god.setInGame(true);
        god.setWinner(false);
    }


    /**
     * has to be invoked at the beginning of the match in order to set a block Invisible in each cell of the
     * game board, if the god's power foresees that
     *
     * @param match the current match
     */
    public void setup(Match match){
        god.setup(match, player);
        god.setInGame(true);
    }

    /**
     * manages the progress of the turn after you select a worker, according to what god's power makes you able/unable to do
     *
     * @param match the current match
     */
    public void turn(Match match){
        int toSend = 0;
        if(god.canMove(match, player.getWorker1()))
            toSend += 1;
        if(god.canMove(match, player.getWorker2()))
            toSend += 2;
        if(toSend>0) {
            //ask what worker to move
            int IDWorker = (int)communicationProxy.sendMessage(Message.MessageType.MOVEMENT, toSend);
            Worker worker;
            if(IDWorker == 1){
                worker = player.getWorker1();
            } else worker = player.getWorker2();
            god.turn(match, communicationProxy, worker);
        }
        else{
            god.setInGame(false);
        }
    }

    public God getGod() {
        return god;
    }

    public Player getPlayer() {
        return player;
    }

    public CommunicationProxy getCommunicationProxy(){
        return communicationProxy;
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

