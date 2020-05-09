package View;

import Controller.Communication.Message;
import Model.Index;

import java.util.List;

public class UserInterface implements Runnable {

    private Mode mode;
    private GameManager gameManager;
    private PlayerManager playerManager;
    Client client;

    private boolean inputUi;
    private boolean inputServer;
    Message messageIn;

    private String godDescription;

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
        this.mode = mode;
        if(mode== Mode.CLI){
            gameManager = new CliGameManager();
            playerManager = new CliPlayerManager();
        }
        else{
            //gameManager = new GuiGameManager();
            //playerManager = new GuiPlayerManager();
        }
        String ip = playerManager.getServerIp();
        client = new Client(this, ip);
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
                inputUi = false;
            }
            if(inputServer){
                inputServer = false;
                identificationMessage(messageIn);
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
        messageIn = msg;
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

    /**
     * Identify the message received from the server by the message type and
     * invokes the correspondent method of the GameManager to print information on
     * screen, or of the PlayerManager to ask the player an input
     * @param msg
     */
    private void identificationMessage(Message msg){
        /*default message is ZZZ*/
        Message response = new Message(Message.MessageType.ZZZ, null);
        switch(msg.getType()){
            case ISLAND_INFO:
                gameManager.updateMap((int[][][])msg.getObject());
                break;
            case GAME_START:
                int idWorker1 = ((int)msg.getObject());
                if(mode == Mode.CLI) {
                    ((CliPlayerManager) playerManager).setIdFirstWorker(idWorker1);
                    ((CliGameManager) gameManager).printIdWorkers(idWorker1);
                }
                response = new Message(Message.MessageType.ZZZ, null);
                break;
            case MOVE_INDEX_REQ:
                int chosenMovement = playerManager.chooseMovement((int[])msg.getObject());
                response = new Message(Message.MessageType.MOVE_INDEX_REQ, chosenMovement);
                break;
            case BUILD_INDEX_REQ:
                int chosenBuilding = playerManager.chooseBuilding((int[])msg.getObject());
                response = new Message(Message.MessageType.BUILD_INDEX_REQ, chosenBuilding);
                break;
            case CHOOSE_WORKER:
                int chosenWorker = playerManager.chooseWorker((int)msg.getObject());
                response = new Message(Message.MessageType.CHOOSE_WORKER, chosenWorker);
                break;
            case CHOOSE_INDEX_FIRST_WORKER:
                int chosenIndexFirst = playerManager.placeWorker(true, (int[])msg.getObject());
                response = new Message(Message.MessageType.CHOOSE_INDEX_FIRST_WORKER, chosenIndexFirst);
                break;
            case CHOOSE_INDEX_SEC_WORKER:
                int chosenIndexSecond = playerManager.placeWorker(false, (int[])msg.getObject());
                response = new Message(Message.MessageType.CHOOSE_INDEX_FIRST_WORKER, chosenIndexSecond);
                break;
            case WAIT_START:
                gameManager.waitForPlayer();
                break;
            case PING_IS_ALIVE:
                response = new Message(Message.MessageType.PING_IS_ALIVE, null);
                break;
            case PLAYER_LOST:
                gameManager.printWin(false);
                break;
            case GET_NAME:
                String playerName = playerManager.getName();
                response = new Message(Message.MessageType.GET_NAME, playerName);
                break;
            case NUMBER_PLAYERS:
                break;
        }
        client.sendThis(response);
    }
}
