package Controller.Communication;

import Controller.God;
import Model.Index;
import Model.Worker;

import java.io.IOException;
import java.util.List;

/**
 * to be called from every matchManager as a way to send and successively receive messages
 * from client-side
 * uses observer & observable design pattern
 */
public class CommunicationProxy implements Runnable, MessageObservers{
    //counts the time since last message
    private Timer timer;
    private static int timeConstant = 99999;

    private ClientHandler clientHandler;
    //serves for methods referring to matchManager
    private IntermediaryClass ic;

    //Message updated from matchManager
    private Message toSend;
    private final Object gameSideLock = new Object();
    private boolean isWaitingToSend;

    //Message to be requested from matchManager
    private Message received = new Message(Message.MessageType.ZZZ,"have not received anything");
    private final Object receivedLock = new Object();

    private boolean acceptInput;


    public void setAcceptInput(boolean acceptInput) {
        this.acceptInput = acceptInput;
    }

    /**
     * @param cl not null
     * @param ic not null
     */
    public CommunicationProxy(ClientHandler cl, IntermediaryClass ic ){
        this.clientHandler = cl;
        this.ic = ic;
        this.acceptInput = true;
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
            this.timer = new Timer(timeConstant,ic);
            handleConnection();

        } catch (IOException e){
            ic.terminateGame();
            e.printStackTrace();
            System.exit(-100);
        }

    }

    /**
     * is always asleep, wakes up only when matchManager asks them to wake up
     * or when a message is received and forwards it to matchManager
     */
    private void handleConnection() throws IOException {
        ic.setCommunicationProxy(this);
        ic.setClientHandlers(clientHandler);
        Thread t = new Thread(timer);
        t.start();

        while(true){
            received.setType(Message.MessageType.ZZZ);
            isWaitingToSend = false;
            //received = null;
            synchronized (receivedLock){
                try{
                    receivedLock.wait();
                } catch(InterruptedException e){
                    e.printStackTrace();
                    System.out.println("\nTheoretically we have a received message and based on that we see what to do.");
                }
            }


//            if(received == null) continue;
            /**
             * messages to be sent automatically without any need of input from client/controller
             */
            Message.MessageType typeCopy = received.getType();
            System.out.println("What Comm proxy received: " + received);
            //if we do not accept input we put type zzz and consider it as useless input
            if(!acceptInput)
                received.setType(Message.MessageType.ZZZ);

            switch(typeCopy){
                case NUMBER_PLAYERS:
                case GET_NAME:
                    toSend = new Message(Message.MessageType.WAIT_START, "Waiting for other player/s to join");
                    break;
                case INFORMATION:
                    ic.Broadcast(new Message(Message.MessageType.ISLAND_INFO, ic.getMatchManager().getInformationArray()));
                    break;
                case END_GAME:
                    return;
                case ZZZ:
                    break;
                default:
            }

            waitForGameMessage(typeCopy);

            timer.setCurrentSecond(timeConstant);
            System.out.println("About to send message to client handler from com proxy");
            clientHandler.setToSendMsg(toSend);

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
        /**
         * if it is the first message the match manager is sending
         * he will have to wait until client sends Join_Game
         */
        if(messageType == Message.MessageType.GET_NAME){
            sentJoinGame();
        }
        synchronized (gameSideLock){
            while(this.isWaitingToSend == false){
                try{
                    gameSideLock.wait();
                } catch (InterruptedException e ){
                    e.printStackTrace();
                }
            }
            //converts gameSideMessage a.k.a toSend into a certain message type
            convertToMessage(messageType,toSend);
            //releases lock and notifies that object has changed
            gameSideLock.notifyAll();
            System.out.println("GameSideLock notified --:-- What Comm proxy sent: "+ messageType + "  " + this);
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
                    System.out.println("Waiting for message to return - received lock wait");
                    receivedLock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            copy = received;
            //MatchManager gets the object they want so we send a map of the current status
            if(isNotBeforeGameInput(copy.getType()))
                received.setType(Message.MessageType.INFORMATION);
        }

        return convertToSpecificObject(copy);

    }

    /**
     * if we get input that changes the board, returns true
     * @param messageType
     * @return
     */
    private boolean isNotBeforeGameInput(Message.MessageType messageType) {
        switch (messageType){
            case MOVEMENT:
            case CHOOSE_INDEX_FIRST_WORKER:
            case CHOOSE_INDEX_SEC_WORKER:
            case MOVE_INDEX_REQ:
            case BUILD_INDEX_REQ:
                return true;
            default:
                return false;
        }
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
            case CHOOSE_INDEX_FIRST_WORKER:
                return (Object)convertFromIntToIndex((int)copy.getObject());
            case MOVEMENT:
            default:
                return copy.getObject();

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

            case END_GAME:
                this.toSend = new Message(messageType, toSend);
                this.received = new Message(messageType, "CloseTheGame - connection error");
                break;

//            case MOVE_AGAIN:
//            case BUILD_AGAIN:
//            case GET_NAME:
//            case BUILD_DOME:
//            case PLAYER_LOST:
//            case PLAYER_WON:
//            case MOVEMENT:
//            case YOUR_GOD:
//                this.toSend = new Message(messageType,toSend);
//                break;
            default:
                this.toSend = new Message(messageType,toSend);
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
            case END_GAME:
            case PING_IS_ALIVE:
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

    private boolean sentJoinGame(){
        synchronized (receivedLock){
            while(received.getType() != Message.MessageType.JOIN_GAME){
                try{
                    receivedLock.wait();
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
        return true;
    }




    /**
     * controls if we need to wait for game message before sending
     * a certain type of msg
     * It is the list of messages that do not need input from
     * GameManager to be sent
     */
    private void waitForGameMessage(Message.MessageType MsT) {

        if(MsT == Message.MessageType.PING_IS_ALIVE ||
                MsT == Message.MessageType.FINISHED_TURN||
                MsT == Message.MessageType.INFORMATION)
            return;
        isWaitingToSend = true;

        synchronized (gameSideLock){
            gameSideLock.notifyAll();
            try{
                System.out.println("GameSideLock in waitForGameMessage");
                gameSideLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Notified");

        }

    }

}
