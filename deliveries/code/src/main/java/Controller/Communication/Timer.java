package Controller.Communication;

import java.util.concurrent.TimeUnit;

public class Timer implements Runnable {
    private int currentSecond;
    private IntermediaryClass intermediaryClass;
    private static final int resendTime = 7;
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
            currentSecond--;
            //resend message every 7 seconds
            if(currentSecond % resendTime == 0){
                resendLastMessage();
            }
            try{
                wait(1*1000);
            }catch (InterruptedException e){

            }
        }
        terminateGame();
    }

    public void resendLastMessage(){
        this.communicationProxy.resendLastMessage();
    }

    public void terminateGame(){
        this.communicationProxy.interruptGame(Message.MessageType.END_GAME,"Connection timed-out");
    }
}
