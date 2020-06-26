package ControllerTest.UtilClasses;


import Controller.Communication.ClientHandler;
import Controller.Communication.IntermediaryClass;
import Controller.Communication.Message;
import Controller.MatchManager;
import Controller.PlayerManager;
import Controller.ViewManager;
import Model.Match;
import View.CliMode.CliMain;
import View.Communication.ServerAdapter;
import View.Interfaces.ServerObserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static Controller.Communication.ClientHandler.*;

public class GameCreator implements ServerObserver {
    public static GameCreator gameCreator;
    ServerAdapter playerOne;
    ServerAdapter playerTwo;
    IntermediaryClass game;
    WriterClass writerClass;
    IntermediaryClass iC;
    Socket server1;
    Socket server2;

    final int FIRST_PLAYER = 1;
    final int SOCKET_PORT = 7777;
    final int SECOND_PLAYER = 2;

    String LABEL_LOCAL_IP = "/ignore/localIP.txt";
    String LABEL_FIRST_NAME = "/ignore/firstName.txt";
    String LABEL_SECOND_NAME = "/ignore/secondName.txt";
    String LABEL_NO = "/ignore/no.txt";
    String LABEL_YES = "/ignore/yes.txt";
    String LABEL_NUMBER_TWO = "/ignore/two.txt";
    private Message lastMessageOne;
    private Message lastMessageTwo;

    //starts the game in cell C4. second worker in c5. 3rd worker d4, last worker d5.
    int index = 13;
    //creates game and two players
    private GameCreator(){
        new Thread(new Runnable() {public void run() {waitForPlayers();}}).start();
        try {
            this.server1 = new Socket("127.0.0.1", SOCKET_PORT);
            this.server2 = new Socket("127.0.0.1", SOCKET_PORT);
        } catch (IOException e) {
            System.out.println("server unreachable");
            return;
        }
        playerOne = new ServerAdapter(server1, 1);
        playerOne.addObserver(this);
        waitForMillies(200);

        playerTwo = new ServerAdapter(server2, 2);
        playerTwo.addObserver(this);
        game = new IntermediaryClass();
        try{
            writerClass = new WriterClass(false);
        } catch (IOException e){
            e.printStackTrace();
        }
        initializeFiles();
    }

