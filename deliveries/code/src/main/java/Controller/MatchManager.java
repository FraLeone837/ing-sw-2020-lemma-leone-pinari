package Controller;
import Controller.Communication.ClientHandler;
import Controller.Communication.CommunicationProxy;
import Controller.Communication.IntermediaryClass;
import Controller.Communication.Message;
import Controller.Gods.*;
import Model.*;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class MatchManager implements Runnable{

    private ArrayList<PlayerManager> playerManagers = new ArrayList<>();
    private Match match;
    private IntermediaryClass intermediaryClass;
    private ArrayList<CommunicationProxy> communicationProxies = new ArrayList<>();
    private Boolean matchInProgress;
    //author Etion
    //number used in test phase, so I can choose which god to test Automatically with JUnit
    final int RANDOM_GODS = -1;
    //x is always -1 unless special constructor is used (only in testing phase)
    private int x = RANDOM_GODS;

    /**
     * starts with only the first player who is also the creator of the match
     * @param id is id of match used for multiple matches
     * @param intermediaryClass is the class that connects all two/three clientHandlers together
     *                          and helps with implementation of certain important methods
     */
    public MatchManager(int id, IntermediaryClass intermediaryClass){
        this.intermediaryClass = intermediaryClass;
        match = new Match(id);
        match.setIntermediaryClass(this.intermediaryClass);
        this.matchInProgress = true;
    }
    //testing
    public MatchManager(int id, IntermediaryClass intermediaryClass, int x){
        this.intermediaryClass = intermediaryClass;
        match = new Match(id);
        match.setIntermediaryClass(this.intermediaryClass);
        this.matchInProgress = true;
        this.x = x;
    }

    public Match getMatch() {
        return match;
    }
    @Override
    public void run() {
        setupPlayers(x);
        setupGame();
        while(matchInProgress ){
            turn();
        }
        intermediaryClass.terminateGame();
    }




    /**
     * connect the players (2 or 3) and give a god to each of them
     * @param godToChoose is used for tests, in which if godToChoose is equal to -1
     *                    it will choose a random god (set by default for normal games) otherwise
     *                    we can change it to a value from 0 to 14 to choose one god we decide.
     */
    public void setupPlayers(int godToChoose){
        ArrayList<String> names = new ArrayList<String>();
        //the first player connects
        CommunicationProxy firstCP = intermediaryClass.getNewCommunicationProxy();
        this.communicationProxies.add(firstCP);
        String playerName = (String)firstCP.sendMessage(Message.MessageType.GET_NAME, "Enter your username: ");
        names.add(playerName);
        PlayerManager firstPlayer = new PlayerManager(new Player(playerName, 1), firstCP);
        playerManagers.add(firstPlayer);
        match.initPlayers(firstPlayer.getPlayer());

        //ask how many players does he want to play with
        int playersNumber = (int)firstCP.sendMessage(Message.MessageType.NUMBER_PLAYERS, "How many players do you want to play with?");
        firstCP.sendMessage(Message.MessageType.WAIT_START, "Please wait for the game to start");
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
            match.initPlayers(newPlayer.getPlayer());
            newCP.sendMessage(Message.MessageType.WAIT_START, "Please wait for the game to start");
        }

        //give gods to the players
        //giveGodsTest();
        giveGods(godToChoose);
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
        match.notifyView();
        for(PlayerManager playerManager : playerManagers){
            playerManager.setup(match);
            CommunicationProxy CP = playerManager.getCommunicationProxy();
            intermediaryClass.Broadcast(new Message(Message.MessageType.TURN_START,playerManager.getPlayer().getName()));

            Index position1 = (Index)CP.sendMessage(Message.MessageType.CHOOSE_INDEX_FIRST_WORKER, possiblePosition);
            Index correctPosition1 = playerManager.getGod().correctIndex(match, position1);
            match.initWorker(playerManager.getPlayer().getWorker1(),correctPosition1);
            playerManager.getGod().setPrevIndex(correctPosition1);
            possiblePosition.remove(correctPosition1);

            Index position2 = (Index)CP.sendMessage(Message.MessageType.CHOOSE_INDEX_SEC_WORKER, possiblePosition);
            Index correctPosition2 = playerManager.getGod().correctIndex(match, position2);
            match.initWorker(playerManager.getPlayer().getWorker2(),correctPosition2);
            playerManager.getGod().setPrevIndex(correctPosition2);
            possiblePosition.remove(correctPosition2);
        }
    }

    /**
     * each player moves a worker and builds. If he cannot do this with none of his workers he loses,
     * if he is the last remained player, he wins
     */
    public synchronized void turn(){
        PlayerManager toRemove = null;
        ArrayList<PlayerManager> playerManagersCopy = (ArrayList<PlayerManager>) playerManagers.clone();
        if(playerManagers.size()==1){
            giveVictory(playerManagers.get(0));
            matchInProgress = false;
            return;
        }
        for(PlayerManager playerManager : playerManagers){
            CommunicationProxy thisPlayer = playerManager.getCommunicationProxy();
            intermediaryClass.Broadcast(new Message(Message.MessageType.TURN_START, playerManager.getPlayer().getName()));

            playerManager.turn(match);

            if(playerManager.getGod().getWinner()==true){
                giveVictory(playerManager);
                matchInProgress = false;
                return;
            }
            if(playerManager.getGod().getInGame()==false){
                thisPlayer.sendMessage(Message.MessageType.PLAYER_LOST, "YOU LOST!");
                intermediaryClass.removeCommunicationProxy(thisPlayer);
                intermediaryClass.Broadcast(new Message(Message.MessageType.OTHERS_LOSS, playerManager.getPlayer().getName()));
                if(playerManagers.size()>2) {
                    match.removeWorker(playerManager.getPlayer().getWorker1());
                    match.removeWorker(playerManager.getPlayer().getWorker2());
                    match.removePlayer(playerManager.getPlayer());
                    match.notifyView();
                }
                toRemove = playerManager;
                if(toRemove != null) {
                    playerManagersCopy.remove(toRemove);
                    toRemove = null;
                }
            }
        }
        playerManagers = playerManagersCopy;
    }

    private God godID(int id){
        switch (id){
            case 0:
                return new Apollo();
            case 1:
                return new Artemis();
            case 2:
                return new Athena();
            case 3:
                return new Atlas();
            case 4:
                return new Demeter();
            case 5:
                return new Hephaestus();
            case 6:
                return new Minotaur();
            case 7:
                return new Pan();
            case 8:
                return new Prometheus();
            case 9:
                return new Hera();
            case 10:
                return new Hestia();
            case 11:
                return new Poseidon();
            case 12:
                return new Triton();
            default:
                return new Zeus();
        }
    }
    /**
     * give randomly a god to each player
     */
    public void giveGods(){
        int numberOfGods = 14;
        ArrayList<Integer> given = new ArrayList<Integer>();
        Random godGen = new Random();
        God god = new Apollo();
        for(PlayerManager playerManager : playerManagers){
            int godCode = godGen.nextInt(numberOfGods);
            while(given.contains(godCode)){
                godCode = godGen.nextInt(numberOfGods);
            }
            given.add(godCode);
            god = godID(godCode);
            playerManager.setGod(god);
            CommunicationProxy CP = playerManager.getCommunicationProxy();
            intermediaryClass.Broadcast(new Message(Message.MessageType.YOUR_GOD, CP.godDescription(god, playerManager.getPlayer().getName())));
            CP.sendMessage(Message.MessageType.GAME_START, playerManager.getPlayer().getIdPlayer());
        }
    }

    public void giveGods(int id){
        if(id == RANDOM_GODS){
            giveGods();
            return;
        }
        Random godGen = new Random();
        God god = new Apollo();
        //gives same god to every single player
        for(PlayerManager playerManager : playerManagers){
            int godCode = id;
            god = godID(godCode);
            playerManager.setGod(god);
            CommunicationProxy CP = playerManager.getCommunicationProxy();
            intermediaryClass.Broadcast(new Message(Message.MessageType.YOUR_GOD, CP.godDescription(god, playerManager.getPlayer().getName())));
            CP.sendMessage(Message.MessageType.GAME_START, playerManager.getPlayer().getIdPlayer());
            id++;
        }
    }

    public void giveVictory(PlayerManager playerManager){
        playerManager.getCommunicationProxy().sendMessage(Message.MessageType.PLAYER_WON, "YOU WON!");
        playerManagers.remove(playerManager);
        for (PlayerManager playerManager1 : playerManagers){
            playerManager1.getCommunicationProxy().sendMessage(Message.MessageType.PLAYER_LOST, "YOU LOST!");
        }
        playerManagers.clear();
    }

//    /**
//     * give a god to each player, asking to the server with cli
//     * useful for testing gods
//     */
//    public void giveGodsTest(){
//        Scanner scanner = new Scanner(System.in);
//        God god;
//        String godCode ;
//        int i = 0;
//        for(PlayerManager playerManager : playerManagers){
//            i++;
//            System.out.println("Dio per il " + i + "-o giocatore");
//            godCode = scanner.nextLine();
//            god = godID(Integer.parseInt(godCode));
//            playerManager.setGod(god);
//            CommunicationProxy CP = playerManager.getCommunicationProxy();
//            intermediaryClass.Broadcast(new Message(Message.MessageType.YOUR_GOD, CP.godDescription(god, playerManager.getPlayer().getName())));
//            CP.sendMessage(Message.MessageType.GAME_START, playerManager.getPlayer().getIdPlayer());
//        }
//    }


    public ArrayList<PlayerManager> getPlayerManagers() {
        return playerManagers;
    }
}
