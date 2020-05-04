package Controller.Communication;


import Controller.MatchManager;
import Model.Worker;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


public class ClientHandler implements Runnable
{
    private Socket client;
    private IntermediaryClass ic;
    private Message currentMessage;
    private Message toSendMsg;
    //Parameter that shows if we can get the input from the other client
    private boolean writes;
    private ArrayList<MessageObservers> observers = new ArrayList<>();

    private CommunicationProxy personalProxy;
    //useless
    private final Object currentLock = new Object();
    private final Object sendLock = new Object();


    public void addObserver(MessageObservers obs){
        observers.add(obs);
    }

    /**
     * changes message to be sent and then notifies the main of client handler
     * which in turn writes the object in stream
     * @param message
     */
    public void setToSendMsg(Message message){
        this.toSendMsg = message;
        synchronized (sendLock){
            sendLock.notifyAll();
        }
    }

    public void updatedMessage(Message message){

    }

    ClientHandler(Socket client, IntermediaryClass ic)
    {
        this.ic = ic;
        this.client = client;
        this.writes = true;
        currentMessage = null;
        personalProxy = new CommunicationProxy(this,ic);
        Thread t = new Thread(personalProxy);
        t.start();
    }


    @Override
    public void run()
    {
        try {
            handleClientConnection();
        } catch (IOException e) {
            System.out.println("client " + client.getInetAddress() + " connection dropped");
        }
    }

    /** Reads a message of type Message and based on the (enum) MessageType calls private methods to give/require information,
     *  then sends the information required back. Waits until the player loses/quits/wins.
     * @throws IOException if not correct messageType/Object
     * @throws NullPointerException if messageType has been lost
     */
    private void handleClientConnection() throws IOException, NullPointerException
    {
        System.out.println("Connected to " + client.getInetAddress());
        ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(client.getInputStream());
        System.out.println("WHATS WRONG?");
//        notifyObservers();
        /**
         * Read first object afterwards reply.
         * After that go into a while loop in which the client responds only to my requests
         */
        try{
            Object in = input.readObject();
            //PROBLEM??? WHY!
            System.out.println("If you're reading this it is a good sign.");
            String inText = (String)in;
            Gson gson = new Gson();
            currentMessage = gson.fromJson(inText,Message.class);
            System.out.println("HELLO?");
            notifyObservers();
            synchronized (sendLock){
                while(toSendMsg == null){
                    toSendMsg = null;

                    try{
                        sendLock.wait();

                    } catch (InterruptedException e){
                        e.printStackTrace();
                        System.out.println("\nTheoretically we have a new toSendMsg which is: " + toSendMsg);
                    }
                    if(toSendMsg == null) continue;

                    output.writeObject(gson.toJson(toSendMsg));

                }
            }
        } catch (ClassNotFoundException e ){
            //resendMessage()
        }
//
//        try {
//            Object initialInput = input.readObject();
//            String initialRequest = (String)initialInput;
//            Gson gson = new Gson();
//            Message initMessage = gson.fromJson(initialRequest,Message.class);
//            Message toSend = null;
//            switch (initMessage.getType()){
//                case JOIN_GAME:
//                    toSend = new Message(Message.MessageType.GET_NAME, "What is your name");
//                    //match
//                    //This method creates a new game from scratch or connects people to another one already created
//                    //constructor getFreeGame();
//                    break;
//                case GET_NAME:
//                    // flag = JoinGame(); if flag ask for number of players, otherwise give a ping or a game_started signal
//                    break;
//                case NUMBER_PLAYERS:
//                    // a
//
//            }
//            /* responds to input from the client */
//            while (true) {
//
//                if(writes == true){
//                    Object next = input.readObject();
//                    String str = (String)next;
//                    currentMessage = gson.fromJson(str, Message.class);
//                    Message.MessageType msgType = currentMessage.getType();
////                Player player = (Player)received.getFirstObject();
//                    switch (msgType){
//                        case ISLAND_INFO:
//                            toSend = getIslandInfo();
//                            break;
//                        case MOVEMENT:
//                            toSend = getIslandInfo();
//                            break;
//                        case CHOOSE_WORKER:
//
//                            //discard
//                    }
//
////                Message toSend = new Message(Message.MessageType.ISLAND_INFO, tosnd);
//                    String converted = gson.toJson(toSend);
//                    output.writeObject(converted);
//                }
//            }
//        } catch (ClassNotFoundException | ClassCastException e) {
//            System.out.println("invalid stream from client");
//        }

        client.close();
    }

    private void notifyObservers() {
        synchronized (this){
            ArrayList<MessageObservers> copy = new ArrayList<>(observers);
        }
        for(MessageObservers obs : observers)
            obs.receivedMessage();
    }

    /**
     *
     * @return the information of current island state
     */
    protected Message getIslandInfo(){
        return new Message(Message.MessageType.ISLAND_INFO, ic.getMatchManager().getInformationArray());
    }

    /**
     *
     * @return a new Message of type ping
     */
    private Message Ping(){
        return new Message(Message.MessageType.PING_IS_ALIVE, "Are you alive?");
    }

    public Message getCurrentMessage() {
        return currentMessage;
    }

     public Worker chooseWorker(int flag){
        //get information on which worker to play
        this.sendMessage(Message.MessageType.CHOOSE_WORKER,flag);


        //stub
        return new Worker(-1);
    }

    /** Sends a message to the view from client
     *  if not already of type Message, it converts it
     * @param messageType one of the enum types
     * @param message might be overwritten based on a protocol (i.e. player lost, message is null, so it gets overwritten)
     */
    public void sendMessage(Message.MessageType messageType, Object message){
        Gson gson = new Gson();

        if(!(message instanceof Message)){
//            message = convertToMessage(message);
            switch (messageType){
                case MOVE_INDEX_REQ:
                    break;
                case PLAYER_LOST:
                    message = new Message(messageType,"You lost! From this point on you can only view the match until it ends.");
                    break;
            }
        }
        String converted = gson.toJson(message);
        try{
            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
            output.writeObject(converted);
        } catch (IOException e){
            System.out.println("Invalid IO:\n" + e);
        }
        catch( ClassCastException e ){
            System.out.println("Invalid stream:\n" + e);
        }

        return;
    }

}

