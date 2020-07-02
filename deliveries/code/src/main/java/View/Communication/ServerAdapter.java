package View.Communication;

import Controller.Communication.Message;
import View.Interfaces.ServerObserver;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static Controller.Communication.ClientHandler.ANSI_PURPLE;
import static Controller.Communication.ClientHandler.ANSI_RESET;


public class ServerAdapter implements Runnable
{


    private boolean debugging = false;

    private Message messageToSend;
    private boolean isWaitingToReceive;
    int ID;

    private Message.MessageType lastMessageType;

    private Socket server;
    private ObjectOutputStream outputStm;
    private ObjectInputStream inputStm;
    private List<ServerObserver> observers = new ArrayList<>();




    public ServerAdapter(Socket server)
    {
        this.server = server;
        isWaitingToReceive = false;
    }
    public ServerAdapter(Socket server, int ID)
    {
        this.server = server;
        isWaitingToReceive = false;
    }


    public void addObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.add(observer);
        }

    }



    public synchronized void stop()
    {
        try{
            server.close();

        } catch (IOException e){
            e.printStackTrace();
        }
        notifyAll();
    }


    public synchronized void requestSending(Message msg)
    {
        if(debugging)
        System.out.println("Sending object " + msg + " - " + msg.getObject());
        messageToSend = msg;
        canSendMessage();
        notifyAll();
    }






    /**
     * takes lock of if we can send message and sends message, afterwards stands in wait
     */
    private synchronized void canSendMessage(){
        if(debugging)
        System.out.println("Can send message? " + messageToSend);
        notifyToSendMessage(messageToSend);
    }


    @Override
    public void run() {
        try{
            outputStm = new ObjectOutputStream(server.getOutputStream());
            inputStm = new ObjectInputStream(server.getInputStream());
            openConnection();
        } catch (IOException e) {
            System.out.println("server has died -- exiting -- comclass");
            for(ServerObserver observer : observers){
                observer.didReceiveMessage(new Message(Message.MessageType.END_GAME, ""));
            }
            //close all other threads
            return;
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("protocol violation");
            return;
        }
    }

    private synchronized void openConnection() throws IOException,ClassNotFoundException{
        Gson gson = new Gson();
        while(this.messageToSend == null){
            try {
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        while(true){


            while(this.messageToSend.getType() != lastMessageType && messageToSend.getType() != Message.MessageType.JOIN_GAME){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message copyOut = new Message(messageToSend.getType(),messageToSend.getObject());
            //for as long as our message is "null" (a.k.a we have no message to send/have already sent a message, we wait
            reset();
            if(debugging)
                System.out.println(ANSI_PURPLE + "Message - communication class - to send is: " + messageToSend + ANSI_RESET );
            String converted = gson.toJson(copyOut);


            /* send the string to the server and get the new string back */
            outputStm.writeObject(converted);

            String response = (String)inputStm.readObject();



            /* copy the list of observers in case some observers changes it from inside
             * the notification method */
            List<ServerObserver> observersCpy;
            synchronized (observers) {
                observersCpy = new ArrayList<>(observers);
            }
            Message msg = gson.fromJson(response, Message.class);
            lastMessageType = msg.getType();
            if(debugging)
                System.out.println(ANSI_PURPLE + "Message - communication class - received is: " + msg + " with object " + msg.getObject() + ANSI_RESET);
            /* notify the observers that we got the string */
            for (ServerObserver observer: observersCpy) {
                observer.didReceiveMessage(msg);
                observer.didReceiveMessage(msg,this.ID);
            }
            //gets broken only when Client.class calls didReceiveMessage
            //didReceiveMessage calls receivedMessage()

            while(isWaitingToReceive){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * called by observer.didReceiveMessage(msg)--
     * notifies that client received a message
     * so we can send an ack
     */
    public synchronized void receivedMessage() {
        isWaitingToReceive = false;
        notifyAll();
    }

    //while isWaitingToReceive no one can send a message
    public synchronized void notifyToSendMessage(Message msg){
        while(isWaitingToReceive == true){
            try{
                wait();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

        }
        isWaitingToReceive = true;
        messageToSend = msg;
        notifyAll();
    }

    private synchronized void reset() {
        this.messageToSend = new Message(Message.MessageType.YYY, "Waiting to send a message");
    }


}
