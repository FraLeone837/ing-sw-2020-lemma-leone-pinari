package Controller;

import Controller.Communication.ClientHandler;
import Controller.Communication.CommunicationProxy;
import Controller.Communication.IntermediaryClass;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class MatchManager {

    private ArrayList<PlayerManager> playerManagers;
    private Match match;
    private IntermediaryClass intermediaryClass;
    //
    private ArrayList<CommunicationProxy> communicationProxies = new ArrayList<>();
//    private ArrayList<ClientHandler> clientHandlers;

    /**             !!!NOT TO BE USED!!!
     * starts with only the first player who is also the creator of the match
     * @param playerName is name of first player
     * @param clientHandler is the client of the first player
     */
    public MatchManager(String playerName, ClientHandler clientHandler){
//        this.clientHandlers = new ArrayList<>();
//        clientHandlers.add(clientHandler);
        this.playerManagers = new ArrayList<PlayerManager>();
        playerManagers.add(new PlayerManager(new Player(playerName, 1)));
        this.playerManagers = playerManagers;
        this.match = new Match(1);
    }
    /**             !!TO BE USED!!
     * starts with only the first player who is also the creator of the match
     * @param id is id of match used for multiple matches
     * @param intermediaryClass is the class that connects all two/three clientHandlers together
     */
    public MatchManager(int id, IntermediaryClass intermediaryClass){
//        this.clientHandlers = new ArrayList<>();
        this.intermediaryClass = intermediaryClass;
        this.communicationProxies.add(intermediaryClass.getNewCommunicationProxy());
        //create playerManager and connect the first id to theirs


        this.playerManagers = new ArrayList<PlayerManager>();
//        this.playerManagers.add(PlayerManager(communicationProxies.get(0)));
        this.match = new Match(id);


        /**
         *                  Important
         */
        // call another method after having received the number of players,
        // fuf() that calls intermediaryClass.getNewCommunicationProxy()
        // puts it in position 1 and of communicationProxies and successively
        // creates the new player manager and connects them two together
        // communicationProxy.sendMessage(messageType, object) is to be called
        // and returns an object according to protocol
    }

    /**         !!Not To Be USED!!
     * starts a MatchManager from scratch that accepts connections
     *          !!To be deleted!!
     */
    public MatchManager(){
//        this.clientHandlers = new ArrayList<>();
        this.playerManagers = new ArrayList<PlayerManager>();
        this.playerManagers = playerManagers;
        this.match = new Match(1);
    }



    /**
     * method of play
     */
    public void play(){
        while(true){
            for(PlayerManager playerManager : playerManagers ){
                God godsTurn = playerManager.getGod();
                Worker worker1 = playerManager.getPlayer().getWorker1();
                Worker worker2 = playerManager.getPlayer().getWorker2();
                // if flag = 0 you can not move, if flag 1 you can move worker 1, if 2 worker 2, if 3 both workers
                int flag = 0;
                if(godsTurn.canMove(match, worker2)){
                    flag = 2;
                }
                if(godsTurn.canMove(match,worker1)){
                    flag++;
                }
                if(flag > 0){
                    Worker workerToMove = chooseWorker(playerManager.getPlayer(),flag);
                    playerManager.getGod().turn(match,workerToMove);
                }
                else if(flag == 0){
                    playerLost(playerManager);
                }
//                if(playerManager.getGod().canMove(match,workerToMove)){
//
//                }
            }
        }
    }



    /**
     * @return all the information of the game according to a protocol
     */
    public int[][][] getInformationArray() {
        return match.getInformationArray();
    }

    /** Calls clientHandler chooseWorker to communicate with view
     *  on which worker to choose
     * @returns the worker chosen from the player by View
     */
    private Worker chooseWorker(Player playerAsked,int flag){
        //communication proxy.sendMessage( messagetype.ChooseWorker, flag)

        //stub
        return new Worker();
    }


    /**
     * notifies the player that they lost and flags as to not receive input
     * anymore from that client
     */
    private void playerLost(PlayerManager playerManager) throws NullPointerException{
        CommunicationProxy findPlayer = null;
        for(int i=0; i<playerManagers.size();i++){
            if(playerManager.equals(playerManagers.get(i))){
                findPlayer = communicationProxies.get(i);
            }
        }
        if(findPlayer == null){
            throw new NullPointerException("Error in playerLost, might be because playerManager is not found");
        }
        findPlayer.sendMessage(Message.MessageType.PLAYER_LOST, "You have lost");
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
}
