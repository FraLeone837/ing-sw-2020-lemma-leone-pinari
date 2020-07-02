package View.GUIMode;

import Controller.Gods.God;
import View.Interfaces.GameManager;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

import static View.Interfaces.PlayerManager.LABEL_YOUR_GOD;
import static View.Interfaces.PlayerManager.LABEL_YOUR_GOD_DESC;

public class GuiGameManager implements GameManager {

    private JPanel panel;

    public GuiGameManager(){
        panel = new JPanel();
    }

    /**
     * UNUSED ON GUI
     */
    @Override
    public void startMatch() {

    }

    @Override
    public void waitForPlayer() {
        panel.removeAll();
        JLabel l = new JLabel("Corrispondere iniziare");
        panel.add(l);
        SwingUtilities.updateComponentTreeUI(panel);
    }

    /**
     * UNUSED ON GUI
     * @param starting true if the player on this client is the first to start
     * @param assigned the randomly chosen god for the player
     */
    @Override
    public void printReadyToStart(boolean starting, God assigned) {

    }

    /**
     * UNUSED ON GUI, handled by PlayerManager
     * @param island the updated map
     */
    @Override
    public void updateMap(int[] island) {

    }

    @Override
    public void showGod(String[] god) {
        panel.removeAll();
        SwingUtilities.updateComponentTreeUI(panel);
    }

    @Override
    public void printWin(boolean win) {
        SwingUtilities.invokeLater(() -> {
            panel.removeAll();
            JLabel l;
            if(win)
                l = new JLabel("You win!");
            else
                l = new JLabel("You lose.");
            panel.add(l);
            SwingUtilities.updateComponentTreeUI(panel);
        });

    }

    /**
     * Getter method for the panel of the GameManager
     * @return the panel
     */
    public JPanel getPanel(){
        return panel;
    }
}
