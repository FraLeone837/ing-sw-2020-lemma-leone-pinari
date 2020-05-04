package View;

import Controller.Communication.Message;
import Model.Player;

import java.util.concurrent.TimeUnit;

public class UserInterface {

    private GameManager gameManager;
    private PlayerManager playerManager;

    private boolean inputUi;
    private boolean inputServer;

    public static void main(String args[]){
        UserInterface ui = new UserInterface();
    }

    public UserInterface(){
        gameManager = new CliGameManager();
        playerManager = new CliPlayerManager();
        Client client = new Client(this, playerManager.getServerIp());
        Thread t = new Thread(client);
        t.start();
        try{
            TimeUnit.SECONDS.sleep(4);
        } catch (InterruptedException e){
            System.out.println("Sleep, allowing time for client handler to be created");
            e.printStackTrace();
        }
        client.sendThis(new Message(Message.MessageType.JOIN_GAME, "Hello! /this message is ignored"));
        while(true){

            if(inputUi){

            }
            if(inputServer){
                //
                System.out.println("Hello Received input from server");

                //switch case

                //if need input from UI (ex: getName // getIndex)
                //Ask for input from UI

                //sendMessage (if ping for example, send automatically) not implemented yet

                //                                                  IMPORTANT ||| SAME TYPE
                //client.sendThis(new Message(Message.MessageType.SAME TYPE THAT IS SENT FROM SERVER, "Object "));
            }
            synchronized (this){
                try {
                        wait();
                } catch (Exception e) {
                    System.out.println("Caught exception");
                    e.printStackTrace();
                }
            }

        }
    }

    //I would do notifyAll
    //it is more secure
    public void receivedServerInput(Message msg){
        inputServer = true;
        System.out.println(msg.getFirstObject());
        synchronized (this){
            notify();
        }
    }

    //I would do notifyAll
    public void receivedUiInput(){
        inputUi = true;
        synchronized (this){
            notify();
        }
    }
}
