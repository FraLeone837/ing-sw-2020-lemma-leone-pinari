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
    private int counter = 0;
    // Mm means MatchManager
    private Thread threadOfMm;
    private ArrayList<CommunicationProxy> communicationProxies = new ArrayList<>();

    public IntermediaryClass(){
        this.notified = false;
        this.matchManager = new MatchManager(1,this);
        this.threadOfMm = new Thread(matchManager);
        this.threadOfMm.start();
    }

    public void setClientHandlers(ClientHandler clientHandler) {
            this.clientHandlerArrayList.add(clientHandler);

    }

    /**
     * method that finishes game and clears all threads after a 10 second period
     */
    public void terminateGame(){
        threadOfMm.stop();
        this.matchManager = new MatchManager(1, this);
        threadOfMm = new Thread(matchManager);
        threadOfMm.start();
        this.notified = false;
        for(ClientHandler cl : clientHandlerArrayList){
            cl.terminateGame();
        }
        communicationProxies = new ArrayList<>();
        clientHandlerArrayList = new ArrayList<>();
    }


    /**
     * used to communicate with all 2/3 players of a game
     * @param msg
     */
    public void Broadcast(Message msg){
        for(CommunicationProxy cl : communicationProxies) {
            cl.sendMessage(msg.getType(), msg);
        }
    }

    /**
     * adds a new comm proxy for matchManager to use
     * @param communicationProxy
     */
    public void setCommunicationProxy(CommunicationProxy communicationProxy) {
        synchronized (lock){
            this.communicationProxies.add(communicationProxy);
            notified = true;
            lock.notifyAll();
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
        notified = false;
        this.counter = this.counter+1;
        return communicationProxies.get(counter-1);
    }
}
