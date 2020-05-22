package View;

import Controller.Communication.Message;

import java.io.IOException;
import java.net.Socket;


public class Client implements Runnable, ServerObserver
{
    private Message messageIn = null;
    private Message messageOut = null;
    private UserInterface ui;
    private String ip;
    public final static int SOCKET_PORT = 7777;
    boolean messageReceived;
    boolean messageToSend;


    public Client(UserInterface ui, String ip){
        this.ui = ui;
        this.ip = ip;
    }


    @Override
    public void run()
    {

        Socket server;
        try {
            server = new Socket(ip, SOCKET_PORT);
        } catch (IOException e) {
            System.out.println("server unreachable");
            return;
        }
        System.out.println("Connected");

        ServerAdapter serverAdapter = new ServerAdapter(server);
        serverAdapter.addObserver(this);
        Thread serverAdapterThread = new Thread(serverAdapter);
        serverAdapterThread.start();

        synchronized (this) {
            try {
                wait(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        messageIn = null;
        Message msg = new Message(Message.MessageType.JOIN_GAME, null);
        serverAdapter.requestSending(msg);

        synchronized (this) {
            while(!messageReceived) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        messageReceived = false;
        ui.receivedServerInput(messageIn);


        //serverAdapter.requestSending(messageOut);

        while (true) {

            messageOut = null;

            synchronized (this) {
                while(!messageToSend){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                messageToSend = false;
                messageIn = null;

                serverAdapter.requestSending(messageOut);
                while (!messageReceived) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                messageReceived = false;

            }

            ui.receivedServerInput(messageIn);
        }

        //serverAdapter.stop();
    }


    @Override
    public synchronized void didReceiveMessage(Message msg)
    {
        messageReceived = true;
        messageIn = msg;
        notifyAll();
    }

    public synchronized void sendThis(Message msg){
        messageToSend = true;
        messageOut = msg;
        notifyAll();
    }
}

