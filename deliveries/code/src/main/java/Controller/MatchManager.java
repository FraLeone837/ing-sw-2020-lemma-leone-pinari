package Controller;

import Controller.Communication.ClientHandler;
import Controller.Communication.CommunicationProxy;
import Controller.Communication.IntermediaryClass;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class MatchManager implements Runnable{

    private ArrayList<PlayerManager> playerManagers = new ArrayList<>();
    private Match match;
    private IntermediaryClass intermediaryClass;
    private ArrayList<CommunicationProxy> communicationProxies = new ArrayList<>();
//    private ArrayList<ClientHandler> clientHandlers;


    /**             !!TO BE USED!!
     * starts with only the first player who is also the creator of the match
     * @param id is id of match used for multiple matches
     * @param intermediaryClass is the class that connects all two/three clientHandlers together
     *                          and helps with implementation of certain important methods
     */
    public MatchManager(int id, IntermediaryClass intermediaryClass){
        this.intermediaryClass = intermediaryClass;
        this.match = new Match(id);
    }

    @Override
    public void run() {
        /**
         *                  Important
         */
        // call another method after having received the number of players,
        // fuf() that calls intermediaryClass.getNewCommunicationProxy()
        // puts it in position 1 and of communicationProxies and successively
        // creates the new player manager and connects them two together
        // communicationProxy.sendMessage(messageType, object) is to be called
        // and returns an object according to protocol
        /**
         * goes on to wait for the first Player to be connected
         * afterwards you need to connect this communicationProxy (canal of communication)
         * to a playerManager
         */
        setupGame();
        setupPlayers();
        turn();
    }

    /**
     * @return all the information of the game according to a protocol
     */
    public int[][][] getInformationArray() {
        return match.getInformationArray();
    }


    public boolean isAnyPlayerConnected(){

        if(playerManagers.size() == 0) return false;
        try{
            playerManagers.get(0).getPlayer().getName();
            return true;
        } catch (NullPointerException ne){

        }
        return false;
    }

    public void close(){

    }

    public void setupPlayers(){
        ArrayList<String> names = new ArrayList<String>();
        //the first player connects
        CommunicationProxy firstCP = intermediaryClass.getNewCommunicationProxy();
        this.communicationProxies.add(firstCP);
        String playerName = (String)firstCP.sendMessage(Message.MessageType.GET_NAME, "Enter your username: ");
        names.add(playerName);
        PlayerManager firstPlayer = new PlayerManager(new Player(playerName, 1), firstCP);
        playerManagers.add(firstPlayer);
        //ask how many players does he want to play with
        int playersNumber = (int)firstCP.sendMessage(Message.MessageType.NUMBER_PLAYERS, "How many players do you want to play with?");
        for (int x=2; x<=playersNumber; x++){
            CommunicationProxy newCP = intermediaryClass.getNewCommunicationProxy();
            this.communicationProxies.add(newCP);
            playerName = (String)newCP.sendMessage(Message.MessageType.GET_NAME, "Enter your username: ");
            while (names.contains(playerName)) {
                playerName = (String)newCP.sendMessage(Message.MessageType.GET_NAME, "This username already exists. Enter another username: ");
            }
            names.add(playerName);
            PlayerManager newPlayer = new PlayerManager(new Player(playerName, x), newCP);
            playerManagers.add(newPlayer);
        }
        //give gods to the players
    }

    public void setupGame(){
        for(PlayerManager playerManager : playerManagers){
            playerManager.setup(match);
        }
    }

    public void turn(){
        for(PlayerManager playerManager : playerManagers){
            playerManager.turn(match);
        }
    }
}
