package View;

import Controller.Communication.Message;
import View.CliMode.CliGameManager;
import View.CliMode.CliPlayerManager;
import View.GUIMode.GuiGameManager;
import View.GUIMode.GuiPlayerManager;
import View.GUIMode.MainFrame;
import View.Interfaces.GameManager;
import View.Interfaces.PlayerManager;

import javax.swing.*;
import java.util.ArrayList;
import static java.lang.System.exit;
import static java.lang.System.in;

/**
 * Converts logic of communication into
 * human-readable input for the player to
 * interact with
 */
public class UserInterface implements Runnable {

    private Mode mode;
    private MainFrame mainFrame;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private Client client;

    private boolean connected = false;
    private boolean inputUi = false;
    private boolean inputServer = false;
    private Message messageIn;
    private Message messageOut;

    private String ip;
    private String name;
    private int idFirstWorker;

    public enum Mode{
        CLI,
        GUI
    }

    /**
     * Initialize the Manager objects according to the Mode params
     * Make the client thread start
     * @param mode (CLI - for cli mode or GUI-for graphical user interface mode)
     */
    public UserInterface(Mode mode){
        this.mode = mode;
        if(mode== Mode.CLI){
            gameManager = new CliGameManager();
            playerManager = new CliPlayerManager(this);
            Thread player = new Thread((CliPlayerManager)playerManager);
            player.start();
        }
        else{
            gameManager = new GuiGameManager();
            playerManager = new GuiPlayerManager(this);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    mainFrame = new MainFrame();
                    mainFrame.setPlayerManagerPanel(((GuiPlayerManager)playerManager).getPanel());
                    mainFrame.setTopGameManagerPanel(((GuiPlayerManager)playerManager).getInfoPanel());
                    mainFrame.setBottomGameManagerPanel(((GuiGameManager)gameManager).getPanel());
                    mainFrame.setGodLayout(((GuiPlayerManager)playerManager).getGodPanel());
                    mainFrame.show();
                }
            });
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
            synchronized (this){
                if(inputUi){
                    inputUi = false;
                    client.sendThis(messageOut);
                    if(mode == Mode.CLI) {
                        if (messageOut.getType() == Message.MessageType.PLAYER_LOST || messageOut.getType() == Message.MessageType.PLAYER_WON)
                            exit(0);
                    }
                }
                if(inputServer){
                    inputServer = false;
                    identificationMessage(messageIn);
                }
            }

            try {
                wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method called by the client when a new message is received
     * It notifies the thread of UserInterface to wake it up from the previous wait
     * @param msg the messaged received
     */
    synchronized void receivedServerInput(Message msg){
        if(msg == null && !connected)
            playerManager.getServerIp();
        else {
            connected = true;
            inputServer = true;
            messageIn = msg;
            notifyAll();
        }
    }
    /**
     * Method called by the PlayerManager when a new input is received
     * It notifies the thread of UserInterface to wake it up from the previous wait
     * @param input what is received (never null)
     */
    public synchronized void receivedUiInput(Object input){
        if(messageOut.getType().equals(Message.MessageType.JOIN_GAME)){
            ip = (String)input;
            client = new Client(this, ip);
            Thread t = new Thread(client);
            t.start();
        }
        else{
            inputUi = true;
            switch(messageOut.getType()){
                /* THESE RETURN A NUMBER */
                case MOVE_INDEX_REQ:
                case BUILD_INDEX_REQ:
                case CHOOSE_INDEX_FIRST_WORKER:
                case CHOOSE_INDEX_SEC_WORKER:
                    if(mode == Mode.CLI)
                        input = correspondingCellNumeration((String) input);
                    break;
                case CHOOSE_WORKER:
                    input = (Integer.parseInt((String)input)) - idFirstWorker + 1;
                    break;
                case NUMBER_PLAYERS:
                    input = Integer.parseInt((String)input);
                    break;
                case MOVEMENT:
                    if(mode == Mode.CLI)
                        input = Integer.parseInt((String)input);
                    break;
                /* THESE RETURN A BOOLEAN */
                case BUILD_DOME:
                    input = ((String)input).equals("DOME");
                    break;
                case BUILD_AGAIN:
                case BUILD_OTHER_WORKER:
                case BUILD_BEFORE:
                case MOVE_AGAIN:
                    if(((String)input).length() == 1){
                        input = ((String)input).equals("Y");
                    }
                    else
                    input = ((String)input).equals("YES");
                    break;
                /* THESE RETURN A SIGNAL */
                case GET_NAME:
                    playerManager.setName((String)input);
                    name = (String)input;
                    break;
                default:
                    //do nothing
            }
            messageOut.setObject(input);
            notifyAll();
        }
    }

    /**
     * Identify the message received from the server by the message type and
     * invokes the correspondent method of the GameManager to print information on
     * screen, or of the PlayerManager to ask the player an input
     * @param msg message received from server
     */
    private synchronized void identificationMessage(Message msg){
        messageOut = new Message(msg.getType(), "Ok!");

        switch(msg.getType()){
            case ISLAND_INFO:
                if(mode == Mode.CLI)
                    gameManager.updateMap(convertToIntArray((ArrayList<Double>)msg.getObject()));
                else{
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ((GuiPlayerManager) playerManager).setUpMap(convertToIntArray((ArrayList<Double>)msg.getObject()));
                        }
                    });
                }
                receivedUiInput(messageOut);
                break;
            case GAME_START:
                idFirstWorker = convertToInt((Double) msg.getObject());
                if(mode == Mode.CLI) {
                    ((CliPlayerManager) playerManager).setIdFirstWorker(idFirstWorker);
                    ((CliGameManager) gameManager).printIdWorkers(idFirstWorker);
                }
                else
                    ((GuiPlayerManager) playerManager).setIdFirstWorker(idFirstWorker);
                receivedUiInput(new Message(Message.MessageType.GAME_START, "Ok!"));
                break;
            case MOVE_INDEX_REQ:
                messageOut = new Message(Message.MessageType.MOVE_INDEX_REQ);
                playerManager.chooseMovement(convertToIntArray((ArrayList<Double>)msg.getObject()));
                break;
            case BUILD_INDEX_REQ:
                messageOut = new Message(Message.MessageType.BUILD_INDEX_REQ);
                playerManager.chooseBuilding(convertToIntArray((ArrayList<Double>)msg.getObject()));
                break;
            case CHOOSE_WORKER:
                messageOut = new Message(Message.MessageType.CHOOSE_WORKER);
                playerManager.chooseWorker(convertToInt((Double)msg.getObject()));
                break;
            case CHOOSE_INDEX_FIRST_WORKER:
                messageOut = new Message(Message.MessageType.CHOOSE_INDEX_FIRST_WORKER);
                playerManager.placeWorker(true, convertToIntArray((ArrayList<Double>)msg.getObject()));
                break;
            case CHOOSE_INDEX_SEC_WORKER:
                messageOut = new Message(Message.MessageType.CHOOSE_INDEX_SEC_WORKER);
                playerManager.placeWorker(false, convertToIntArray((ArrayList<Double>)msg.getObject()));
                break;
            case WAIT_START:
                receivedUiInput(messageOut);
                gameManager.waitForPlayer();
                break;
            case PLAYER_WON:
                gameManager.printWin(true);
                receivedUiInput(messageOut);
                break;
            case PLAYER_LOST:
                gameManager.printWin(false);
                receivedUiInput(messageOut);
                break;
            case GET_NAME:
                messageOut = new Message(Message.MessageType.GET_NAME);
                playerManager.getName((String)msg.getObject());
                break;
            case NUMBER_PLAYERS:
                messageOut = new Message(Message.MessageType.NUMBER_PLAYERS);
                playerManager.chooseNumberPlayers();
                break;
            case YOUR_GOD:
                String[] god = new String[2];
                String owner;
                god[0] = ((ArrayList<String>) msg.getObject()).get(0);
                god[1] = ((ArrayList<String>) msg.getObject()).get(1);
                owner = ((ArrayList<String>) msg.getObject()).get(2);
                gameManager.showGod(god);
                playerManager.showGods(god,owner);
                receivedUiInput(messageOut);
                break;
            case BUILD_AGAIN:
                messageOut = new Message(Message.MessageType.BUILD_AGAIN);
                playerManager.doItAgain(Message.MessageType.BUILD_AGAIN);
                break;
            case MOVE_AGAIN:
                messageOut = new Message(Message.MessageType.MOVE_AGAIN);
                playerManager.doItAgain(Message.MessageType.MOVE_AGAIN);
                break;
            case BUILD_BEFORE:
                messageOut = new Message(Message.MessageType.BUILD_BEFORE);
                playerManager.buildBefore();
                break;
            case BUILD_DOME:
                messageOut = new Message(Message.MessageType.BUILD_DOME);
                playerManager.buildDome();
                break;
            case MOVEMENT:
                messageOut = new Message(Message.MessageType.MOVEMENT);
                playerManager.chooseWorker(convertToInt((Double)msg.getObject()));
                break;
            //NEWLY ADDED! as of 23-05
            case TURN_START:
                playerManager.showTurn((String)msg.getObject());
                receivedUiInput(messageOut);
                break;
            //added in 11-06
            case BUILD_OTHER_WORKER:
                playerManager.buildOtherWorker();
                break;
            case END_GAME:
                if(mode == Mode.CLI){
                    System.out.println(msg.getObject());
                    System.out.println("Exiting");
                    /*
                     * method that quits the game with a warning
                     */
                    exit(-1);
                }
                break;
            case OTHERS_LOSS:
                playerManager.printLoser((String)msg.getObject());
                receivedUiInput(messageOut);
                break;
            default:
                receivedUiInput(messageOut);
        }
    }

    /**
     * Convert the double given by GSON to int
     * @param d the double value
     * @return the number converted to int
     */
    private int convertToInt(Double d){
        return d.intValue();
    }

    /**
     * Convert an array list of double into an array of int
     * @param d the array list of double
     * @return the array of int
     */
    private int[] convertToIntArray(ArrayList<Double> d){
        int[] toReturn = new int[d.size()];
        for(int i=0; i<d.size(); i++){
            toReturn[i] = d.get(i).intValue();
        }
        return toReturn;
    }


    /**
     * Given the coordinates from console, this method validates them
     * and give back a corresponding int according to the numeration
     * @param coor a two char String corresponding to a Cell (A-E)(1-5)
     * @return an int corresponding to the numeration of the cell
     */
    private int correspondingCellNumeration(String coor){
        if(coor.length()!=2)
            return -1;
        if(coor.charAt(0)<'a'&& coor.charAt(0)>'e')
            return -1;
        if(coor.charAt(1)<'1'&& coor.charAt(1)>'5')
            return -1;
        int x = coor.charAt(0)-65;
        int y = coor.charAt(1)-48-1;
        return y * 5 + x;
    }

}
