package View;

import Controller.Communication.Message;
import View.CustomComponent.CellButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiPlayerManager implements PlayerManager{

    private JPanel panel;
    private String input;
    private CellButton[] cells;
    //private int[] validCells;
    private UserInterface ui;
    private int idFirstWorker;
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

    public void setUpMap(int val[]){
        SwingUtilities.updateComponentTreeUI(panel);
        panel.removeAll();
        cells = new CellButton[25];
        panel.setLayout(null);
        JPanel gridPanel = new JPanel();
        panel.setSize(500, 500);
        gridPanel.setLayout(new GridLayout(5, 5));
        for(int i=0; i<25; i++){
            cells[i] = new CellButton(val[i]);

            cells[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("lol");
                            setUpMap(val);
                        }
                    });
                }
            });
            gridPanel.add(cells[i]);
        }
        gridPanel.setLocation(0, 0);
        gridPanel.setSize(500, 500);
        gridPanel.setBackground(null);
        gridPanel.setOpaque(false);
        ImageIcon image = new ImageIcon(getClass().getResource("/ResizedCliff.png"));
        Image scaledImg = image.getImage().getScaledInstance(500, 500, Image.SCALE_SMOOTH);
        JLabel mapLabel = new JLabel(new ImageIcon(scaledImg));
        mapLabel.setLocation(0, 0);
        mapLabel.setSize(panel.getSize());
        panel.add(gridPanel);
        panel.add(mapLabel);
    }

    @Override
    public void placeWorker(boolean firstWorker, int[] possiblePositions) {
        for(int i=0; i<possiblePositions.length; i++){
            cells[possiblePositions[i]].addActionListener(new CellListener(possiblePositions[i]));
        }
    }

    @Override
    public void chooseWorker(int workers) {
        if(workers==3){
            for(int i=0; i<25; i++){
                if(cells[i].idPlayerWorker(idFirstWorker)!=-1)
                  cells[i].addActionListener(new CellListener(cells[i].idPlayerWorker(idFirstWorker)));
            }
        }
    }

    @Override
    public void chooseMovement(int[] movements) {
        for(int i=0; i<movements.length; i++){
            cells[movements[i]].addActionListener(new CellListener(movements[i]));
        }
    }

    @Override
    public void chooseBuilding(int[] buildings) {
        for(int i=0; i<buildings.length; i++){
            cells[buildings[i]].addActionListener(new CellListener(buildings[i]));
        }
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

    private void removeCellListeners(){
        for(int i=0; i<25; i++){
            for(int o=0; o<cells[i].getActionListeners().length; o++) {
                cells[i].removeActionListener(cells[i].getActionListeners()[o]);
            }
        }
    }

    private void textInput(String input){
        this.input = input;
        ui.receivedUiInput(input);
    }
/*
    private boolean cellInput(int cellNumber){
        for(int i=0; i<validCells.length; i++){
            if(validCells[i]==cellNumber){
                System.out.println("correct input "+ input);
                return true;
            }
        }
        System.out.println("invalid input");
        return false;
    }*/

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
            //cellInput(cellNumber);
            removeCellListeners();
        }
    }
}
