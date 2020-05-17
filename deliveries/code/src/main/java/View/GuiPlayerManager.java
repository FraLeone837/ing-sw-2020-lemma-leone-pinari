package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiPlayerManager implements PlayerManager{

    private JPanel panel;
    private String input;
    private JButton[] cells;
    private int[] validCells;
    private UserInterface ui;
    private String godName;
    private String godDescription;

    public GuiPlayerManager(UserInterface ui){
        panel = new JPanel();
        this.ui = ui;
    }

    @Override
    public void getServerIp() {
        prepareTextInputPanel(LABEL_SERVER_IP);
    }

    @Override
    public void getName() {
        prepareTextInputPanel(LABEL_USERNAME);
    }

    @Override
    public void chooseNumberPlayers() {
        prepareTextInputPanel(LABEL_NUMBER_PLAYERS);
    }

    public void setUpMap(){
        cells = new JButton[25];
        panel.setLayout(new GridLayout(5, 5));
        for(int i=0; i<25; i++){
            JButton b = new JButton("Button "+ i);
            b.addActionListener(new CellListener(i));
            cells[i] = b;
            panel.add(b);
        }
    }

    @Override
    public void placeWorker(boolean firstWorker, int[] possiblePositions) {
        validCells = possiblePositions;
    }

    @Override
    public void chooseWorker(int workers) {
        /*hmm da decidere*/
    }

    @Override
    public void chooseMovement(int[] movements) {
        validCells = movements;
    }

    @Override
    public void chooseBuilding(int[] buildings) {
        validCells = buildings;
    }

    @Override
    public void buildDome() {

    }

    @Override
    public void showGods(String[] god) {

    }

    public JPanel getPanel() {
        return panel;
    }

    private void prepareTextInputPanel(String labelText){
        panel.removeAll();
        JLabel label = new JLabel(labelText);
        panel.add(label);
        JTextField tf = new JTextField(10);
        panel.add(tf);
        JButton submit = new JButton("SUBMIT");
        submit.addActionListener(new TextInputListener(tf));
        panel.add(submit);
        SwingUtilities.updateComponentTreeUI(panel);
        JButton stampo = new JButton("STAMPO");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                stampo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("EEEEEEE MACARENA");
                    }
                });
            }
        });
        panel.add(stampo);
    }


    private void textInput(String input){
        this.input = input;
        ui.receivedUiInput(input);
    }

    private boolean cellInput(int cellNumber){
        for(int i=0; i<validCells.length; i++){
            if(validCells[i]==cellNumber){
                System.out.println("correct input "+ input);
                return true;
            }
        }
        System.out.println("invalid input");
        return false;
    }

    class TextInputListener implements ActionListener{

        JTextField tf;
        public TextInputListener(JTextField tf){
            this.tf = tf;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            textInput(tf.getText());
        }
    }

    class CellListener implements ActionListener{
        private int cellNumber;
        public CellListener(int cellNumber){
            this.cellNumber = cellNumber;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            cellInput(cellNumber);
        }
    }
}
