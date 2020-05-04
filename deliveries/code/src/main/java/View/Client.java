package View;

import Controller.Message;
import Controller.ViewManager;

import java.io.IOException;
import java.net.Socket;


public class Client implements Runnable, ServerObserver
{
    private Message messageIn = null;
    private Message messageOut = null;
    private UserInterface ui;
    private String ip;
    public final static int SOCKET_PORT = 7777;

    public Client(UserInterface ui, String ip, Message nameMessage){
        this.ui = ui;
        this.ip = ip;
        messageOut = nameMessage;
    }


    @Override
    public void run()
    {
        System.out.println("IP address of server?");

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

        serverAdapter.requestSending(messageOut);

        while (true) {
            try {
                wait();
            } catch (InterruptedException e) {
            }

            ui.receivedServerInput(messageIn);

            try {
                wait();
            } catch (InterruptedException e) {
            }

            serverAdapter.requestSending(messageOut);

        }

        //serverAdapter.stop();
    }


    @Override
    public synchronized void didReceiveMessage(Message msg)
    {
        messageIn = msg;
        notifyAll();
    }

    public synchronized void sendThis(Message msg){
        messageOut = msg;
        notifyAll();
    }
}

