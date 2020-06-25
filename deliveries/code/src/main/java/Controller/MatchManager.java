package Controller;
import Controller.Communication.CommunicationProxy;
import Controller.Communication.IntermediaryClass;
import Controller.Communication.Message;
import Controller.Gods.*;
import Model.*;
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
    private boolean disconnected = false;
    private int disconnectedPlayer;

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

    public Match getMatch() {
        return match;
    }
    @Override
    public void run() {
        setupPlayers();
        setupGame();
        while(matchInProgress ){
            turn();
        }
        System.out.println("stacca stacca");
        intermediaryClass.terminateGame();
    }

    /**
     * if any player is disconnected notifies
     * all the other players
     */
    private void notifyDisconnection() {
        if(disconnected){
            for(int i=0; i<communicationProxies.size(); i++){
                if(i != disconnectedPlayer){
                    communicationProxies.get(i).sendMessage(Message.MessageType.END_GAME, "One player disconnected");
                }
            }
        }
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
//        giveGodsTest();
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
            if(disconnected){
                notifyDisconnection();
                return;
            }
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
                    god = new Minotaur();
                    break;
                case 7:
                    god = new Pan();
                    break;
                case 8:
                    god = new Prometheus();
                    break;
                case 9:
                    god = new Hera();
                    break;
                case 10:
                    god = new Hestia();
                    break;
                case 11:
                    god = new Poseidon();
                    break;
                case 12:
                    god = new Triton();
                    break;
                case 13:
                    god = new Zeus();
                    break;
            }
            playerManager.setGod(god);
            CommunicationProxy CP = playerManager.getCommunicationProxy();
            intermediaryClass.Broadcast(new Message(Message.MessageType.YOUR_GOD, CP.godDescription(god, playerManager.getPlayer().getName())));
            CP.sendMessage(Message.MessageType.GAME_START, playerManager.getPlayer().getIdPlayer());
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

    /**
     * give a god to each player, asking to the server with cli
     * useful for testing gods
     */
    public void giveGodsTest(){
        Scanner scanner = new Scanner(System.in);
        God god = new Apollo();
        String godCode = "apollo";
        int i = 0;
        for(PlayerManager playerManager : playerManagers){
            i++;
            System.out.println("Dio per il " + i + "-o giocatore");
            godCode = scanner.nextLine();
            switch (godCode){
                case "apollo":
                    god = new Apollo();
                    break;
                case "artemis":
                    god = new Artemis();
                    break;
                case "athena":
                    god = new Athena();
                    break;
                case "atlas":
                    god = new Atlas();
                    break;
                case "demeter":
                    god = new Demeter();
                    break;
                case "hephaestus":
                    god = new Hephaestus();
                    break;
                case "minotaur":
                    god = new Minotaur();
                    break;
                case "pan":
                    god = new Pan();
                    break;
                case "prometheus":
                    god = new Prometheus();
                    break;
                case "hera":
                    god = new Hera();
                    break;
                case "hestia":
                    god = new Hestia();
                    break;
                case "poseidon":
                    god = new Poseidon();
                    break;
                case "triton":
                    god = new Triton();
                    break;
                case "zeus":
                    god = new Zeus();
                    break;
            }
            playerManager.setGod(god);
            CommunicationProxy CP = playerManager.getCommunicationProxy();
            intermediaryClass.Broadcast(new Message(Message.MessageType.YOUR_GOD, CP.godDescription(god, playerManager.getPlayer().getName())));
            CP.sendMessage(Message.MessageType.GAME_START, playerManager.getPlayer().getIdPlayer());
        }
    }

    public void setDisconnected(String name) {
        this.disconnected = true;
        this.disconnectedPlayer = Integer.parseInt(name);
    }

    public synchronized CommunicationProxy getDisconnectedProxy(){
        if(disconnected)
            return communicationProxies.get(disconnectedPlayer-1);
        return null;
    }
}
