package ControllerTest.UtilClasses;

import Controller.Communication.Message;
import ControllerTest.TestGod;
import View.Communication.ServerAdapter;
import View.Interfaces.ServerObserver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static Controller.Communication.ClientHandler.ANSI_RESET;
import static Controller.Communication.ClientHandler.ANSI_YELLOW;

public class BotPlayer implements Runnable, ServerObserver {
    Message copiedMessage = null;
    Message toSend = null;
    Message newMsg = null;
    ServerAdapter player;
    Socket server;

    final int FIRST_PLAYER = 1;

    final int SECOND_PLAYER = 2;

    int ID;
    static int index = 13;
    static int index2 = 18;
    GameCreator gameCreator;
    TestGod testGod = null;

    ArrayList<Integer> input = new ArrayList<>();
    ArrayList<String> literalInput = new ArrayList<>();
    private boolean debugging = true;

    public synchronized void addInput(int input){
        this.input.add(input);
        notifyAll();
    }
    public synchronized void addInput(String input){
        this.literalInput.add(input);
        notifyAll();
    }
    public void stop(){
        player.stop();
    }
    public synchronized int getInput(int i){
        while(input.size() == 0){
            try{
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return input.remove(0);
    }

    public synchronized String getInput(String i){
        while(literalInput.size() == 0){
            try{
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return literalInput.remove(0);
    }


    public BotPlayer(int ID, GameCreator gameCreator){
        try{
            this.server = new Socket("127.0.0.1", gameCreator.SOCKET_PORT);
        } catch (IOException e){
            e.printStackTrace();
            return;
        }
        player = new ServerAdapter(server,ID);
        this.ID = ID;
        this.gameCreator = gameCreator;
        Thread t = new Thread(player);
        t.start();
        player.addObserver(this);
    }


    @Override
    public void run() {
        //connect to server
        if(debugging)
        System.out.println("Connect " + ID);
        connect();
        if(debugging)
        System.out.println("Reply " + ID);
        //handle replies
        reply();
    }

    private synchronized void reply() {
        while(true){

        while(newMsg == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Message.MessageType lastType = newMsg.getType();

            switch (lastType){
                case TURN_START:
                    testGod.notifyMessage(this);
                    player.requestSending(new Message(lastType,"Ok!" ));
                    newMsg = null;
                    break;
                case GAME_START:
                case ISLAND_INFO:
                case WAIT_START:
                case YOUR_GOD:
                case OTHERS_LOSS:
                    player.requestSending(new Message(lastType,"OK!"));
                    newMsg = null;
                    break;
                case GET_NAME:
                    if(ID == FIRST_PLAYER){
                        player.requestSending(
                                new Message(lastType,
                                        gameCreator.getFirstName())
                        );
                    }
                    else {
                        player.requestSending(     new Message(lastType,
                                gameCreator.getSecondName())
                        );
                    }
                    newMsg = null;
                    break;
                case NUMBER_PLAYERS:
                    player.requestSending(
                            new Message(lastType, SECOND_PLAYER));
                    newMsg = null;
                    break;
                case CHOOSE_INDEX_FIRST_WORKER:
                case CHOOSE_INDEX_SEC_WORKER:
                    while(testGod == null){
                        try{
                            wait();
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    player.requestSending(new Message(lastType, initializeWorkers()));
                    newMsg = null;
                    break;
                case MOVE_INDEX_REQ:
                case MOVEMENT:
                case BUILD_INDEX_REQ:
                    player.requestSending(new Message(lastType,getInput(0)));
                    newMsg = null;
                    break;
                case MOVE_AGAIN:
                case BUILD_AGAIN:
                case BUILD_BEFORE:
                case BUILD_DOME:
                case BUILD_OTHER_WORKER:
                    player.requestSending(new Message(lastType,getInput("Yes or no?")));
                    newMsg = null;
                    break;
                case END_GAME:
                case PLAYER_WON:
                case PLAYER_LOST:
                    player.requestSending(new Message(lastType,"Ok!"));
                    return;
            }
        }
    }


    private synchronized void connect() {
        synchronized (this){
            try{
                wait(1500);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        player.requestSending(new Message(Message.MessageType.JOIN_GAME, "Hello!" + ID));
        if(debugging)
        System.out.println("Sending join game player " + ID);
    }
    @Override
    public void didReceiveMessage(Message newMsg) {
    }

    public synchronized void setTestGod(TestGod testGod){
        this.testGod = testGod;
        if(debugging)
        System.out.println("Test god set.");
        notifyAll();
    }

    @Override
    public synchronized void didReceiveMessage(Message newMsg, int ID) {
        this.newMsg = newMsg;
        notifyAll();
        if(debugging)
        System.out.println("Received newMsg in player " + ID + ". " + newMsg);
        player.receivedMessage();
    }

    private synchronized int initializeWorkers(){
        int temp;
        if(ID == 1){
            temp = index;
            if(index == 13)
                index = 14;
        }
        else{
            temp = index2;
            if(index2 ==18){
                index2 = 19;
            }
        }
        return temp;
    }

    public synchronized Message getLastMessage(){
        while(newMsg == null){
            try{
                if(debugging)
                System.out.println("Waiting get last msg");
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        if(debugging)
        System.out.println("Getting last message");
        Message temp = newMsg;
        if(!inAutomaticList(newMsg)){
            copiedMessage = newMsg;
            newMsg = null;
            notifyAll();
        }
        return temp;
    }

    private synchronized boolean inAutomaticList(Message Msg) {
        switch (Msg.getType()){
            case GAME_START:
            case WAIT_START:
            case YOUR_GOD:
            case ISLAND_INFO:
            case PLAYER_WON:
            case PLAYER_LOST:
            case TURN_START:
            case OTHERS_LOSS:
            case GET_NAME:
            case NUMBER_PLAYERS:
            case CHOOSE_INDEX_FIRST_WORKER:
            case CHOOSE_INDEX_SEC_WORKER:
            return true;
        }
        return false;
    }

    public int getID(){
        return this.ID;
    }

    public boolean isMovement(Message message){
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
    public synchronized void requestSending(Message toSend){
        if(debugging)
        System.out.println("Copied message is: " + copiedMessage);
        while(toSend.getType() != copiedMessage.getType()){
            try{
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        player.requestSending(toSend);
    }
}
