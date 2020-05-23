package Controller.Communication;

import java.util.concurrent.TimeUnit;

public class Timer implements Runnable {
    private int currentSecond;
    private IntermediaryClass intermediaryClass;
    private Object lock = new Object();
    private CommunicationProxy communicationProxy;

    public Timer(int currentSecond, IntermediaryClass intermediaryClass, CommunicationProxy communicationProxy){
        this.currentSecond = currentSecond;
        this.intermediaryClass = intermediaryClass;
        this.communicationProxy = communicationProxy;
    }

    public void setCurrentSecond(int currentSecond) {
        this.currentSecond = currentSecond;
    }

    @Override
    public void run() {
        waitForSeconds();
    }

    /**
     * if it ever goes to -1, it interrupts the game
     */
    private synchronized void waitForSeconds() {
        while(currentSecond >= 0){
            //there have been currentSecond seconds until last message
            myTurnCommunicating(communicationProxy.getToSend());
            currentSecond--;

            try{
                wait(1*1000);
            }catch (InterruptedException e){

            }
        }
        terminateGame();
    }

    //shows if
    private void myTurnCommunicating(Message toSend) {
        while(toSend.getType() == Message.MessageType.ZZZ){
            synchronized (lock){
                try{
                    lock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }


    public void terminateGame(){
        if(communicationProxy != null)
        this.communicationProxy.interruptGame(Message.MessageType.END_GAME,"Connection timed-out");
    }
}
