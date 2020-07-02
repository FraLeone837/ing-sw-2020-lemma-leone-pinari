package Controller.Communication;

import Controller.MatchManager;
import Model.Match;

import java.lang.reflect.Array;
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
    //if somebody has been disconnected
    private boolean disco = false;

    // Mm means MatchManager
    private Thread threadOfMm;
    private ArrayList<CommunicationProxy> communicationProxies = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<CommunicationProxy> unusedProxies = new ArrayList<>();
    private final String disconnectedFlag = "--";
    private String disconnected = disconnectedFlag;

    public IntermediaryClass(){
        this.notified = false;
        this.matchManager = new MatchManager(1,this);
        this.threadOfMm = new Thread(matchManager);
        this.threadOfMm.start();
    }
    //int x is used only to differentiate between testing phase and normal game
    public IntermediaryClass(int x){
        this.notified = false;
        this.matchManager = new MatchManager(1,this,x);
        this.threadOfMm = new Thread(matchManager);
        this.threadOfMm.start();
    }

    private synchronized void setClientHandlers(ClientHandler clientHandler) {
            this.clientHandlerArrayList.add(clientHandler);
    }

    synchronized void changeNoOfPlayers(){
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
        Broadcast(new Message(Message.MessageType.END_GAME, "Player has disconnected"),disconnected);
        counter = 0;
        this.maxPlayers = 2;
        this.notified = false;
        threadOfMm.stop();

        System.out.println("Stopped match manager");
        this.notified = false;
        for(CommunicationProxy com : communicationProxies){
            com.removeIntermediaryClass();
        }
        communicationProxies = new ArrayList<>();
        clientHandlerArrayList = new ArrayList<>();
        ArrayList<CommunicationProxy> tempList = new ArrayList<>();
        for(CommunicationProxy communicationProxy : unusedProxies){
            tempList.add(communicationProxy);
        }
        for(CommunicationProxy communicationProxy :tempList){
            this.setCommunicationProxy(communicationProxy);
        }
        disconnected = disconnectedFlag;
        unusedProxies = new ArrayList<>();
        names = new ArrayList<>();
        System.out.println(ANSI_CYAN + "FINISH method TERMINATE GAME" + ANSI_RESET);
        this.matchManager = new MatchManager(1, this);
        threadOfMm = new Thread(matchManager);
        threadOfMm.start();
    }

    private boolean isDisconnected(CommunicationProxy communicationProxy){
        if(disconnected.equals(communicationProxy.getClientHandler().getName())){
                return true;
            }
        return false;
    }

    /**
     * used to communicate with all 2/3 players of a game
     * only after game has started which means that communication proxies is constant
     * @param msg the message to be sent (no error messages)
     */
    public void Broadcast(Message msg){
        System.out.println("Broadcasting" + msg);
        for(CommunicationProxy cp : communicationProxies) {
            if(!isDisconnected(cp))
            cp.sendMessage(msg.getType(), msg.getObject());
        }
    }

    /**
     * Used to send error messages to different players
     * @param msg message to be communicated to every other communication proxy except the one disconnected
     * @param disconnected is the reason of the game being interrupted (time-out/ disconnected user)
     */
    private void Broadcast(Message msg, String disconnected){
        System.out.println("Broadcasting" + msg);
        for(CommunicationProxy cp : communicationProxies) {
            if(isDisconnected(cp)){
                continue;
            }
            else{
                cp.getClientHandler().setOtherDisconnected(true);
                cp.sendMessage(msg.getType(), msg.getObject(),disconnected);
            }
        }
    }

    public String getDisconnected() {
        return disconnected;
    }

    synchronized void setDisconnected(String disconnected){
        int num = 0;
        int numberToRemove = -1;
        for(num=0; num<names.size();num++){
            if(disconnected.equals(names.get(num))){
                numberToRemove = num;
            }
        }
        if(numberToRemove == -1){
            return;
        }
        disconnected = names.remove(numberToRemove);
    }

    /**
     * adds a new comm proxy for matchManager to use
     * @param communicationProxy the object to be added as new channel for the game to communicate with
     *                           (or to be put into a waiting list so it is first in line to play the next game)
     */
    public synchronized void setCommunicationProxy(CommunicationProxy communicationProxy) {

            // do not accept more than maxPlayers clients
            if(maxPlayers > communicationProxies.size()){
                this.communicationProxies.add(communicationProxy);
                setClientHandlers(communicationProxy.getClientHandler());
                names.add("Player" + communicationProxies.size());
            } else {
                this.unusedProxies.add(communicationProxy);
            }
            notified = true;
            notifyAll();
    }

    /**
     * removes a comm proxy. Called only by matchManager
     * @param communicationProxy the communication proxy to be removed from the list (in case of loss/disconnection)
     */
    public void removeCommunicationProxy(CommunicationProxy communicationProxy) {
        synchronized (this){
            try{
                communicationProxies.remove(communicationProxy);
                removeClientHandlers(communicationProxy);
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
     *
     * @return the communication proxy from which to call
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


    public Match getMatch() {
        return matchManager.getMatch();
    }
}