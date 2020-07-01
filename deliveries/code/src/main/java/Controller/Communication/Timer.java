package Controller.Communication;


import Controller.Communication.CommunicationProxy;
import Controller.Communication.IntermediaryClass;
import Controller.Communication.Message;

public class Timer implements Runnable {
    private int currentSecond;
    boolean myTurn ;
    boolean debugging = true;
    private IntermediaryClass intermediaryClass;
    private CommunicationProxy communicationProxy;
    public Timer(int currentSecond, IntermediaryClass intermediaryClass, CommunicationProxy communicationProxy){
        this.currentSecond = currentSecond;
        this.intermediaryClass = intermediaryClass;
        this.communicationProxy = communicationProxy;
        myTurn = false;
    }

    public void setCurrentSecond(int currentSecond) {
        this.currentSecond = currentSecond;
    }

    @Override
    public void run() {
        waitForSeconds();
    }

    /**
     * notify that we are waiting for a message to be received
     */
    public synchronized void notifyWait(){
        myTurn = true;
        notifyAll();
        System.out.println("called notify wait");
    }

    /**
     * notify that we received the message for which we were waiting
     * @param currentSecond is the duration of the new time to wait before interrupting game
     */
    public synchronized void notifyReceived(int currentSecond){
        System.out.println("Called notify received");
        myTurn = false;
        setCurrentSecond(currentSecond);
        notifyAll();
    }
    /**
     * if it ever goes to -1, it interrupts the game
     */
    private synchronized void waitForSeconds() {
        while(currentSecond >= 0){
            //there have been currentSecond seconds until last message
            myTurnCommunicating();
            currentSecond--;

            try{
                wait(1*1000);
            }catch (InterruptedException e){

            }
            if(debugging)
            System.out.println("Time left: " + currentSecond);
        }
        terminateGame();
    }

    //shows if
    // waits forever
    private synchronized void myTurnCommunicating() {
        while(!myTurn){
                try{
                    this.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
    }


    public void terminateGame(){
        if(communicationProxy != null){
            System.out.println("No interrupt game");
            this.communicationProxy.interruptGame(Message.MessageType.END_GAME,"Connection timed-out");
            System.out.println("No send message");
            this.communicationProxy.sendMessage(Message.MessageType.END_GAME, new Object(), "Connection timed-out");
            System.out.println("No terminate game");
            this.intermediaryClass.terminateGame();
        }
    }
}
