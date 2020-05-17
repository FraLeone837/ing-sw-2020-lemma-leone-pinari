package View;

import Controller.God;

import javax.swing.*;

public class GuiGameManager implements GameManager{

    private JPanel panel;

    public GuiGameManager(){
        panel = new JPanel();
    }

    @Override
    public void startMatch() {

    }

    @Override
    public void waitForPlayer() {

    }

    @Override
    public void printReadyToStart(boolean starting, God assigned) {

    }

    @Override
    public void updateMap(int[][][] island) {

    }

    @Override
    public void printWin(boolean win) {

    }

    public JPanel getPanel(){
        return panel;
    }
}
