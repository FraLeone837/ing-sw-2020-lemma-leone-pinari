package Controller.Communication;

import Controller.MatchManager;

import java.util.ArrayList;

import static Controller.Communication.ClientHandler.*;

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
    private ClientHandler clCause = null;

    public IntermediaryClass(){
        this.notified = false;
        this.matchManager = new MatchManager(1,this);
        this.threadOfMm = new Thread(matchManager);
        this.threadOfMm.start();
    }

    public synchronized void setClientHandlers(ClientHandler clientHandler) {
            this.clientHandlerArrayList.add(clientHandler);

    }

    /**
     * method that finishes game and clears all threads after a 10 second period?
     */
    public void terminateGame(){
        counter = 0;
        threadOfMm.stop();
        this.matchManager = new MatchManager(1, this);
        threadOfMm = new Thread(matchManager);
        threadOfMm.start();
        this.notified = false;
        for(ClientHandler cl : clientHandlerArrayList){
            if(cl != clCause)
            cl.terminateGame();
        }
        communicationProxies = new ArrayList<>();
        clientHandlerArrayList = new ArrayList<>();
        System.out.println(ANSI_CYAN + "FINISH method TERMINATE GAME" + ANSI_RESET);
    }


    /**
     * used to communicate with all 2/3 players of a game
     * @param msg
     */
    public synchronized void Broadcast(Message msg){
        for(CommunicationProxy cp : communicationProxies) {
            cp.sendMessage(msg.getType(), msg.getObject());
        }
    }

    /**
     * adds a new comm proxy for matchManager to use
     * @param communicationProxy
     */
    public void setCommunicationProxy(CommunicationProxy communicationProxy) {
        synchronized (this){
            this.communicationProxies.add(communicationProxy);
            setClientHandlers(communicationProxy.getClientHandler());
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
        synchronized (this){
            while(!notified){
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            notified = false;
            this.counter = this.counter+1;
            communicationProxies.get(counter-1).getClientHandler().setName(Integer.toString(counter));
            return communicationProxies.get(counter-1);
        }
    }

    public synchronized void Broadcast(Message.MessageType messageType, String cause, CommunicationProxy communicationProxy) {
        this.clCause = communicationProxy.getClientHandler();
        for(CommunicationProxy cp : communicationProxies) {
            //notify every other player except for the one that caused the error
            if(cp != communicationProxy)
            cp.sendMessage(messageType,cause);
        }
    }
}
