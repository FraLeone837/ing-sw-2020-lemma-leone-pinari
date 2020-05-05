package Controller.Communication;

import Model.Index;

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
//    private final Object sendLock = new Object();

    //Message to be requested from matchManager
    private Message received;
    private final Object receivedLock = new Object();

    private final Object gameSideLock = new Object();
    private Message gameSideMessage;

    /**
     * if id = -1 then it needs to be set up
     * from matchManager
     * @param cl
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
            received = clientHandler.getCurrentMessage();
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
     * is always asleep, awakes only when matchManager asks them to wake up
     * or when a message is received and forwards it to matchManager
     */
    public void handleConnection() throws IOException {
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
            switch(typeCopy){
                case JOIN_GAME:
                    toSend = new Message(Message.MessageType.GET_NAME, "What is your name?");
                    break;
                case GET_NAME:
                    if(!ic.isAnyPlayerConnected())
                        toSend = new Message(Message.MessageType.NUMBER_PLAYERS, "How many players do you want to join?");
                    else toSend = new Message(Message.MessageType.WAIT_START, "Waiting for other player/s to join");
                    gameRunning = false;
                    break;
                case ZZZ:
                    break;
//                case GAME_START:
//                    getGameStart();
//                    toSend = new Message(Message.MessageType.GAME_START, waitForStart());
//                    break;
//                case CHOOSE_INDEX_FIRST_WORKER:
//                    toSend = new Message(Message.MessageType.CHOOSE_INDEX_FIRST_WORKER, waitForIndexList());
//                    break;
//                case CHOOSE_INDEX_SEC_WORKER:
//                    toSend = new Message(Message.MessageType.CHOOSE_INDEX_SEC_WORKER, waitForIndexList());
//                case CHOOSE_WORKER:
//                    waitForManager();
//                    toSend = new Message(Message.MessageType.MOVE_INDEX_REQ, waitForIndexList());
//                    break;
//                case MOVE_INDEX_REQ:
//                    toSend = new Message(Message.MessageType.BUILD_INDEX_REQ, waitForIndexList());
//                    break;
//                case BUILD_INDEX_REQ:
//                    toSend = new Message(Message.MessageType.FINISHED_TURN, "You finished your turn.");
//                    break;
//                case ISLAND_INFO:
//                    break;
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
            MsT == Message.MessageType.GAME_START ||
            MsT == Message.MessageType.JOIN_GAME ||
            MsT == Message.MessageType.FINISHED_TURN)
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

    //useless
    private void getGameStart(){
        synchronized (gameSideLock){
            while(gameSideMessage == null){
                try{
                    gameSideLock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
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
        synchronized (gameSideLock){
            //converts gameSideMessage into a certain message type
            convertToMessage(messageType,toSend);
            //releases lock and notifies that object has changed
            gameSideLock.notifyAll();
        }
        Message copy;
        synchronized (this.receivedLock){
            while(received.getType() != messageType){
                try{
                    receivedLock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            copy = received;
            received.setType(Message.MessageType.ZZZ);
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
            case CHOOSE_INDEX_FIRST_WORKER:
            case MOVEMENT:
            default:
                return copy;

        }
    }

    /**
     * sets this.gameSideMessage to a certain Message according to protocol
     * @param messageType kind of message
     * @param toSend depending on what toSend CommProxy sends a flag or other
     */
    private void convertToMessage(Message.MessageType messageType, Object toSend) {
        switch(messageType){
            case BUILD_INDEX_REQ:

            case CHOOSE_INDEX_FIRST_WORKER:

            case CHOOSE_INDEX_SEC_WORKER:

            case MOVE_INDEX_REQ:


            case GAME_START:
            gameSideMessage = new Message(messageType,toSend);
            break;
            case ISLAND_INFO:
            gameSideMessage = new Message(messageType, clientHandler.getIslandInfo());
            break;
        }

    }

    /**
     *
     * @return a list of index not null
     */
    private Index[] waitForIndexList(){
        List<Index> copy;
        synchronized (gameSideLock){
            while(gameSideMessage.getType() != Message.MessageType.BUILD_INDEX_REQ ||
                  gameSideMessage.getType() != Message.MessageType.MOVE_INDEX_REQ ||
                  gameSideMessage.getType() != Message.MessageType.CHOOSE_INDEX_FIRST_WORKER||
                  gameSideMessage.getType() != Message.MessageType.CHOOSE_INDEX_SEC_WORKER)
            {
                gameSideMessage.setType(Message.MessageType.ZZZ);

                try{
                    gameSideLock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            copy = (List<Index>)gameSideMessage;
            gameSideMessage = null;
        }
        Index[] c = new Index[copy.size()];
        int i = 0;
        for(Index ix : copy){
            c[i] = ix;
            i++;
        }
        return c;
    }
}
