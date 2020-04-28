package Controller;

import Model.Player;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public class ClientHandler implements Runnable
{
    private Socket client;


    ClientHandler(Socket client)
    {
        this.client = client;
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


    private void handleClientConnection() throws IOException
    {
        System.out.println("Connected to " + client.getInetAddress());

        ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(client.getInputStream());

        try {
            while (true) {
                /* read a String from the stream and write an uppercase string in response */
                Object next = input.readObject();
                String str = (String)next;

                Gson gson = new Gson();
                Message msg = gson.fromJson(str, Message.class);
                Player player = (Player)msg.getFirstObject();
                try {
                    /* simulate a complex computation */
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) { }
                Player tosnd = new Player(player.getName().toUpperCase(), player.getIdPlayer()+1);
                Message toSend = new Message(Message.MessageType.TYPE_0, tosnd);
                String converted = gson.toJson(toSend);
                output.writeObject(converted);
            }
        } catch (ClassNotFoundException | ClassCastException e) {
            System.out.println("invalid stream from client");
        }

        client.close();
    }
}

