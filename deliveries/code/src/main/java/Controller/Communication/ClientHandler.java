package Controller.Communication;

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
//        System.out.println("WHATS WRONG?");
//        notifyObservers();
        /**
         * Read first object afterwards reply.
         * After that go into a while loop in which the client responds only to my requests
         */
        try{
            Object in = input.readObject();
            //PROBLEM??? WHY!
//            System.out.println("If you're reading this it is a good sign.");
            String inText = (String)in;
            System.out.println("inText: " + inText);
            Gson gson = new Gson();
            this.currentMessage = gson.fromJson(inText,Message.class);
            System.out.println(currentMessage);
//            System.out.println("HELLO?");
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

                    if(toSendMsg.getType() == Message.MessageType.END_GAME)
                        client.close();
                }
            }
        } catch (ClassNotFoundException e ){
            //resendMessage()
        }
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

    public Message getCurrentMessage() {
        return currentMessage;
    }

    public void terminateGame(){
        this.personalProxy.sendMessage(Message.MessageType.END_GAME,null);

    }
}

