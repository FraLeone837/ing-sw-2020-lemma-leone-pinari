package ControllerTest.UtilClasses;


import Controller.Communication.Message;
import Controller.ViewManager;
import View.CliMode.CliMain;
import View.Communication.ServerAdapter;
import View.Interfaces.ServerObserver;

import java.io.IOException;
import java.net.Socket;

public class GameCreator implements ServerObserver {
    GameCreator gameCreator;
    ServerAdapter playerOne;
    ServerAdapter playerTwo;
    ViewManager game;
    WriterClass writerClass;

    final int FIRST_PLAYER = 1;
    final int SECOND_PLAYER = 2;

    String LABEL_LOCAL_IP = "/ignore/localIP.txt";
    String LABEL_FIRST_NAME = "/ignore/firstName.txt";
    String LABEL_SECOND_NAME = "/ignore/secondName.txt";
    String LABEL_NO = "/ignore/firstName.txt";
    String LABEL_YES = "/ignore/firstName.txt";
    String LABEL_NUMBER_TWO = "/ignore/two.txt";
    private Message lastMessageOne;
    private Message lastMessageTwo;

    //creates game and two players
    private GameCreator(){
        Socket server;
        try {
            server = new Socket("127.0.0.1", 7777);
        } catch (IOException e) {
            System.out.println("server unreachable");
            return;
        }
        playerOne = new ServerAdapter(server, 1);
        playerTwo = new ServerAdapter(server, 2);
        game = new ViewManager();
        try{
            writerClass = new WriterClass(false);
        } catch (IOException e){
            e.printStackTrace();
        }
        initializeFiles();
    }

    public GameCreator getGameCreator() {
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

    public void connectPlayers(){
        Thread connectionP1 = new Thread(playerOne);
        connectionP1.start();
        automaticReplies(playerOne, FIRST_PLAYER);

        Thread connectionP2 = new Thread(playerTwo);
        connectionP2.start();
        automaticReplies(playerTwo, SECOND_PLAYER);
    }
    //Responds to every message before the game starts
    private synchronized void automaticReplies(ServerAdapter player,int number) {

        player.requestSending(new Message(Message.MessageType.JOIN_GAME, "Hello!"));
        Message lastMessage;
        if(number == FIRST_PLAYER){
            lastMessage = lastMessageOne;
        } else lastMessage = lastMessageTwo;

        while(lastMessage == null){
            try{
                wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        while(lastMessage.getType() != Message.MessageType.WAIT_START){
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
                                    LABEL_NUMBER_TWO)
                    );
                    break;
                default:
                    player.requestSending(
                            new Message(lastType,
                                    "Ok!")
                    );
            }
        }
    }




    //sends back message as an answer to "User Information" messages

    private void reply(Message newMsg, ServerAdapter player) {
        Message.MessageType lastType = newMsg.getType();
        switch (lastType){
            case GAME_START:
            case YOUR_GOD:
            case ISLAND_INFO:
            case PLAYER_WON:
            case PLAYER_LOST:
            case OTHERS_LOSS:
                player.requestSending(new Message(lastType,"OK!"));
        }
    }

    @Override
    public void didReceiveMessage(Message newMsg){

    }

    @Override
    public synchronized void didReceiveMessage(Message newMsg, int ID) {
        if(ID == FIRST_PLAYER)
            this.lastMessageOne = newMsg;
        else this.lastMessageTwo = newMsg;
        notifyAll();
        if(ID == 1)
            reply(newMsg,playerOne);
        else reply(newMsg,playerTwo);
    }
}
