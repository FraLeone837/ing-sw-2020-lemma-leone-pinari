package View.Communication;

import Controller.Communication.Message;
import View.Interfaces.ServerObserver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerAdapter implements Runnable
{


    private enum Commands {
        SEND_MESSAGE,
        STOP;

    }
    private Commands nextCommand;
    private Message messageToSend;
    //lock
    private Object toSendLock = new Object();
    private boolean isWaitingToReceive;
    private Object receivedLock = new Object();

    private CommunicationClass cc;

    private Socket server;
    private List<ServerObserver> observers = new ArrayList<>();


    public ServerAdapter(Socket server)
    {
        this.server = server;
        isWaitingToReceive = false;
        cc = new CommunicationClass(server);
    }


    public void addObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.add(observer);
            cc.addObserver(observer);
        }

    }


    public void removeObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.remove(observer);
            cc.removeObserver(observer);
        }
    }


    public synchronized void stop()
    {
        nextCommand = Commands.STOP;
        notifyAll();
    }


    public synchronized void requestSending(Message msg)
    {
        messageToSend = msg;
        nextCommand = Commands.SEND_MESSAGE;
        notifyAll();
    }


    @Override
    public void run()
    {
        try {
            Thread t2 = new Thread(cc);
            t2.start();
            handleServerConnection();
        } catch (IOException e) {
            System.out.println("server has died");
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("protocol violation");
        }

        try {
            server.close();
            return;
        } catch (IOException e) { }
        return;
    }


    private synchronized void handleServerConnection() throws IOException, ClassNotFoundException
    {
        /* wait for commands */
        while (true) {
            nextCommand = null;
            try {
                wait();
            } catch (InterruptedException e) { }

            if (nextCommand == null)
                continue;

            switch (nextCommand) {
                case SEND_MESSAGE:
                    canSendMessage();
                    break;

                case STOP:
                    return;
            }
        }
    }

    /**
     * takes lock of if we can send message and sends message, afterwards stands in wait
     */
    private synchronized void canSendMessage()  throws IOException, ClassNotFoundException{
        cc.notifyToSendMessage(messageToSend);
    }


    /**
     * notifies that client received a message
     * so we can send an ack
     */
    public synchronized void receivedMessage() {
        cc.receivedMessage();
    }


}
