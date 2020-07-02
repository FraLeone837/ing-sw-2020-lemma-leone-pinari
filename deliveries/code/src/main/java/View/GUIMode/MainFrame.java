package View.GUIMode;

import javax.swing.*;
import java.awt.*;

public class MainFrame {
    private JFrame frame;

    public MainFrame(){
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
    }
    /**
     * Add the panel that handles the interaction with the player to the CENTER of the frame
     * @param panel to panel to be added
     */
    public void setPlayerManagerPanel(JPanel panel){
        frame.add(panel, BorderLayout.CENTER);
    }
    /**
     * Add a panel with game infos to the NORTH of the frame
     * @param panel to panel to be added
     */
    public void setTopGameManagerPanel(JPanel panel){
        frame.add(panel, BorderLayout.NORTH);
    }
    /**
     * Add a panel with game infos to the SOUTH of the frame
     * @param panel to panel to be added
     */
    public void setBottomGameManagerPanel(JPanel panel){
        frame.add(panel, BorderLayout.SOUTH);
    }

    /**
     * Add the panel with the players' gods to the EAST of the frame
     * @param panel to panel to be added
     */
    public void setGodLayout(JPanel panel){
        frame.add(panel, BorderLayout.EAST);
    }

    /**
     * Set the frame appareance and show it
     */
    public void show(){
        frame.setSize(900, 650);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }
}
