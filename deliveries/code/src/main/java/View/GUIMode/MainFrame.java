package View.GUIMode;

import javax.swing.*;
import java.awt.*;

public class MainFrame {
    private JFrame frame;

    public MainFrame(){
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
    }

    public void setPlayerManagerPanel(JPanel panel){
        frame.add(panel, BorderLayout.CENTER);
    }
    public void setTopGameManagerPanel(JPanel panel){
        frame.add(panel, BorderLayout.NORTH);
    }
    public void setBottomGameManagerPanel(JPanel panel){
        frame.add(panel, BorderLayout.SOUTH);
    }

    public void show(){
        //frame.pack();
        frame.setSize(500, 650);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }
}
