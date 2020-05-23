package Controller.Communication;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import static Controller.Communication.Message.MessageType.ZZZ;


public class ClientHandler implements Runnable
{

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";



    private Socket client;
    private Message currentMessage;
    private Message toSendMsg;
    //Parameter that shows if we can get the input from the other client
    private boolean writes;
    private ArrayList<MessageObservers> observers = new ArrayList<>();

    private CommunicationProxy personalProxy;
    //useless
    private final Object currentLock = new Object();

    private String name;

    private final Object sendLock = new Object();

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Player " + name;
    }

    public void addObserver(MessageObservers obs){
        observers.add(obs);
    }

    /**
     * changes message to be sent and then notifies the main of client handler
     * which in turn writes the object in stream
     * @param message
     */
    public void setToSendMsg(Message message){
        synchronized (sendLock){
            this.toSendMsg = message;
            sendLock.notifyAll();
        }
    }


    ClientHandler(Socket client, IntermediaryClass ic, int counter)
    {
        this.client = client;
        toSendMsg = new Message(ZZZ, "Waiting");
        this.writes = true;
        currentMessage = null;
        personalProxy = new CommunicationProxy(this,ic);
        name = (Integer.toString(counter));
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
            //calls every client and this.personalProxy to close their connections
            terminateGame();
            //closes thread
            return;
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

        /**
         * Read first object afterwards reply.
         * After that go into a while loop in which the client responds only to my requests
         */
        while(true){
            try{
//                System.out.println(ANSI_RED + "About to read" + ANSI_RESET);

                Object in = input.readObject();
                String inText = (String)in;
                Gson gson = new Gson();
                this.currentMessage = gson.fromJson(inText,Message.class);

                System.out.println("Message received cl.handler: " + currentMessage +" " + this);
                notifyObservers();

                synchronized (sendLock){
                    while(toSendMsg.getType() == ZZZ){

                        try{
                           System.out.println("WAITING ON A SEND MESSAGE cl.handler " + this);
                            sendLock.wait();
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        System.out.println(ANSI_BLUE+ "We have a new toSendMsg which is: " + toSendMsg + ANSI_RESET + " " + this);


                        output.writeObject(gson.toJson(toSendMsg));

                        if(toSendMsg.getType() == Message.MessageType.END_GAME)
                            client.close();
                    }
                }
            } catch (ClassNotFoundException e ){

            }
            toSendMsg = new Message(ZZZ,"Waiting");
        }

    }

    private void notifyObservers() {
        synchronized (this){
            ArrayList<MessageObservers> copy = new ArrayList<>(observers);
        }
        for(MessageObservers obs : observers)
            obs.receivedMessage();
    }


    public Message getCurrentMessage() {
        return currentMessage;
    }

    /**
     * receives a local message that allows the game to terminate
     */
    public void terminateGame(){
        this.personalProxy.sendMessage(Message.MessageType.END_GAME,"One player disconnected, game has been interrupted.");
        this.currentMessage = new Message(Message.MessageType.END_GAME,"One player disconnected, game has been interrupted.");
        notifyObservers();
    }

    /**
     * terminates game in case there has been a disconnection
     * of one of the players
     */
    public void terminateGame(String cause){
        this.personalProxy.interruptGame(Message.MessageType.END_GAME,"Player has been disconnected");
    }

    public String getName() {
        return "Player" + name;
    }

    public CommunicationProxy getCommProxy(){
        return personalProxy;
    }
}

