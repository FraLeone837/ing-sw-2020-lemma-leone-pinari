package View;

import Controller.Communication.Message;
import View.Communication.ServerAdapter;
import View.Interfaces.ServerObserver;

import java.io.IOException;
import java.net.Socket;

/**
 * Accepts Clicks and text input and
 * then displays the result on screen,
 * after it being processed by other
 * classes and methods!
 */
public class Client implements Runnable, ServerObserver
{
    private Message messageIn = null;
    private Message messageOut = null;
    private UserInterface ui;
    private String ip = "127.0.0.1";
    public final static int SOCKET_PORT = 7777;
    private ServerAdapter serverAdapter;

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
            ui.receivedServerInput(null);
            return;
        }
        System.out.println("Connected. Wait for server message.");

        this.serverAdapter = new ServerAdapter(server);


        serverAdapter.addObserver(this);
        Thread serverAdapterThread = new Thread(serverAdapter);
        serverAdapterThread.start();

        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        messageIn = null;
        Message msg = new Message(Message.MessageType.JOIN_GAME, null);
        serverAdapter.requestSending(msg);
        while(messageIn == null) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        ui.receivedServerInput(messageIn);

        while (true) {


            synchronized (this) {
                messageOut = null;
                while(messageOut == null){
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                serverAdapter.requestSending(messageOut);

                messageIn = null;
                while (messageIn == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                ui.receivedServerInput(messageIn);
            }

        }

        //serverAdapter.stop();
    }


    @Override
    public synchronized void didReceiveMessage(Message msg)
    {
        messageIn = msg;
        serverAdapter.receivedMessage();
        notifyAll();
    }

    @Override
    public void didReceiveMessage(Message newMsg, int ID) {

    }

    synchronized void sendThis(Message msg){
        messageOut = msg;
        notifyAll();
    }
}

