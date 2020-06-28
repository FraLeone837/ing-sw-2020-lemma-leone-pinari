package ControllerTest.UtilClasses;


import Controller.Communication.ClientHandler;
import Controller.Communication.IntermediaryClass;
import Controller.Communication.Message;
import Controller.MatchManager;
import Controller.PlayerManager;
import Model.Match;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameCreator {
    public static final int Apollo = 0;
    public static final int Artemis = 1;
    public static final int Athena = 2;
    public static final int Atlas = 3;
    public static  final int Demeter = 4;
    public static final int Hephaestus = 5;
    public static final int Minotaur = 6;
    public static final int Pan = 7;
    public static final int Prometheus = 8;
    public static final int Hera = 9;
    public static final int Hestia = 10;
    public static final int Poseidon = 11;
    public static final int Triton = 12;
    public static final int Zeus = 13;

    public String LABEL_FIRST_NAME = "/ignore/firstName.txt";
    String LABEL_LOCAL_IP = "/ignore/localIP.txt";
    String LABEL_SECOND_NAME = "/ignore/secondName.txt";
    String LABEL_NO = "/ignore/no.txt";
    String LABEL_YES = "/ignore/yes.txt";
    String LABEL_NUMBER_TWO = "/ignore/two.txt";


    public int CELL_E5 = 24; //3,4,0
    public int CELL_E4 = 23; //3,4,0
    public int CELL_E3 = 22; //3,4,0
    public int CELL_E2 = 21; //3,4,0
    public int CELL_E1 = 20; //3,4,0

    public int CELL_D5 = 19; //3,4,0
    public int CELL_D4 = 18; //3,3,0
    public int CELL_D3 = 17; //3,2,0
    public int CELL_D2 = 16; //3,1,0
    public int CELL_D1 = 15; //3,0,0

    public int CELL_C5 = 14; //2,4,0
    public int CELL_C4 = 13; //2,3,0
    public int CELL_C3 = 12; //2,2,0
    public int CELL_C2 = 11; //2,1,0
    public int CELL_C1 = 10; //2,0,0

    public int CELL_B5 = 9;  //1,4,0
    public int CELL_B4 = 8;  //1,3,0
    public int CELL_B3 = 7;  //1,2,0
    public int CELL_B2 = 6;  //1,1,0
    public int CELL_B1 = 5;  //1,0,0

    public int CELL_A5 = 4;  //0,4,0
    public int CELL_A4 = 3;  //0,3,0
    public int CELL_A3 = 2;  //0,2,0
    public int CELL_A2 = 1;  //0,1,0
    public int CELL_A1 = 0;  //0,0,0



    public static int SOCKET_PORT = 7777-1;
    static boolean serverAck = false;
    int playerCounter = 1;
    int requestedPlayer = 0;
    final int SECOND_PLAYER = 2;
    BotPlayer playerOne;
    BotPlayer playerTwo;
    IntermediaryClass game;
    WriterClass writerClass;
    IntermediaryClass iC;
    //server
    Thread server;

    public String getFirstName(){
        return writerClass.readFromFile(LABEL_FIRST_NAME);
    }
    public String getSecondName(){
        return writerClass.readFromFile(LABEL_SECOND_NAME);
    }


    private boolean serverOpen = false;
    int god = -1;
    //creates game and two players
    public GameCreator(int god){
        this.god = god;
        this.server = new Thread(new Runnable() {public void run() {waitForPlayers();}});
        server.start();
        synchronized (this){
            while(!serverOpen){
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        game = new IntermediaryClass();
        try{
            writerClass = new WriterClass(false);
        } catch (IOException e){
            e.printStackTrace();
        }
        initializeFiles();
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
        playerOne = new BotPlayer(playerCounter, this);
        Thread connectionP1 = new Thread(playerOne);
        connectionP1.start();
        playerCounter++;
        try{
            wait(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        playerTwo = new BotPlayer(playerCounter, this);
        Thread connectionP2 = new Thread(playerTwo);
        connectionP2.start();
    }

    public synchronized Match startGame(){
        System.out.println("connect players");
        connectPlayers();
        System.out.println("Waiting for ic to not be null");
        synchronized (this){
            while(iC == null){
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        return iC.getMatch();
    }


    public MatchManager matchManager(){
        return iC.getMatchManager();
    }

    public synchronized PlayerManager getFirstPlayer(){
        int playerMngSize = matchManager().getPlayerManagers().size();
        while( requestedPlayer%2 >= playerMngSize){
            try{
                wait(1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            playerMngSize = matchManager().getPlayerManagers().size();
        }
        PlayerManager temp = matchManager().getPlayerManagers().get(requestedPlayer%2);
        requestedPlayer++;
        return temp;
    }

    public synchronized PlayerManager getSecondPlayer(){
        return  getFirstPlayer();
    }

    public synchronized BotPlayer getPlayerOne() {
        return playerOne;
    }

    public synchronized BotPlayer getPlayerTwo() {
        return playerTwo;
    }

    /**
     * acts as viewManager//Server and waits for players one and two to connect
     */
    private void waitForPlayers() {
        ServerSocket socket;
        System.out.println("Opening server in socket port: "+ SOCKET_PORT);
        SOCKET_PORT++;
        try {
            socket = new ServerSocket(SOCKET_PORT);
        } catch (IOException e) {
            System.out.println("cannot open server socket");
            System.out.println("Caught IOException");
            e.printStackTrace();
            return;
        }
        System.out.println("Opened server");
        serverOpen = true;
        synchronized (this){
            notifyAll();
        }
        int counter = 1;

        this.iC = new IntermediaryClass(god);
        System.out.println("Server status open?:" + serverOpen);
        while (true){
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
                System.out.println("Server status open?:" + serverOpen);
            } catch (IOException e) {
                System.out.println("connection dropped");
                if (iC.isAnyPlayerConnected()) {
                    iC.Broadcast(new Message(Message.MessageType.END_GAME, "Connection dropped from one player, game interrupted and finished"));
                    iC.terminateGame();
                }
            }
        }
    }

}
