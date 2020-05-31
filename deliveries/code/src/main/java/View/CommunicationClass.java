package View;

import Controller.Communication.Message;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import static java.lang.System.exit;

public class CommunicationClass implements Runnable {
    private Message messageToSend;
    private boolean isWaitingToReceive;
    boolean debugging = false;

    private Message.MessageType lastMessageType;

    private Socket server;
    private ObjectOutputStream outputStm;
    private ObjectInputStream inputStm;
    private List<ServerObserver> observers = new ArrayList<>();

    public CommunicationClass(Socket server){
        this.server=server;
    }

    @Override
    public void run() {
        try{
            outputStm = new ObjectOutputStream(server.getOutputStream());
            inputStm = new ObjectInputStream(server.getInputStream());
            openConnection();
        } catch (IOException e) {
            System.out.println("server has died -- exiting");
            //close all other threads
            exit(-1);
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("protocol violation");
        }
    }

    private synchronized void openConnection() throws IOException,ClassNotFoundException{
        Gson gson = new Gson();
        while(true){

            //for as long as our message is "null" (a.k.a we have no message to send/have already sent a message, we wait
            reset();
            while(this.messageToSend.getType() != lastMessageType && messageToSend.getType() != Message.MessageType.JOIN_GAME){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message copyOut = new Message(messageToSend.getType(),messageToSend.getObject());
            if(debugging)
            System.out.println("Message to send is: " + messageToSend );
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
            /* notify the observers that we got the string */
            for (ServerObserver observer: observersCpy) {
                observer.didReceiveMessage(msg);
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

    public void addObserver(ServerObserver observer) {
        synchronized (observers){
            this.observers.add(observer);
        }
    }

    public void removeObserver(ServerObserver observer) {
        synchronized (observers){
            this.observers.remove(observer);
        }
    }
}
