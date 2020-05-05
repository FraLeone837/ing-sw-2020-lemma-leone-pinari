package Controller.Communication;

import Controller.MatchManager;

import java.util.ArrayList;

/**
 * Class needed so we could always go back to the original matchManager
 */

public class IntermediaryClass {
    private MatchManager matchManager;
    private final Object lock = new Object();
    private ArrayList<ClientHandler> clientHandlerArrayList = new ArrayList<>();
    private boolean notified;
    private ArrayList<CommunicationProxy> communicationProxies;

    public IntermediaryClass(){
        notified = false;
        this.communicationProxies = null;
        matchManager = new MatchManager(1,this);
    }



    /**
     * used to communicate with all 2/3 players of a game
     * @param msg
     */
    public void Broadcast(Message msg){
        for(ClientHandler cl : clientHandlerArrayList) {
            cl.sendMessage(msg.getType(), msg);
        }
    }
    /**
     *
     * @param communicationProxy
     */
    public void setCommunicationProxy(CommunicationProxy communicationProxy) {
        synchronized (lock){
            this.communicationProxies.add(communicationProxy);
            notified = true;
            notifyAll();
        }
    }

    public MatchManager getMatchManager() {
        return matchManager;
    }

    /**
     * Helps in asking the first player to be connected, how many players want to play
     * @return
     */
    public boolean isAnyPlayerConnected(){
        return matchManager.isAnyPlayerConnected();
    }

    /**
     *
     * @returns the communication proxy from which to call
     * and send messages to the client
     */
    public CommunicationProxy getNewCommunicationProxy(){
        synchronized (lock){
            while(!notified){
                try{
                    lock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            //if there are no more clients to get we need to wait
            if(communicationProxies.size() == 0)
            notified = false;
        }
        return communicationProxies.get(0);
    }
}
