package Controller.Communication;

import Controller.God;
import Model.Index;
import Model.Worker;

import java.io.IOException;
import java.util.List;

/**
 * to be called from every matchManager as a way to send and successively receive messages
 * from client-side
 */
public class CommunicationProxy implements Runnable, MessageObservers{
    private ClientHandler clientHandler;
    private int id;
    //serves for methods referring to matchManager
    private IntermediaryClass ic;

    //Message updated from matchManager
    private Message toSend;

    //Message to be requested from matchManager
    private Message received;
    private final Object receivedLock = new Object();

    /**
     *
     */
    private final Object gameSideLock = new Object();
    private Message gameSideMessage;

    /**
     * if id = -1 then it needs to be set up
     * from matchManager
     * @param cl not null
     * @param ic not null
     */
    public CommunicationProxy(ClientHandler cl, IntermediaryClass ic ){
        this.clientHandler = cl;
        this.ic = ic;
        this.id = -1;
        clientHandler.addObserver(this);
    }


    @Override
    public void receivedMessage() {
        synchronized (receivedLock){
            this.received = clientHandler.getCurrentMessage();
            this.receivedLock.notifyAll();
        }
    }

    @Override
    public void run() {
        try{
            handleConnection();

        } catch (IOException e){
            System.exit(-100);
        }

    }

    /**
     * is always asleep, wakes up only when matchManager asks them to wake up
     * or when a message is received and forwards it to matchManager
     */
    public void handleConnection() throws IOException {
        ic.setCommunicationProxy(this);
        boolean gameRunning = true;
        while(gameRunning){
            received = null;
            synchronized (receivedLock){
                try{
                    receivedLock.wait();
                } catch(InterruptedException e){
                    e.printStackTrace();
                    System.out.println("\nTheoretically we have a received message and based on that we see what to do.");
                }
            }


            if(received == null) continue;
            /**
             * messages to be sent automatically without any need of input from client/controller
             */
            Message.MessageType typeCopy = received.getType();
            System.out.println(received);
            switch(typeCopy){
                case NUMBER_PLAYERS:
                case GET_NAME:
                    toSend = new Message(Message.MessageType.WAIT_START, "Waiting for other player/s to join");
                    break;
                case INFORMATION:
                    ic.Broadcast(new Message(Message.MessageType.ISLAND_INFO, ic.getMatchManager().getInformationArray()));
                    break;
                case ZZZ:
                    break;

                default:
            }

            waitForGameMessage(typeCopy);

            clientHandler.setToSendMsg(toSend);

        }


    }

