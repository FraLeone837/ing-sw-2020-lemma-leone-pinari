package Controller;


import Model.Player;
import Model.Worker;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class ClientHandler implements Runnable
{
    private Socket client;
    private MatchManager matchManager;
    private int PlayerId;
    //Parameter that shows if we can get the input from the other client
    private boolean writes;
    public void setPlayerId(int id){
        PlayerId = id;
    }

    public int getPlayerId() {
        return PlayerId;
    }


    ClientHandler(Socket client)
    {
        this.client = client;
        writes = true;
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

        try {
            /* responds to input from the client */
            while (true) {

                if(writes == true){
                    Object next = input.readObject();
                    String str = (String)next;
                    Gson gson = new Gson();
                    Message received = gson.fromJson(str, Message.class);
                    Message.MessageType msgType = received.getType();
//                Player player = (Player)received.getFirstObject();
                    Message toSend = null;
                    switch (msgType){
                        case ISLAND_INFO:
                            toSend = getIslandInfo();
                            break;
                        case MOVEMENT:
//                            toSend = sendMessage();

                            toSend = getIslandInfo();
                            break;
                        case NAME:
//                    case PING_IS_ALIVE:
//                    to be put in a new thread with a timeout
//                        toSend = sendPing();
//                        break;
                        default:
                            //discard
                    }

//                Message toSend = new Message(Message.MessageType.ISLAND_INFO, tosnd);
                    String converted = gson.toJson(toSend);
                    output.writeObject(converted);
                }
            }
        } catch (ClassNotFoundException | ClassCastException e) {
            System.out.println("invalid stream from client");
        }

        client.close();
    }

    //Stub
    private Message getIslandInfo(){

        return new Message(Message.MessageType.ISLAND_INFO, matchManager.getInformationArray());
    }

    private Message sendPing(){

        return new Message(Message.MessageType.PING_IS_ALIVE, "Are you alive?");
    }

    protected Worker chooseWorker(Player playerAsked, int flag){
        //get information on which worker to play
        //
        Message message = new Message(Message.MessageType.CHOOSE_WORKER,flag);
        this.sendMessage(message.getType(),message);

        //stub
        return new Worker(-1);
    }

    /** Sends a message to the view from client
     *
     * @param messageType
     * @param message
     */
    protected void sendMessage(Message.MessageType messageType, Object message){
        Gson gson = new Gson();

        if(!(message instanceof Message)){
//            message = convertToMessage(message);
            switch (messageType){
                case FUNCTION_STUB:
                    //call function
                    //save Message of function // message = function(param);
                    break;
                case PLAYER_LOST:
                    message = new Message(messageType,"You lost");
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

    //converts any object to a json message
    private Message convertToMessage(Object message){

        return new Message(Message.MessageType.CHOOSE_WORKER, "sad");
    }
}

