package Controller.Communication;

import java.util.concurrent.TimeUnit;

public class Timer implements Runnable {
    private int currentSecond;
    private IntermediaryClass intermediaryClass;

    public Timer(int currentSecond, IntermediaryClass intermediaryClass){
        this.currentSecond = currentSecond;
        this.intermediaryClass = intermediaryClass;
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
            try{
                wait(1*1000);
            }catch (InterruptedException e){

            }
        }
        intermediaryClass.terminateGame();
    }

    public void updateCurrentSecond(int currentSecond){
        this.currentSecond = currentSecond;
    }
}
