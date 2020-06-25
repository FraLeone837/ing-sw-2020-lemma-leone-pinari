package Controller.Communication;

import Controller.MatchManager;
import Model.Match;

import java.util.ArrayList;

import static Controller.Communication.ClientHandler.*;

/**
 * Class needed so we could always go back to the original matchManager
 */
public class IntermediaryClass {
    private MatchManager matchManager;
    private ArrayList<ClientHandler> clientHandlerArrayList = new ArrayList<>();
    //if there are new players online
    private boolean notified;
    //how many players we have given to matchManager
    private int counter = 0;
    private int maxPlayers = 2;

    // Mm means MatchManager
    private Thread threadOfMm;
    private ArrayList<CommunicationProxy> communicationProxies = new ArrayList<>();
    private ArrayList<CommunicationProxy> unusedProxies = new ArrayList<>();
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

    public synchronized void changeNoOfPlayers(){
        maxPlayers = 3;
        if(unusedProxies.size() > 0){
            setCommunicationProxy(unusedProxies.remove(0));
        }
    }

    public MatchManager getMatchManager() {
        return matchManager;
    }

    /**
     * method that finishes game and clears all threads after a 10 second period?
     */
    public synchronized void terminateGame(){
        System.out.println("Start method terminate game");
        Broadcast(new Message(Message.MessageType.END_GAME, "Player has disconnected"));
        counter = 0;
        this.maxPlayers = 2;
        this.notified = false;
        threadOfMm.stop();

        System.out.println("Stopped match manager");
        this.notified = false;
        communicationProxies = new ArrayList<>();
        clientHandlerArrayList = new ArrayList<>();
        for(CommunicationProxy communicationProxy : unusedProxies){
            this.setCommunicationProxy(communicationProxy);
        }
        unusedProxies = new ArrayList<>();
        System.out.println(ANSI_CYAN + "FINISH method TERMINATE GAME" + ANSI_RESET);
        this.matchManager = new MatchManager(1, this);
        threadOfMm = new Thread(matchManager);
        threadOfMm.start();
    }
//    /**
//     * method that finishes game and clears all threads after a 10 second period?
//     */
//    public synchronized void terminateGame(ClientHandler clientHandler){
//        for(CommunicationProxy cp : communicationProxies){
//
//        }
//    }


    /**
     * used to communicate with all 2/3 players of a game
     * @param msg
     */
    public synchronized void Broadcast(Message msg){
        System.out.println("Broadcasting " + msg.getType());
        for(CommunicationProxy cp : communicationProxies) {
            if(cp != matchManager.getDisconnectedProxy())
                cp.sendMessage(msg.getType(), msg.getObject());
        }
    }

    /**
     * adds a new comm proxy for matchManager to use
     * @param communicationProxy
     */
    public void setCommunicationProxy(CommunicationProxy communicationProxy) {

        synchronized (this){
            // do not accept more than maxPlayers clients
            if(maxPlayers > communicationProxies.size()){
                this.communicationProxies.add(communicationProxy);
                setClientHandlers(communicationProxy.getClientHandler());
            } else {
                this.unusedProxies.add(communicationProxy);
            }
            notified = true;
            notifyAll();
        }
    }

    /**
     * removes a comm proxy. Called only by matchManager
     * @param communicationProxy
     */
    public void removeCommunicationProxy(CommunicationProxy communicationProxy) {
        synchronized (this){
            // do not accept more than maxPlayers clients
            if(maxPlayers > communicationProxies.size()){
                this.communicationProxies.add(communicationProxy);
                setClientHandlers(communicationProxy.getClientHandler());
            } else {
                this.unusedProxies.add(communicationProxy);
            }
            try{
                communicationProxies.remove(communicationProxy);
                removeClientHandlers(communicationProxy);
//                matchManager has only two players
                this.counter--;
            } catch (NullPointerException e){
                e.printStackTrace();
                System.out.println("Communication proxy not found");
            }
        }
    }

    private void removeClientHandlers(CommunicationProxy communicationProxy) {
        clientHandlerArrayList.remove(communicationProxy.getClientHandler());
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
            this.counter = this.counter+1;
            communicationProxies.get(counter-1).getClientHandler().setName(Integer.toString(counter));
            if(counter == communicationProxies.size())
                    notified = false;
            return communicationProxies.get(counter-1);
        }
    }

    public synchronized void Broadcast(Message.MessageType messageType, String cause, ClientHandler clientHandler) {
        System.out.println(ANSI_RED + "aboutta broadcast " + cause +ANSI_RESET);
        this.clCause = clientHandler;
        for(ClientHandler clh : clientHandlerArrayList) {
            //notify every other player except for the one that caused the error
            if(clh != clientHandler)
                clh.getCommProxy().sendMessage(messageType,cause);
        }
    }

    public Match getMatch() {
        return MatchManager.getMatch();
    }
}