package Controller;

import Controller.Communication.ClientHandler;
import Controller.Communication.IntermediaryClass;
import Controller.Communication.Message;
import Model.Match;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ViewManager
{
    public final static int SOCKET_PORT = 7777;

    /** Passively opens the server side and waits for clients to connect with.
     *  Once connected calls clientHandler to do all the functionalities as a new Thread.
     * @param args not needed
     */
    public static void main(String[] args)
    {
        ServerSocket socket;
        try {
            socket = new ServerSocket(SOCKET_PORT);
        } catch (IOException e) {
            System.out.println("cannot open server socket");
            System.exit(1);
            return;
        }
        int counter = 1;

        IntermediaryClass iC = new IntermediaryClass();

        while (true) {
            try {
                /* accepts connections; for every connection we accept,
                 * create a new Thread executing a ClientHandler */
                Socket client = socket.accept();
                ClientHandler clientHandler = new ClientHandler(client, iC,counter);
                Thread thread = new Thread(clientHandler, "server_" + client.getInetAddress());
                thread.start();
                counter = counter + 1;
            } catch (IOException e) {
                System.out.println("connection dropped");
            }
        }
    }




}
