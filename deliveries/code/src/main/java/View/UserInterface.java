package View;

import Controller.Message;
import Model.Index;
import Model.Island;
import Model.Player;
import Model.Worker;

import java.util.List;

public class UserInterface implements Runnable {

    private GameManager gameManager;
    private PlayerManager playerManager;

    private boolean inputUi;
    private boolean inputServer;
    public enum Mode{
        CLI,
        GUI
    }

    /**
     * Initialize the Manager objects according to the Mode params
     * Make the client thread start
     * @param mode
     */
    public UserInterface(Mode mode){
        if(mode== Mode.CLI){
            gameManager = new CliGameManager();
            playerManager = new CliPlayerManager();
        }
        else{
            //gameManager = new GuiGameManager();
            //playerManager = new GuiPlayerManager();
        }
        String ip = playerManager.getServerIp();
        String name = playerManager.getName();
        Message nameMessage = new Message(Message.MessageType.NAME, name);
        Client client = new Client(this, ip, nameMessage);
        Thread t = new Thread(client);
        t.start();
    }

    /**
     * Run method of this Runnable class
     * It executes a loop in which it checks if whether there's a new input from
     * the client of from the ui, and then makes the thread wait
     * The thread will be then waken up by the methods receivedServer/UiInput
     */
    @Override
    public void run() {
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

    /**
     * Method called by the client when a new message is received
     * It notifies the thread of UserInterface to wake it up from the previous wait
     * @param msg the messaged received
     */
    public void receivedServerInput(Message msg){
        inputServer = true;
        System.out.println(msg.getFirstObject());
        synchronized (this){
            notify();
        }
    }
    /**
     * Method called by the PlayerManager when a new input is received
     * It notifies the thread of UserInterface to wake it up from the previous wait
     */
    public void receivedUiInput(){
        inputUi = true;
        synchronized (this){
            notify();
        }
    }
}