    private synchronized void waitForMillies(int time) {
        try{
            wait(time);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static GameCreator getGameCreator() {
        if(gameCreator == null){
            gameCreator = new GameCreator();
        }
        return gameCreator;
    }

    private void initializeFiles() {
        try{
            writerClass.writeOnFile("Yes",LABEL_YES);
            writerClass.writeOnFile("No",LABEL_NO);
            writerClass.writeOnFile("2",LABEL_NUMBER_TWO);
            writerClass.writeOnFile("Achilles",LABEL_FIRST_NAME);
            writerClass.writeOnFile("Theseus",LABEL_SECOND_NAME);
            writerClass.writeOnFile("127.0.0.1", LABEL_LOCAL_IP);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public synchronized void connectPlayers(){
        Thread connectionP1 = new Thread(playerOne);
        connectionP1.start();
        System.out.println("Calling automatic replies player 1");
        automaticReplies(playerOne, FIRST_PLAYER);
        System.out.println("Calling automatic replies player 2");
        Thread connectionP2 = new Thread(playerTwo);
        connectionP2.start();
        System.out.println("Called automatic replies player 2");
        automaticReplies(playerTwo, SECOND_PLAYER);
        System.out.println("Finished automatic rep pl 2");
    }

    public synchronized Match startGame(){
        connectPlayers();
        return iC.getMatch();
    }

    public synchronized void initializeWorkers(int id){
        Message mx;
        ServerAdapter playerTurn;
        if(id == FIRST_PLAYER){
            mx = lastMessageOne;
            playerTurn = playerOne;
        }
        else{
            mx = lastMessageTwo;
            playerTurn = playerTwo;
        }
        while(mx == null || (mx.getType() != Message.MessageType.CHOOSE_INDEX_SEC_WORKER && mx.getType() != Message.MessageType.CHOOSE_INDEX_FIRST_WORKER)){
            try{
                System.out.println();
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            if(id == FIRST_PLAYER){
                mx = lastMessageOne;
            }
            else{
                mx = lastMessageTwo;
            }
        }
        System.out.println(ANSI_YELLOW + "About to send " + mx.getType() + ANSI_RESET);
        if(id == FIRST_PLAYER){
            lastMessageOne = null;
        } else {
            lastMessageTwo = null;
        }

        playerTurn.requestSending(new Message(mx.getType(), index));

        if(index ==18)
            index = 19;

        if(index ==14)
            index = 18;

        if(index == 13)
            index = 14;

    }

    public synchronized boolean isMovement(Message message){
        switch (message.getType()){
            case MOVEMENT:
            case MOVE_INDEX_REQ:
            case BUILD_INDEX_REQ:
            case CHOOSE_INDEX_FIRST_WORKER:
            case CHOOSE_INDEX_SEC_WORKER:
            case MOVE_AGAIN:
            case BUILD_AGAIN:
            case BUILD_DOME:
            case BUILD_BEFORE:
            case BUILD_OTHER_WORKER:
            return true;
        }
        return false;
    }

    public MatchManager matchManager(){
        return iC.getMatchManager();
    }

    public PlayerManager getFirstPlayer(){
        return matchManager().getPlayerManagers().get(0);
    }

    public PlayerManager getSecondPlayer(){
        return  matchManager().getPlayerManagers().get(1);
    }

    public ServerAdapter getPlayerOne() {
        return playerOne;
    }

    public ServerAdapter getPlayerTwo() {
        return playerTwo;
    }

    //Responds to every message before the game starts
    private synchronized void automaticReplies(ServerAdapter player,int number) {
        System.out.println("Sending join game player" + number);
        try{
            wait(500);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        player.requestSending(new Message(Message.MessageType.JOIN_GAME, "Hello!"));
    }

    public synchronized Message getLastMessage(int id){
        if(id == FIRST_PLAYER){
            return lastMessageOne;
        } else
            return lastMessageTwo;
    }

    //sends back message as an answer to "User Information" messages
    /**
     * answers if it's a message that can be answered automatically
     * @param newMsg
     * @param player
     * @return true if he answered, false if didn't answer
     */
    private synchronized boolean reply(Message newMsg, ServerAdapter player) {
        Message.MessageType lastType = newMsg.getType();
        switch (lastType){
            case GAME_START:
            case WAIT_START:
            case YOUR_GOD:
            case ISLAND_INFO:
            case PLAYER_WON:
            case PLAYER_LOST:
            case TURN_START:
            case OTHERS_LOSS:
                player.requestSending(new Message(lastType,"OK!"));
                if(player == playerOne){
                    if(lastType == Message.MessageType.WAIT_START){
                        lastMessageOne = new Message(Message.MessageType.ZZZ, "...");
                        notifyAll();
                        System.out.println("Putting ZZZ");
                    }
                    else lastMessageOne = null;

                }
                else if(lastType == Message.MessageType.WAIT_START){
                    lastMessageTwo = new Message(Message.MessageType.ZZZ, "...");
                    System.out.println("Putting ZZZ");
                    notifyAll();
                }
                else lastMessageTwo = null;
            /*
            System.out.println("Sending join game player" + number);
        try{
            wait(500);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        player.requestSending(new Message(Message.MessageType.JOIN_GAME, "Hello!"));
        Message lastMessage = null;
        while(lastMessage == null || lastMessage.getType() == Message.MessageType.ZZZ){
            if(number == FIRST_PLAYER){
                lastMessage = lastMessageOne;
            } else lastMessage = lastMessageTwo;
            if(lastMessage == null){
                try{
                    wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            if(lastMessage == null)
                continue;
            System.out.println(ANSI_RED+"ZZZCXC " + lastMessage + ANSI_RESET);
            if(lastMessage.getType() != Message.MessageType.WAIT_START && lastMessage.getType() != Message.MessageType.ZZZ){
                Message.MessageType lastType = lastMessage.getType();
                switch (lastType){
                    case GET_NAME:
                        if(number == FIRST_PLAYER)
                            player.requestSending(
                                    new Message(lastType,
                                            writerClass.readFromFile(LABEL_FIRST_NAME))
                            );
                        else player.requestSending(        new Message(lastType,
                                writerClass.readFromFile(LABEL_SECOND_NAME))
                        );
                        break;
                    case NUMBER_PLAYERS:
                        player.requestSending(
                                new Message(lastType,
                                        Integer.parseInt(writerClass.readFromFile(LABEL_NUMBER_TWO)))
                        );
                        break;
                    default:
                        player.requestSending(
                                new Message(lastType,
                                        "Ok!")
                        );
                }

                lastMessage = null;
                if(number == FIRST_PLAYER)
                    lastMessageOne = null;
                if(number == SECOND_PLAYER)
                    lastMessageTwo = null;
            }
            else if(lastMessage.getType() == Message.MessageType.ZZZ){
                return;
            }
        }
             */
            case GET_NAME:
                if(number == FIRST_PLAYER)
                    player.requestSending(
                            new Message(lastType,
                                    writerClass.readFromFile(LABEL_FIRST_NAME))
                    );
                else player.requestSending(        new Message(lastType,
                        writerClass.readFromFile(LABEL_SECOND_NAME))
                );
                break;
            case NUMBER_PLAYERS:
                player.requestSending(
                        new Message(lastType,
                                Integer.parseInt(writerClass.readFromFile(LABEL_NUMBER_TWO)))
                );
                break;

            return true;
        }
        return false;
    }

    /**
     * acts as viewManager//Server and waits for players one and two to connect
     */
    private void waitForPlayers() {
        ServerSocket socket;
        try {
            socket = new ServerSocket(SOCKET_PORT);
        } catch (IOException e) {
            System.out.println("cannot open server socket");
            System.exit(-1);
            return;
        }
        System.out.println("Opened server");
        int counter = 1;

        this.iC = new IntermediaryClass();

        while (true) {
            try {
                /* accepts connections; for every connection we accept,
                 * create a new Thread executing a ClientHandler */
                Socket client = socket.accept();
                System.out.println("Accepted client.");
                ClientHandler clientHandler = new ClientHandler(client, iC, counter);
                Thread thread = new Thread(clientHandler, "server_" + client.getInetAddress());
                thread.start();
                System.out.println("started thread");
                counter = counter + 1;
            } catch (IOException e) {
                System.out.println("connection dropped");
                if (iC.isAnyPlayerConnected()) {
                    iC.Broadcast(new Message(Message.MessageType.END_GAME, "Connection dropped from one player, game interrupted and finished"));
                    iC.terminateGame();
                }
            }
        }
    }

    @Override
    public synchronized void didReceiveMessage(Message newMsg){

    }

    @Override
    public synchronized void didReceiveMessage(Message newMsg, int ID) {
        if(ID == FIRST_PLAYER){
            this.lastMessageOne = newMsg;
            if(reply(newMsg, playerOne))
                System.out.println("Replied to message one automatically");
            else System.out.println("Need reply for message one");
            playerOne.receivedMessage();
        }
        else{
            this.lastMessageTwo = newMsg;
            if(reply(newMsg, playerTwo))
                System.out.println("Replied to message two automatically");
            else System.out.println("Need reply for message two");
            playerTwo.receivedMessage();
        }

        notifyAll();
    }
}
