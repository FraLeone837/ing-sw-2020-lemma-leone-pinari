package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameFrame {
    public enum Mode{
        PLACE_WORKER,
        MOVE,
        BUILD
    }
    Mode mode;
    int[] consentedMove = new int[]{1, 5, 18, 23};

    private JFrame frame;

    public GameFrame(){
        prepareGUI();
    }
    public void prepareGUI(){
        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 5));
        for(int i=0; i<25; i++){
            JButton b = new JButton("Button "+ i);
            b.addActionListener(new CellListener(i));
            panel.add(b);
        }
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        //frame.setSize(500, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    private boolean clickInput(int input){
        for(int i=0; i<consentedMove.length; i++){
            if(consentedMove[i]==input){
                System.out.println("correct input "+ input);
                return true;
            }
        }
        System.out.println("invalid input");
        return false;
    }

    class CellListener implements ActionListener{
        private int cellNumber;
        public CellListener(int cellNumber){
            this.cellNumber = cellNumber;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            clickInput(cellNumber);
        }
    }
}
