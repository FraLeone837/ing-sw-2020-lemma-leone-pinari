package View;

import Controller.Communication.Message;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CommunicationClass implements Runnable {
    private Message messageToSend;
    //lock
    private Object toSendLock = new Object();
    private boolean isWaitingToReceive;
    private Object receivedLock = new Object();

    private CommunicationClass cc;

    private Socket server;
    private ObjectOutputStream outputStm;
    private ObjectInputStream inputStm;
    private List<ServerObserver> observers = new ArrayList<>();

    public CommunicationClass(Socket server){
        this.server=server;
    }

    //while isWaitingToReceive no one can send a message
    public void notifyToSendMessage(Message msg){
        while(isWaitingToReceive == true){
            synchronized (receivedLock){
                try{
                    receivedLock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        synchronized (toSendLock){
            messageToSend = msg;
            toSendLock.notifyAll();
        }

    }

    @Override
    public void run() {

        try{
            outputStm = new ObjectOutputStream(server.getOutputStream());
            inputStm = new ObjectInputStream(server.getInputStream());
            openConnection();

        } catch (IOException e) {
            System.out.println("server has died");
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("protocol violation");
        }
    }

    private void openConnection() throws IOException,ClassNotFoundException{
        //We have problem if you send message with GsonBuilder ecc ecc
        //Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        Gson gson = new Gson();
        reset();
        while(true){
            //for as long as our message is "null" (a.k.a we have no message to send/have already sent a message, we wait
            while(this.messageToSend.getType() == Message.MessageType.YYY){
                synchronized (toSendLock) {
                    try {
                        toSendLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Message copyOut = new Message(messageToSend.getType(),messageToSend.getObject());
            System.out.println("Message to send is: " + messageToSend );
            String converted = gson.toJson(copyOut);
            reset();
            isWaitingToReceive = true;

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
            System.out.println("Received message is " + msg);//+" and it contains: "+msg.getObject());
            /* notify the observers that we got the string */
            for (ServerObserver observer: observersCpy) {
                observer.didReceiveMessage(msg);
            }
            //gets broken only when Client.class calls didReceiveMessage
            //didReceiveMessage calls receivedMessage()
            while(isWaitingToReceive){
                synchronized (receivedLock) {
                    try {
                        receivedLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * called by observer.didReceiveMessage(msg)--
     * notifies that client received a message
     * so we can send an ack
     */
    public void receivedMessage() {
        synchronized (receivedLock){
            receivedLock.notifyAll();
        }
        isWaitingToReceive = false;
    }

    private void reset() {
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
