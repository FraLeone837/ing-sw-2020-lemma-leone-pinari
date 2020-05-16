package Controller;

import Controller.Communication.ClientHandler;
import Controller.Communication.CommunicationProxy;
import Controller.Communication.IntermediaryClass;
import Controller.Communication.Message;
import Model.*;

import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Random;

public class MatchManager implements Runnable{

    private ArrayList<PlayerManager> playerManagers = new ArrayList<>();
    private Match match;
    private IntermediaryClass intermediaryClass;
    private ArrayList<CommunicationProxy> communicationProxies = new ArrayList<>();
    private Boolean matchInProgress;

    /**
     * starts with only the first player who is also the creator of the match
     * @param id is id of match used for multiple matches
     * @param intermediaryClass is the class that connects all two/three clientHandlers together
     *                          and helps with implementation of certain important methods
     */
    public MatchManager(int id, IntermediaryClass intermediaryClass){
        this.intermediaryClass = intermediaryClass;
        this.match = new Match(id);
        this.matchInProgress = true;
    }

    @Override
    public void run() {
        setupPlayers();
        setupGame();
        while(matchInProgress) {
            turn();
        }
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
            ne.printStackTrace();
        }
        return false;
    }


    /**
     * connect the players (2 or 3) and give a god to each of them
     */
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
        giveGods();
    }

    /**
     * each player puts his own invisible blocks on the game board if his god foresees it, and sets his workers
     */
    public void setupGame(){
        ArrayList<Index> possiblePosition = new ArrayList<Index>();
        for (int x=0; x<5; x++){
            for (int y=0; y<5; y++){
                Index index = new Index(x, y, 0);
                possiblePosition.add(index);
            }
        }
        for(PlayerManager playerManager : playerManagers){
            playerManager.setup(match);
            CommunicationProxy CP = playerManager.getCommunicationProxy();
            Index position1 = (Index)CP.sendMessage(Message.MessageType.CHOOSE_INDEX_FIRST_WORKER, possiblePosition);
            Index correctPosition1 = playerManager.getGod().correctIndex(match, position1);
            possiblePosition.remove(correctPosition1);
            Index position2 = (Index)CP.sendMessage(Message.MessageType.CHOOSE_INDEX_SEC_WORKER, possiblePosition);
            Index correctPosition2 = playerManager.getGod().correctIndex(match, position2);
            possiblePosition.remove(correctPosition2);
        }
    }

    /**
     * each player moves a worker and builds. If he cannot do this with none of his workers he loses,
     * if he is the last remained player, he wins
     */
    public void turn(){
        for(PlayerManager playerManager : playerManagers){
            if(playerManagers.size()==1){
                giveVictory(playerManager);
                matchInProgress = false;
                return;
            }
            playerManager.turn(match);
            if(playerManager.getGod().getWinner()==true){
                giveVictory(playerManager);
                matchInProgress = false;
                return;
            }
            if(playerManager.getGod().getInGame()==false){
                playerManager.getCommunicationProxy().sendMessage(Message.MessageType.PLAYER_LOST, "YOU LOST!");
                match.removeWorker(playerManager.getPlayer().getWorker1());
                match.removeWorker(playerManager.getPlayer().getWorker2());
                match.removePlayer(playerManager.getPlayer());
                playerManagers.remove(playerManager);
            }
        }
    }

    /**
     * give randomly a god to each player
     */
    public void giveGods(){
        int numberOfGods = 8;
        ArrayList<Integer> given = new ArrayList<Integer>();
        Random godGen = new Random();
        God god = new Apollo();
        for(PlayerManager playerManager : playerManagers){
            int godCode = godGen.nextInt(numberOfGods);
            while(given.contains(godCode)){
                godCode = godGen.nextInt(numberOfGods);
            }
            given.add(godCode);
            switch (godCode){
                case 0:
                    god = new Apollo();
                    break;
                case 1:
                    god = new Artemis();
                    break;
                case 2:
                    god = new Athena();
                    break;
                case 3:
                    god = new Atlas();
                    break;
                case 4:
                    god = new Demeter();
                    break;
                case 5:
                    god = new Hephaestus();
                    break;
                case 6:
                    god = new Pan();
                    break;
                case 7:
                    god = new Prometheus();
                    break;
            }
            playerManager.setGod(god);
            CommunicationProxy CP = playerManager.getCommunicationProxy();
            CP.sendMessage(Message.MessageType.YOUR_GOD, CP.godDescription(god));
            CP.sendMessage(Message.MessageType.GAME_START, playerManager.getPlayer().getIdPlayer());
        }
    }

    public void giveVictory(PlayerManager playerManager){
        playerManager.getCommunicationProxy().sendMessage(Message.MessageType.PLAYER_WON, "YOU WON!");
        playerManagers.remove(playerManager);
        for (PlayerManager playerManager1 : playerManagers){
            playerManager1.getCommunicationProxy().sendMessage(Message.MessageType.PLAYER_LOST, "YOU LOST!");
            playerManagers.remove(playerManager1);
        }
    }
}
