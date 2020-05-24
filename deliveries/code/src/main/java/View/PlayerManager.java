package View;

import Controller.Communication.Message;
import Model.Index;
import Model.Worker;

import java.util.List;

public interface PlayerManager {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    final String LABEL_SERVER_IP = "Which server do you want to connect to?";
    final String LABEL_TURN = "It is ";
    final String LABEL_TURN_2 = "'s turn";
    final String LABEL_HELP = "To see which is your god type /god, and it's description /godDescription" +
                                " to see your name type /name, to see your worker's id type /id, " +
                                " to see whose turn it is type /turn, to see what is the valid input to reply with type /input";
    final String LABEL_NO_INPUT = "Please wait for the game to start";
    final String LABEL_WAIT_YOUR_TURN = "It is not your turn yet, ask that question after the beginning of your turn";
    final String LABEL_USERNAME = "Write your username";
    final String LABEL_NUMBER_PLAYERS = "How many players do you want in this match? (2 or 3)";
    final String LABEL_FIRST_WORKER = "Choose where to locate the first worker";
    final String LABEL_SECOND_WORKER = "Choose where to locate the second worker";
    final String LABEL_ONLY_FIRST_WORKER_MOVE = "You can only move the first worker in this turn";
    final String LABEL_ONLY_SECOND_WORKER_MOVE = "You can only move the second worker in this turn";
    final String LABEL_CHOOSE_WORKER_TO_MOVE = "Which worker do you want to move?";
    final String LABEL_CHOOSE_WHERE_TO_MOVE = "Where do you want to move the worker?";
    final String LABEL_CHOOSE_WHERE_TO_BUILD = "Where do you want to build?";
    final String LABEL_BUILD_DOME = "Do you want to build a dome or a building?";
    final String LABEL_MOVE_AGAIN = "Do you want to ";
    final String LABEL_BUILD_BEFORE = "Do you want to build before moving?";
    final String LABEL_ID_BEGIN = "Your Id is: ";
    final String LABEL_ID_FINAL = " and the workers you can move are: ";
    final String LABEL_YOUR_GOD = "Your god is: ";
    final String LABEL_YOUR_GOD_DESC = ", ";


    /**
     * Ask the user the IP of the server it wants to connect to
     */
    public abstract void getServerIp();

    /**
     * Ask the user the username it wants to use
     */
    public abstract void getName();

    /**
     * Print the list of all the matches that the player can join
     * @param ids the ArrayList with the ids of all the matches
     * @return the id of the match that the player has choosen to join
     */
    //public abstract int listMatch(List<Integer> ids);

    /**
     * If there isn't any match on the server, the player is asked to create a new one
     * So this method ask the player how many players it wants this match to have
     */
    public abstract void chooseNumberPlayers();

    /**
     * Ask the player where to place his workers at the start of the game
     * @param firstWorker true if the player hasn't place any worker before
     */
    public abstract void placeWorker(boolean firstWorker, int[] possiblePositions);

    /**
     * Ask the player which of its workers to use in this turn
     * @param workers the list of the workers that can be moved
     */
    public abstract void chooseWorker(int workers);

    /**
     * Ask the player in which of the possible cells it wants to move the
     * previously selected worker
     * @param movements list of the possible movements
     */
    public abstract void chooseMovement(int[] movements);

    /**
     * Ask the player in which of the possible cells it wants the worker to build
     * @param buildings list of the possible cells to build in
     */
    public abstract void chooseBuilding(int[] buildings);

    /**
     * Ask the player if it wants to build a dome or a normal building
     * in the previosly selected cells
     */
    public abstract void buildDome();

    /**
     * show the god's description and name
     */
    public abstract void showGods(String[] god);

    /**
     * used if god power allows to move/build again
     * @param moveAgain only equals to MOVE_AGAIN or BUILD_AGAIN
     */
    public abstract void doItAgain(Message.MessageType moveAgain);

    /**
     * used by god powers if you want to build
     * before moving
     */
    public abstract void buildBefore();

    /**
     * shows whose turn it is
     * @param object
     */
    public void showTurn(String object);
}
