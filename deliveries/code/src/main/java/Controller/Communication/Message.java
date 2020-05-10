package Controller.Communication;

import Model.Player;

import java.util.ArrayList;


/**                 !!!REALLY IMPORTANT BEFORE READING OTHER PART OF JAVADOC!!!
 *        for every message type there ***MUST*** be declared only one type of information exchange
 *        AND for every message sent server side, the client side response ***SHOULD*** be of the same
 *                                  type.
 */


public class Message {
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

        PING_IS_ALIVE,

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
         * sent from client to server anytime there are no games running
         * client sends name and requires new game.
         * creates a game and sends an ack message,
         * otherwise connects the person to a game that is already created
         */
        CREATE_GAME,
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
        FINISHED_TURN,
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

        MOVE_AGAIN,
        BUILD_AGAIN
    }
    private MessageType type;
    private Object object;

    public Message(MessageType type, Object o){
        object = o;
//        list.add(o);
        this.type = type;
    }


    public MessageType getType() {
        return type;
    }

    public void setType(MessageType mt){
        this.type = mt;
    }

    public Object getFirstObject(){
        return object;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return getType().toString();
    }
}
