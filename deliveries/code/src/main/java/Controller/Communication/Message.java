package Controller.Communication;

import Model.Player;

import java.util.ArrayList;


/**                 !!!REALLY IMPORTANT BEFORE READING OTHER PART OF JAVADOC!!!
 *        for every message type there ***MUST*** be declared only one type of information exchange
 *        AND for every message sent server side, the client side response ***SHOULD*** be of the same
 *                                  type.
 */


public class Message {
    /**
     * creates copy
     * @param received
     */
    public Message(Message received) {
        this.object = received.getObject();
        this.type = received.getType();
    }

    public enum MessageType{
        /**
         * gets the information about all the island
         */
        ISLAND_INFO,
        /** from server to client
         *  it's the turn to move
         *  requires to choose one of
         *  the available workers (if message (int) == 3)
         *  otherwise prints that there is only one worker that can be moved
         *  and it is already chosen (highlighted)
         */
        MOVEMENT,
        /**
         * name and description of god
         */
        YOUR_GOD,
        /**
         * REQUIRES NAME
         */
        GET_NAME,
        /**
         * not needed
         */
        CHOOSE_WORKER,

        /**
         * sends a Message to the
         * client that he cannot write any input anymore
         */
        PLAYER_LOST,
        /**
         * sends a Message to the
         * client that he cannot write any input anymore and
         * that the game ended
         */
        PLAYER_WON,
        /**
         * sent from server to client
         * asks information on where to move
         * from the given indices
         */
        MOVE_INDEX_REQ,
        /**
         * sent from server to client
         * asks information on where to build
         * from the given indices
         */
        BUILD_INDEX_REQ,

        /**
         * called directly after create game to indicate the number of
         * players that will play in the game
         */
        NUMBER_PLAYERS,
        /**
         *
         */
        JOIN_GAME,
        /**
         * a message sent so the other person can afterwards wait
         */
        TURN_START,
        /**
         * Game has started
         */
        GAME_START,
        /**
         * request to choose the first worker
         * given an array of indexes
         */
        CHOOSE_INDEX_FIRST_WORKER,
        /**
        * request to choose the second worker
         * given an array of indexes
        */
        CHOOSE_INDEX_SEC_WORKER,
        /**
         * wait signal,
         * wait for other players before
         * the beginning of
         * a match
         */
        WAIT_START,
        /**
         * value to be ignored by messages in input.
         * acts as null value
         */
        ZZZ,
        /**
         * SET BY DEFAULT FROM MATCH-MANAGER AFTER
         * MOVEMENT OF WORKERS
         */
        INFORMATION,
        /**
         * message sent when one of the players disconnects
         * after which the game ends and every data is lost
         */
        END_GAME,
        /**
         * Used for god powers, asks if you
         * want to move/build again
         */
        MOVE_AGAIN,
        BUILD_AGAIN,
        /**
         * Used for god powers,
         * asks if you want to build a dome or a normal building
         */
        BUILD_DOME,
        /**
         * used for god powers,
         * asks if you want to build before the movement
         */
        BUILD_BEFORE,
        /**
         * used for Poseidon power: at the end of your turn if the worker you didn't use respects some conditions,
         * you can build with it up to three more times
         */
        BUILD_OTHER_WORKER,
        /**
         * substitutes null value
         * means that we have no msg received in output
         */
        YYY

    }
    private MessageType type;
    private Object object;

    public Message(MessageType type){
        this.type = type;
    }

    public Message(MessageType type, Object o){
        object = o;
        this.type = type;
    }


    public MessageType getType() {
        return type;
    }

    public void setType(MessageType mt){
        this.type = mt;
    }

    public void setObject(Object object){
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return getType().toString();
    }
}
