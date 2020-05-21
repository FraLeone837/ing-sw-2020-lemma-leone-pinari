package View;

import Controller.God;

import javax.swing.*;

import static View.PlayerManager.LABEL_YOUR_GOD;
import static View.PlayerManager.LABEL_YOUR_GOD_DESC;

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
        panel.removeAll();
        JLabel l = new JLabel("Corrispondere iniziare");
        panel.add(l);
    }

    @Override
    public void printReadyToStart(boolean starting, God assigned) {

    }

    @Override
    public void updateMap(int[] island) {

    }

    @Override
    public void showGod(String[] god) {
        System.out.println("AAAAAAAAAA");
        panel.removeAll();
        String godName = god[0];
        String godDescription = god[1];
        JLabel l = new JLabel(LABEL_YOUR_GOD + godName + LABEL_YOUR_GOD_DESC + godDescription);
        panel.add(l);
        SwingUtilities.updateComponentTreeUI(panel);
    }

    @Override
    public void printWin(boolean win) {

    }

    public JPanel getPanel(){
        return panel;
    }
}
