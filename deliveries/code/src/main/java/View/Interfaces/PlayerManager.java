package View.Interfaces;

import Controller.Communication.Message;

public interface PlayerManager {

    String ANSI_RESET = "\u001B[0m";
    String ANSI_BLACK = "\u001B[30m";
    String ANSI_RED = "\u001B[31m";
    String ANSI_GREEN = "\u001B[32m";
    String ANSI_YELLOW = "\u001B[33m";
    String ANSI_BLUE = "\u001B[34m";
    String ANSI_PURPLE = "\u001B[35m";
    String ANSI_CYAN = "\u001B[36m";
    String ANSI_WHITE = "\u001B[37m";

    String LABEL_SERVER_IP = "Which server do you want to connect to?";
    String LABEL_TURN = "It is ";
    String LABEL_TURN_2 = "'s turn";
    String LABEL_HELP = "To see which is your god type /god" + System.lineSeparator() +
                                "To see map's legend type /map" + System.lineSeparator() +
                                "To see your name type /name" + System.lineSeparator() +
                                "To see your worker's id type /id" + System.lineSeparator() +
                                "To see whose turn it is type /turn" + System.lineSeparator() +
                                "To exit from the game type /exit" + System.lineSeparator() +
                                "To see what is the valid input to reply with type /input";
    String LABEL_NO_INPUT = "Please wait for the game to start";
    String LABEL_WAIT_YOUR_TURN = "It is not your turn yet, ask that question after the beginning of your turn";
    String LABEL_USERNAME = "Write your username";
    String LABEL_NUMBER_PLAYERS = "How many players do you want in this match? (2 or 3)";
    String LABEL_FIRST_WORKER = "Choose where to locate the first worker";
    String LABEL_SECOND_WORKER = "Choose where to locate the second worker";
    String LABEL_ONLY_FIRST_WORKER_MOVE = "You can only move the first worker in this turn";
    String LABEL_ONLY_SECOND_WORKER_MOVE = "You can only move the second worker in this turn";
    String LABEL_CHOOSE_WORKER_TO_MOVE = "Which worker do you want to move?";
    String LABEL_CHOOSE_WHERE_TO_MOVE = "Where do you want to move the worker?";
    String LABEL_CHOOSE_WHERE_TO_BUILD = "Where do you want to build?";
    String LABEL_BUILD_DOME = "Do you want to build a dome or a building?";
    String LABEL_MOVE_AGAIN = "Do you want to ";
    String LABEL_BUILD_BEFORE = "Do you want to build before moving?";
    String LABEL_ID_BEGIN = "Your Id is: ";
    String LABEL_ID_FINAL = " and the workers you can move are: ";
    String LABEL_YOUR_GOD = "Your god is: ";
    String LABEL_YOUR_GOD_DESC = ", ";
    String LABEL_YOUR_TURN = "It is your turn.";
    String LABEL_POSEIDON_POWER = "Do you want to build with the other worker?";
    String LABEL_OTHER_PLAYER_LOST = " has lost the game.";

    /**
     * Ask the user the IP of the server it wants to connect to
     */
    void getServerIp();


    /**
     * Once the name is validated by the server, it is stored in the player manager
     * @param name the name given and validated
     */
    void setName(String name);
    /**
     * Store the id of the first worker of this player (1-3-5)
     * @param id the id of the first worker
     */
    void setIdFirstWorker(int id);

    /**
     * If there isn't any match on the server, the player is asked to create a new one
     * So this method ask the player how many players it wants this match to have
     */
    void chooseNumberPlayers();

    /**
     * Ask the player where to place his workers at the start of the game
     * @param firstWorker true if the player hasn't place any worker before
     * @param possiblePositions all the possible positions enumerated from 0 to 24 (A0 is 0, A1 is 1, ... , E4 is 23, E5 is 24)
     */
    void placeWorker(boolean firstWorker, int[] possiblePositions);

    /**
     * Ask the player which of its workers to use in this turn
     * @param workers the list of the workers that can be moved
     */
    void chooseWorker(int workers);

    /**
     * Ask the player in which of the possible cells it wants to move the
     * previously selected worker
     * @param movements list of the possible movements
     */
    void chooseMovement(int[] movements);

    /**
     * Ask the player in which of the possible cells it wants the worker to build
     * @param buildings list of the possible cells to build in
     */
    void chooseBuilding(int[] buildings);

    /**
     * Ask the player if it wants to build a dome or a normal building
     * in the previosly selected cells
     */
    void buildDome();

    /**
     * show the god's description and name
     * @param god first index contains name of God, second index contains Its description
     * @param owner is the name of the owner of the God.
     */
    void showGods(String[] god, String owner);

    /**
     * used if god power allows to move/build again
     * @param moveAgain only equals to MOVE_AGAIN or BUILD_AGAIN
     */
    void doItAgain(Message.MessageType moveAgain);

    /**
     * used by god powers if you want to build
     * before moving
     */
    void buildBefore();

    /**
     * shows whose turn it is
     * @param object the name of the player whose turn it is
     */
    void showTurn(String object);

    /**
     * shows request of get name
     * @param object is the question asked by the server
     *               "what is your name"/"name is already being used"
     */
    void getName(String object);

    /**
     * asks if player wants to build with
     * the other worker in certain conditions (Poseidon God power)
     */
    void buildOtherWorker();

    /**
     * shows who just lost the game
     * @param object loser of the game
     */
    void printLoser(String object);
}
