package View;

import Controller.Communication.Message;

import javax.swing.*;

public class UserInterface implements Runnable {

    private Mode mode;
    private MainFrame mainFrame;
    private GameManager gameManager;
    private PlayerManager playerManager;
    Client client;

    private boolean inputUi = false;
    private boolean inputServer = false;
    Message messageIn;
    Message messageOut;

    String ip;

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
            playerManager = new CliPlayerManager(this);
        }
        else{
            gameManager = new GuiGameManager();
            playerManager = new GuiPlayerManager(this);
            /*SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    */mainFrame = new MainFrame();
                    mainFrame.setPlayerManagerPanel(((GuiPlayerManager)playerManager).getPanel());
                    mainFrame.setTopGameManagerPanel(((GuiGameManager)gameManager).getPanel());
                    mainFrame.show();
               /* }
            });*/
        }
        messageOut = new Message(Message.MessageType.JOIN_GAME);
        playerManager.getServerIp();
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
                client.sendThis(messageOut);
            }
            if(inputServer){
                inputServer = false;
                identificationMessage(messageIn);
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
    public void receivedUiInput(Object input){
        if(messageOut.getType().equals(Message.MessageType.JOIN_GAME)){
            client = new Client(this, ip);
            Thread t = new Thread(client);
            t.start();
        }
        else{
            inputUi = true;
            messageOut.setObject(input);
//            this.ip=(String)input;
            synchronized (this){
                notify();
            }
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
        messageOut = new Message(Message.MessageType.ZZZ, null);
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
                /*invia subito*/
                //response = new Message(Message.MessageType.ZZZ, null);
                break;
            case MOVE_INDEX_REQ:
                messageOut = new Message(Message.MessageType.MOVE_INDEX_REQ);
                playerManager.chooseMovement((int[])msg.getObject());
                break;
            case BUILD_INDEX_REQ:
                messageOut = new Message(Message.MessageType.BUILD_INDEX_REQ);
                playerManager.chooseBuilding((int[])msg.getObject());
                break;
            case BUILD_DOME:
                messageOut = new Message(Message.MessageType.BUILD_DOME);
                playerManager.buildDome();
                break;
            case CHOOSE_WORKER:
                messageOut = new Message(Message.MessageType.CHOOSE_WORKER);
                playerManager.chooseWorker((int)msg.getObject());
                break;
            case CHOOSE_INDEX_FIRST_WORKER:
                messageOut = new Message(Message.MessageType.CHOOSE_INDEX_FIRST_WORKER);
                playerManager.placeWorker(true, (int[])msg.getObject());
                break;
            case CHOOSE_INDEX_SEC_WORKER:
                messageOut = new Message(Message.MessageType.CHOOSE_INDEX_FIRST_WORKER);
                playerManager.placeWorker(false, (int[])msg.getObject());
                break;
            case WAIT_START:
                messageOut = new Message(Message.MessageType.ZZZ, "Ok waiting!");
                receivedUiInput(messageOut);
                gameManager.waitForPlayer();
                break;
            case PING_IS_ALIVE:
                /*invia subito*/
                //response = new Message(Message.MessageType.PING_IS_ALIVE, null);
                break;
            case PLAYER_LOST:
                gameManager.printWin(false);
                break;
            case GET_NAME:
                messageOut = new Message(Message.MessageType.GET_NAME);
                playerManager.getName();
                break;
            case NUMBER_PLAYERS:
                messageOut = new Message(Message.MessageType.NUMBER_PLAYERS);
                playerManager.chooseNumberPlayers();
                break;
        }
    }

}
