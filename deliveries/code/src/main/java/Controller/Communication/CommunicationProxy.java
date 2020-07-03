package Controller.Communication;

import Controller.Gods.God;
import Model.Index;

import java.util.ArrayList;

import static Controller.Communication.ClientHandler.*;
import static Controller.Communication.Message.MessageType.*;

/**
 * to be called from every matchManager as a way to send and successively receive messages
 * from client-side
 * uses observer and observable design pattern
 */
public class CommunicationProxy implements Runnable, MessageObservers {

    private boolean debugging = true;

    private ClientHandler clientHandler;
    //serves for methods referring to matchManager
    private IntermediaryClass ic;

    //Message updated from matchManager
    private Message toSend;
    //isWaitingToReceive is true iff there is a message being sent , and we have not received yet the response
    private boolean isWaitingToReceive;

    //Message to be requested from matchManager
    private Message received = new Message(Message.MessageType.ZZZ,"have not received anything");
    private boolean acceptInput;
    private boolean disconnected = false;

    /**
     * @param cl not null
     * @param ic not null
     */
    public CommunicationProxy(ClientHandler cl, IntermediaryClass ic ){
        this.clientHandler = cl;
        this.ic = ic;
        this.acceptInput = true;
        this.isWaitingToReceive = true;
        this.toSend = new Message(Message.MessageType.ZZZ,"waiting for input");
        clientHandler.addObserver(this);
    }

    /**
     * message observers abstract method
     */
    @Override
    public synchronized void receivedMessage() {
        received = clientHandler.getCurrentMessage();
        notifyAll();
        if(!acceptInput)
            received.setType(Message.MessageType.YYY);
    }
    private synchronized void receivedMessage(Message msg) {
        received = msg;
        notifyAll();
        if(!acceptInput)
            received.setType(Message.MessageType.YYY);
    }

