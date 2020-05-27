package View;

import Controller.Communication.Message;

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
            JButton l = new JButton();
            //l.setBackground(null);
            l.setBorder(null);
            //JPanel p = new JPanel();
            l.setLayout(null);
            //JButton b = new JButton("Button "+ i);
            JLabel b = new JLabel("Button");
            b.setBounds(0, 0, 100, 100);
            //JButton b2 = new JButton("Janson "+ i);
            JLabel b2 = new JLabel("Janson");
            b2.setBounds(20, 20, 80, 80);
            //b.addActionListener(new CellListener(i));
            //cells[i] = p;
            l.add(b);
            l.add(b2);
            l.setComponentZOrder(b, 1);
            l.setComponentZOrder(b2, 0);
            l.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("lol");
                }
            });
            cells[i] = l;
            panel.add(cells[i]);
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

    @Override
    public void doItAgain(Message.MessageType moveAgain) {

    }

    @Override
    public void buildBefore() {

    }

    @Override
    public void showTurn(String object) {

    }

    @Override
    public void getName(String object) {

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
        /*JButton stampo = new JButton("STAMPO");
        stampo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("EEEEEEE MACARENA");
            }
        });
        panel.add(stampo);*/
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
