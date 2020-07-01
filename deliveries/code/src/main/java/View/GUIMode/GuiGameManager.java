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

    @Override
    public void printReadyToStart(boolean starting, God assigned) {

    }

    @Override
    public void updateMap(int[] island) {

    }

    @Override
    public void showGod(String[] god) {
        String godName = god[0];

        panel.removeAll();
        //panel.setLayout(new GridLayout());
        ImageIcon image = new ImageIcon(getClass().getResource("/godCards/"+godName+".png"));
        Image scaledImg = image.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel godLabel = new JLabel(new ImageIcon(scaledImg));
        //godLabel.setBounds(30, 30, 40, 40);
        panel.add(godLabel);


        String godDescription = god[1];
        godDescription = godDescription.replaceAll("\n", "<br>");
        System.out.println(godDescription);
        JLabel l = new JLabel("<html>"+LABEL_YOUR_GOD + godName + LABEL_YOUR_GOD_DESC + godDescription+"</html>");
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
