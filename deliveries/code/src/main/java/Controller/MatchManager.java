package Controller;

import Model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MatchManager {

    private ArrayList<PlayerManager> playerManagers;
    private Match match;
    private ArrayList<ClientHandler> clientHandlers;

    /**
     * starts with only the first player who is also the creator of the match
     * @param name is name of first player
     * @param clientHandler is the client of the first player
     */
    public MatchManager(String name, ClientHandler clientHandler){
        this.clientHandlers = new ArrayList<>();
        clientHandlers.add(clientHandler);
        this.playerManagers = new ArrayList<PlayerManager>();
        playerManagers.add(new PlayerManager(new Player(name, 1)));
        this.playerManagers = playerManagers;
        this.match = new Match(1);
    }

    /** Calls method of playerManager to setup workers
     *  based on god chosen
     * @param numberOfPlayers
     */
    public void matchSetUp(int numberOfPlayers){


        //wait for the other player for 30 seconds
        playerManagers.get(1).setup(this.match, createNewPlayer( 2));
        if(numberOfPlayers == 3) {
            playerManagers.get(2).setup(this.match, createNewPlayer( 1));
        }


    }

    /**
     *
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
                    Worker workerToMove = clientHandlers.get
                            (playerManager.getPlayer().getIdPlayer()).
                            chooseWorker(playerManager.getPlayer(),flag)
                            ;
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

    private Player createNewPlayer(int id){
//        stub
//               name = requireName();
        String name = "stub";
        return new Player(name,id);
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
        return clientHandlers.get(playerAsked.getIdPlayer()).chooseWorker(playerAsked,flag);
    }


    /**
     * notifies the player that they lost and flags as to not receive input
     * anymore from that client
     */
    private void playerLost(PlayerManager playerManager) throws NullPointerException{
        ClientHandler findPlayer = null;
        for(int i=0; i<playerManagers.size();i++){
            if(playerManager.equals(playerManagers.get(i))){
                findPlayer = clientHandlers.get(i);
            }
        }
        if(findPlayer == null){
            throw new NullPointerException("Error in playerLost, might be because playerManager is not found");
        }
        findPlayer.sendMessage(Message.MessageType.PLAYER_LOST, false);
    }
}