    /**
     * after having sent a message we wait for x seconds (counted by the timer)
     * or until the
     */
    private synchronized void waitForReceiveMessage(){
        while(this.received.getType() == Message.MessageType.YYY && !disconnected){
            try{
                if(debugging)
                System.out.println("Waiting for a messg");
                    wait();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        isWaitingToReceive = false;
        notifyAll();
    }

    //wait for specific message
    private synchronized void waitForReceiveMessage(Message.MessageType mt){
        while(this.received.getType() != mt){
            try{
                wait();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        isWaitingToReceive = false;
        notifyAll();
    }

    @Override
    public void run() {
        handleConnection();
    }

    public void removeIntermediaryClass(){
        this.ic = null;
    }

    /**
     * is always asleep, wakes up only when matchManager asks them to wake up
     * or when a message is received and forwards it to matchManager
     */
    private synchronized void handleConnection(){
        if(ic != null)
        ic.setCommunicationProxy(this);
        received.setType(Message.MessageType.YYY);
        waitForReceiveMessage(JOIN_GAME);
        while(true){

            /*
             * messages to be sent automatically without any need of input from client/controller
             */
            Message.MessageType typeCopy = received.getType();

            if(typeCopy == END_GAME ||
                    typeCopy == PLAYER_LOST ||
                    typeCopy == PLAYER_WON ||
                    disconnected){
                if(disconnected && ic != null) {
                    ic.terminateGame();
                }
                if(debugging)
                    System.out.println(ANSI_PURPLE + "Exiting from comm proxy " + getClientHandler().getName() + ANSI_RESET);
                return;
            }
            toSend = new Message(Message.MessageType.ZZZ, "Waiting");
            //changes message toSend to ZZZ and waits until gamesidelock.notifyAll() updates toSend
            waitForGameMessage();


            //sets msg toSend and notifies client handler
            clientHandler.setToSendMsg(toSend);

            received.setType(YYY);
            waitForReceiveMessage();

        }


    }

    /**
     * controls if we need to wait for game message before sending
     * a certain type of msg
     * It is the list of messages that do not need input from
     * GameManager to be sent
     */
    private synchronized void waitForGameMessage() {
            if(debugging)
            System.out.println("notified gamesidelock " + this.clientHandler.getName());

            notifyAll();
            while(toSend.getType() == Message.MessageType.ZZZ && !disconnected){
                try{
                    wait();
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
        /*
         * stops execution while
         * we have not yet received a message
         * sets waiting to receive to true and gets lock
         */
        canSendMessage();
        /*
         * prepare to send a message
         */
        synchronized (this){

            //converts gameSideMessage a.k.a toSend into a certain message type
            convertToMessage(messageType,toSend);

            //releases lock and notifies that object has changed
            notifyAll();
            if(debugging)
                System.out.println("What Comm proxy sent: "+ messageType + "  " + this.clientHandler);

        Message copy;
        if(messageType == END_GAME){
            received = new Message(END_GAME,"Ending");
            return new Object();
        }


        /*
         * prepare to receive message
         */
            if(debugging)
                System.out.println("Comm proxy waiting for response for " + messageType);
            while(received.getType() != messageType){
                if(disconnected)
                    break;
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            if(debugging)
                System.out.println("What Comm proxy received: " + received + " " + this.clientHandler);
            copy = new Message(received);
            Object c = convertToSpecificObject(copy);
            //accepts at most 3 players
            if(copy.getType() == NUMBER_PLAYERS && (int)c == 3 && ic != null){
                ic.changeNoOfPlayers();
            }
            received = new Message(ZZZ,"waiting");
            return c;
        }
    }

    private void canSendMessage() {
        synchronized (this){
            while(isWaitingToReceive || disconnected){
                if(debugging)
                System.out.println("asking if i can send message");
                try{
                    wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            if(debugging)
            System.out.println("Now I can!!");
            this.isWaitingToReceive = true;
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
            case CHOOSE_INDEX_FIRST_WORKER:
            case CHOOSE_INDEX_SEC_WORKER:
            case MOVE_INDEX_REQ:
            case BUILD_INDEX_REQ:
            case INFORMATION:
                int toReturnx = ((Double)copy.getObject()).intValue();
                return convertFromIntToIndex(toReturnx);
            case NUMBER_PLAYERS:
                return ((Double)copy.getObject()).intValue();
            case MOVEMENT:
                return ((Double)copy.getObject()).intValue() % 2;
            default:
                return copy.getObject();

        }
        //(Integer.parseInt((String)copy.getObject())) converts the object from string sent by json into integer
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
                Index[] toConvert = new Index[((ArrayList<Index>)toSend).size()];
                for(int i=0; i<toConvert.length;i++){
//                    System.out.println(ANSI_PURPLE + "INDEX IS:" +((ArrayList<Index>)toSend).get(i) + ANSI_RESET);
                    toConvert[i] = ((ArrayList<Index>)toSend).get(i);
                }
                this.toSend = new Message(messageType,convertFromIndexToInts(toConvert));
                break;

            case GAME_START:
                this.toSend = new Message(messageType, convertFromPlayerIDtoInt((int) toSend));
                break;

            case END_GAME:
                this.toSend = new Message(messageType, toSend);
                this.received = new Message(messageType, "CloseTheGame - connection error");
                break;
            default:
                this.toSend = new Message(messageType,toSend);
        }

    }


    /**
     * orders any index into a number of 0-24
     * @param number 0-24 indicating A1,A2 ecc.
     * @return The "right" (x,y) copy of Index. Caller needs to check the height
     */
    private Object convertFromIntToIndex(int number) {
        int x = number % 5;
        int y = (number - x)/5;
        Index ix = new Index(x,y,-1);
        return ix;
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
     * @param name is the name of the owner of the God
     * @return not null
     */
    public String[] godDescription(God god, String name){
        String[] x = new String[3];
        x[0] = god.getName();
        x[1] = god.getDescription();
        x[2] = name;
        return x;
    }



    public ClientHandler getClientHandler() {
        return clientHandler;
    }


    /**
     * method called when connection has been dropped
     */
    public void interruptGame(){
        if(debugging)
        System.out.println("Interrupting Game");
        //inform all other players that they have been disconnected
        synchronized (this){

            receivedMessage(new Message(toSend.getType(), "...."));
            if(debugging) {
                System.out.println("Set disconnected");
            }
            setDisconnected(true);
        }
        if(debugging)
                System.out.println("Received fake message, calling terminate game.");

        if(debugging)
            System.out.println("Done");
    }

    IntermediaryClass getIC() {
        return ic;
    }

    void setDisconnected(boolean disconnected){
        this.disconnected = disconnected;
        synchronized (this){
            notifyAll();
        }
    }

    /**
     * send message method for interruptions
     * @param type is always END_GAME
     * @param object is not important
     * @param disconnected reason of disconnection
     */
    public void sendMessage(Message.MessageType type, Object object, String disconnected) {
        this.toSend = new Message(type,"Player disconnected " + disconnected);
        if(clientHandler != null)
        clientHandler.setToSendMsg(this.toSend);
        synchronized (this){
            notifyAll();
        }
    }
}
