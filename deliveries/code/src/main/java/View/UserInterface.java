package View;

import Controller.Message;
import Model.Index;
import Model.Island;
import Model.Player;
import Model.Worker;

import java.util.List;

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
        Player player = new Player(playerManager.getServerIp(), 2);
        client.sendThis(new Message(Message.MessageType.ISLAND_INFO, player));
        while(true){
            if(inputUi){

            }
            if(inputServer){
                //client.sendThis(new Message(Message.MessageType.TYPE_0, playerManager.getServerIp()));
            }

            try {
                wait();
            } catch (Exception e) {
            }
        }
    }

    public void receivedServerInput(Message msg){
        inputServer = true;
        System.out.println(msg.getFirstObject());
        synchronized (this){
            notify();
        }
    }

    public void receivedUiInput(){
        inputUi = true;
        synchronized (this){
            notify();
        }
    }
}
