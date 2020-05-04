package View;

import Controller.Communication.Message;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerAdapter implements Runnable
{
    private enum Commands {
        SEND_MESSAGE,
        STOP
    }
    private Commands nextCommand;
    private Message messageToSend;

    private Socket server;
    private ObjectOutputStream outputStm;
    private ObjectInputStream inputStm;

    private List<ServerObserver> observers = new ArrayList<>();


    public ServerAdapter(Socket server)
    {
        this.server = server;
    }


    public void addObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.add(observer);
        }
    }


    public void removeObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.remove(observer);
        }
    }


    public synchronized void stop()
    {
        nextCommand = Commands.STOP;
        notifyAll();
    }


    public synchronized void requestSending(Message msg)
    {
        nextCommand = Commands.SEND_MESSAGE;
        messageToSend = msg;
        notifyAll();
    }


    @Override
    public void run()
    {
        try {
            outputStm = new ObjectOutputStream(server.getOutputStream());
            inputStm = new ObjectInputStream(server.getInputStream());
            handleServerConnection();
        } catch (IOException e) {
            System.out.println("server has died");
        } catch (ClassCastException | ClassNotFoundException e) {
            System.out.println("protocol violation");
        }

        try {
            server.close();
        } catch (IOException e) { }
    }


    private synchronized void handleServerConnection() throws IOException, ClassNotFoundException
    {
        /* wait for commands */
        while (true) {
            nextCommand = null;
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (nextCommand == null)
                continue;

            switch (nextCommand) {
                case SEND_MESSAGE:
                    doSendMessage();
                    break;

                case STOP:
                    return;
            }
        }
    }


    private synchronized void doSendMessage() throws IOException, ClassNotFoundException
    {
        System.out.println("Message to send is: " + messageToSend);
        Gson gson = new Gson();

        String converted = gson.toJson(messageToSend);

        /* send the string to the server and get the new string back */
        outputStm.writeObject(converted);
        String newStr = (String)inputStm.readObject();

        /* copy the list of observers in case some observers changes it from inside
         * the notification method */
        List<ServerObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<>(observers);
        }

        Message msg = gson.fromJson(newStr, Message.class);
        System.out.println("Message received is: " + msg);
        /* notify the observers that we got the string */
        for (ServerObserver observer: observersCpy) {
            observer.didReceiveMessage(msg);
        }
    }

}
