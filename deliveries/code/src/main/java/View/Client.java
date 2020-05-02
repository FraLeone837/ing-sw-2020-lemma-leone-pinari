package View;

import Controller.Message;
import Controller.ViewManager;

import java.io.IOException;
import java.net.Socket;


public class Client implements Runnable, ServerObserver
{
    private Message request = null;
    private Message response = null;
    private UserInterface ui;
    private String ip;

    public Client(UserInterface ui, String ip){
        this.ui = ui;
        this.ip = ip;
    }


    @Override
    public void run()
    {
        System.out.println("IP address of server?");

        Socket server;
        try {
            server = new Socket(ip, ViewManager.SOCKET_PORT);
        } catch (IOException e) {
            System.out.println("server unreachable");
            return;
        }
        System.out.println("Connected");

        ServerAdapter serverAdapter = new ServerAdapter(server);
        serverAdapter.addObserver(this);
        Thread serverAdapterThread = new Thread(serverAdapter);
        serverAdapterThread.start();

        while (true) {

            request = null;

            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
                response = null;

                serverAdapter.requestSending(request);
                int seconds = 0;
                while (response == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) { }
                }

            }

            ui.receivedServerInput(response);
        }

        //serverAdapter.stop();
    }


    @Override
    public synchronized void didReceiveMessage(Message msg)
    {
        response = msg;
        notifyAll();
    }

    public synchronized void sendThis(Message msg){
        request = msg;
        notifyAll();
    }
}

