package Controller.Communication;

import Controller.MatchManager;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Class needed so we could always go back to the original matchManager
 */

public class IntermediaryClass {
    private MatchManager matchManager;
    private final Object lock = new Object();
    private ArrayList<ClientHandler> clientHandlerArrayList = new ArrayList<>();
    private boolean notified;
    private CommunicationProxy communicationProxy;

    public IntermediaryClass(){
        notified = false;
        this.communicationProxy = null;
        matchManager = new MatchManager(1,this);
    }

    public void setCommunicationProxy(CommunicationProxy communicationProxy) {
        synchronized (lock){
            this.communicationProxy = communicationProxy;
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

    public CommunicationProxy getNewCommunicationProxy(){
        synchronized (lock){
            while(!notified){
                try{
                    lock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        return communicationProxy;
    }
}