    /**
     * controls if we need to wait for game messaged before sending
     * a certain type of msg
     * It is the list of messages that do not need input from
     * GameManager to be sent
     */
    private void waitForGameMessage(Message.MessageType MsT) {
        if(MsT == Message.MessageType.PING_IS_ALIVE ||
            MsT == Message.MessageType.GET_NAME ||
            MsT == Message.MessageType.JOIN_GAME ||
            MsT == Message.MessageType.FINISHED_TURN||
            MsT == Message.MessageType.INFORMATION)
            return;

        synchronized (gameSideLock){
            try{
                System.out.println("GameSideLock");
                gameSideLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }



    /**
     * Updates the object to be sent from game side.
     * Afterwards waits until someone wakes it up because the answer is ready
     * @param messageType type of message according to enum of Message.MessageType
     * @param toSend might be also null
     * @return right value of the answer (index/int for the worker/string for the name)
     */
    public Object sendMessage(Message.MessageType messageType, Object toSend){
        /**
         * prepare to send a message
         */
        synchronized (gameSideLock){
            //converts gameSideMessage into a certain message type
            convertToMessage(messageType,toSend);
            //releases lock and notifies that object has changed
            gameSideLock.notifyAll();
        }
        Message copy;

        if(!isWaitingForResponse(messageType))
            return new Object();
        /**
         * prepare to receive message
         */
        synchronized (this.receivedLock){
            while(received.getType() != messageType){
                try{
                    receivedLock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            copy = received;
            //MatchManager gets the one object so we make it default
            received.setType(Message.MessageType.INFORMATION);
        }

        return convertToSpecificObject(copy);

    }

    /**
     * converts whatever is received into an object from message
     *          following protocol
     * @param copy not null
     * @return specifiedObject
     */
    private Object convertToSpecificObject(Message copy) {
        switch (copy.getType()){
            case CHOOSE_WORKER:
                if((int)copy.getObject() == 1){
                    return "Worker 1";
                } else {
                    return "Worker 2";
                }
            case NUMBER_PLAYERS:
                return copy.getObject();
            case CHOOSE_INDEX_FIRST_WORKER:
                return (Object)convertFromIntToIndex((int)copy.getObject());
            case MOVEMENT:
            default:
                return copy;

        }
    }

    /**
     * sets this.toSend to a certain Message according to protocol
     * method used to send a message
     * @param messageType kind of message
     * @param toSend depending on what toSend CommProxy sends a flag or other
     */
    private void convertToMessage(Message.MessageType messageType, Object toSend) {
        switch(messageType){
            case BUILD_INDEX_REQ:

            case CHOOSE_INDEX_FIRST_WORKER:

            case CHOOSE_INDEX_SEC_WORKER:

            case MOVE_INDEX_REQ:
                this.toSend = new Message(messageType,convertFromIndexToInts((Index[])toSend));
                break;

            case GAME_START:
                this.toSend = new Message(messageType, convertFromPlayerIDtoInt((int) toSend));
                break;

            case GET_NAME:
            case PLAYER_LOST:
            case PLAYER_WON:
            case MOVEMENT:
            case YOUR_GOD:
                this.toSend = new Message(messageType,toSend);
                break;
        }

    }

    /**
     * Only some messages wait for an input client-side
     * we check here if our message is one of those
     * @param messageType
     * @return
     */
    private boolean isWaitingForResponse(Message.MessageType messageType) {
        switch (messageType){
            case ISLAND_INFO:
            case YOUR_GOD:
            case WAIT_START:
            case GAME_START:
            case PLAYER_LOST:
            case PLAYER_WON:
                return false;
            default:
                return true;
        }
    }

    /**
     * orders any index into a number of 0-24
     * @param number
     * @return
     */
    private Object convertFromIntToIndex(int number) {
        int x = number % 5;
        int y = (number - x)/5;
        Index ix = new Index(x,y,-1);
        return (Object)ix;
    }



    private int convertFromPlayerIDtoInt(int toSend) {
        if(toSend == 1) return 1;
        if(toSend == 2) return 3;
        if(toSend == 3) return 5;
        System.out.println("ERROR - convertFromPlayerIDtoInt!");
        return -1;
    }

    /**
     * converts all indexes to an array of ints from 0-24 which are the cells
     * @param toSend not null, might contain null
     */
    private int[] convertFromIndexToInts(Index[] toSend) {
        int[] zzz = new int[toSend.length];
        int x = 0;
        for(Index ix: toSend){
            zzz[x] = ix.getX() + ix.getY()*5;
            x++;
        }
        return zzz;
    }

    /**
     * return the NAME in first cell and GOD DESCRIPTION in second cell
     * @param god not null, god of this player
     * @return not null
     */
    public String[] godDescription(God god){
        String[] x = new String[2];
        x[0] = god.getName();
        x[1] = god.getDescription();
        return x;
    }

    /**
     * returns 1 if you can move only the first worker, 2 the second, 3 both
     * @param worker1 can be null
     * @param worker2 can be null
     * @return not null {0,1,2,3}
     */
    public int WorkersToInt(Worker worker1, Worker worker2){
        int flag = 0;
        if(worker2 != null){
            flag = 2;
        }
        if(worker1 != null){
            flag++;
        }
        return flag;
    }

}
