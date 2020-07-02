package View.GUIMode;

import Controller.Communication.Message;
import View.CustomComponent.CellButton;
import View.Interfaces.PlayerManager;
import View.UserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GuiPlayerManager implements PlayerManager {

    private JPanel panel;
    private JPanel infoPanel;
    private JPanel godPanel;

    private JLabel infoLabel;
    private String input;
    private CellButton[] cells;
    //private int[] validCells;
    private UserInterface ui;
    private int idFirstWorker;
    private String godName;
    private String godDescription;
    private int howManyGodsShown = 0;
    private boolean isEveryInputValid = true;

    private String name;

    public GuiPlayerManager(UserInterface ui){
        panel = new JPanel();
        infoPanel = new JPanel();
        godPanel = new JPanel();
        godPanel.setLayout(new GridLayout(3,1));
        infoPanel.setSize(500, 60);
        infoLabel = new JLabel(" ");
        this.ui = ui;
    }

    @Override
    public void getServerIp() {
        prepareTextInputPanel(LABEL_SERVER_IP);
    }

    @Override
    public void getName() {
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public void setIdFirstWorker(int idFirstWorker) {
        this.idFirstWorker = idFirstWorker;
    }

    @Override
    public void chooseNumberPlayers() {
        isEveryInputValid = false;
        prepareTextInputPanel(LABEL_NUMBER_PLAYERS);
    }

    /**
     * Set up the map on the GUI
     * @param val the island state given by the server
     */
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(firstWorker)
                    prepareUpperText(LABEL_FIRST_WORKER);
                else
                    prepareUpperText(LABEL_SECOND_WORKER);
                for(int i=0; i<possiblePositions.length; i++){
                    cells[possiblePositions[i]].addActionListener(new CellListener(possiblePositions[i]));
                }
            }
        });
    }

    @Override
    public void chooseWorker(int workers) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                prepareUpperText(LABEL_CHOOSE_WORKER_TO_MOVE);
                if(workers==3){
                    for(int i=0; i<25; i++){
                        if(cells[i].idPlayerWorker(idFirstWorker)!=-1) {
                            cells[i].setSelectable(true);
                            cells[i].addActionListener(new CellListener(cells[i].idPlayerWorker(idFirstWorker)));
                            cells[i].addActionListener(e -> ((CellButton) e.getSource()).setSelected());
                        }
                    }
                }
                else
                    ui.receivedUiInput(workers);
            }
        });
    }

    @Override
    public void chooseMovement(int[] movements) {
        prepareCellInput(LABEL_CHOOSE_WHERE_TO_MOVE, movements);
    }

    @Override
    public void chooseBuilding(int[] buildings) {
        prepareCellInput(LABEL_CHOOSE_WHERE_TO_BUILD, buildings);
    }

    /**
     * Method that prepares the map for an input on the cell (selecting a worker, moving, building)
     * @param msg the string to print on screen to tell the player what to do
     * @param moves the consented cells that can be clicked
     */
    private void prepareCellInput(String msg, int[] moves){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                removeCellListeners();
                prepareUpperText(msg);
                for(int i=0; i<25; i++){
                    cells[i].setSelectable(false);
                }
                for(int i=0; i<moves.length; i++){
                    cells[moves[i]].setSelectable(true);
                    cells[moves[i]].addActionListener(new CellListener(moves[i]));
                }
            }
        });
    }

    @Override
    public void buildDome() {
        Object[] options = {"Building", "Dome"};
        int n = JOptionPane.showOptionDialog(panel, LABEL_BUILD_DOME, null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,null);
        if(n==0)
            ui.receivedUiInput("BUILDING");
        else
            ui.receivedUiInput("DOME");
    }

    @Override
    public void showGods(String[] god, String owner) {

        JPanel thisGod = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String godName = god[0];

        ImageIcon image = new ImageIcon(getClass().getResource("/godCards/"+godName+".png"));
        Image scaledImg = image.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        JLabel godLabel = new JLabel(new ImageIcon(scaledImg));
        thisGod.add(godLabel);

        String godDescription = god[1];
        godDescription = godDescription.replaceAll("\n", "<br>");
        JLabel l;
        if(this.name.equals(owner.toUpperCase()))
            l = new JLabel("<html>"+LABEL_YOUR_GOD + godName + LABEL_YOUR_GOD_DESC + godDescription+"</html>");
        else
            l = new JLabel("<html>"+owner+"'s God is:" + godName + LABEL_YOUR_GOD_DESC + godDescription+"</html>");
        switch(howManyGodsShown){
            case 0:
                l.setForeground(Color.blue);
                break;
            case 1:
                l.setForeground(Color.green);
                break;
            case 2:
                l.setForeground(Color.red);
                break;
        }
        thisGod.add(l);
        godPanel.add(thisGod);


        howManyGodsShown++;
        SwingUtilities.updateComponentTreeUI(godPanel);
    }

    @Override
    public void doItAgain(Message.MessageType moveAgain) {
        String msg = LABEL_MOVE_AGAIN;
        if(moveAgain == Message.MessageType.MOVE_AGAIN){
            msg += "Move again?";
        } else if(moveAgain == Message.MessageType.BUILD_AGAIN){
            msg += "Build again?";
        }
        int n = JOptionPane.showOptionDialog(panel, msg, null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,null,null);
        if(n==0)
            ui.receivedUiInput("YES");
        else
            ui.receivedUiInput("NO");
    }

    @Override
    public void buildBefore() {
        int n = JOptionPane.showOptionDialog(panel, LABEL_BUILD_BEFORE, null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,null,null);
        if(n==0)
            ui.receivedUiInput("YES");
        else
            ui.receivedUiInput("NO");
    }

    @Override
    public void showTurn(String object) {

    }

    @Override
    public void getName(String object) {
        prepareTextInputPanel(object);

    }

    @Override
    public void buildOtherWorker() {
        int n = JOptionPane.showOptionDialog(panel, LABEL_POSEIDON_POWER, null, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,null,null);
        if(n==0)
            ui.receivedUiInput("YES");
        else
            ui.receivedUiInput("NO");

    }

    @Override
    public void printLoser(String object) {
        SwingUtilities.invokeLater(() -> {
            prepareUpperText(object.toUpperCase() + LABEL_OTHER_PLAYER_LOST);
        });
    }

    /**
     * Getter for the main panel (contening either the text input panel or the island)
     * @return the main panel
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * Getter for the upper panel that gives to the player infos on what's happening/what it has to do
     * @return the info panel
     */
    public JPanel getInfoPanel() {
        return infoPanel;
    }

    /**
     * Getter for the right panel that shows the gods of the players
     * @return the god panel
     */
    public JPanel getGodPanel() {
        return godPanel;
    }

    /**
     * Method that prepares a GUI with just a text to tell the player what to write, a text field to write it, a submit button
     * Used before starting a match to connect to the server, give the username, tell how many players there will be
     * @param labelText the text displayed on the label to tell the player what to write
     */
    private void prepareTextInputPanel(String labelText){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                panel.removeAll();
                JLabel label = new JLabel(labelText);
                panel.add(label);
                JTextField tf = new JTextField(10);
                panel.add(tf);
                JButton submit = new JButton("SUBMIT");
                submit.addActionListener(new TextInputListener(tf));
                panel.add(submit);
                SwingUtilities.updateComponentTreeUI(panel);
            }
        });
    }

    /**
     * Method that displays a text on the upper panel
     * @param labelText the text to display
     */
    private void prepareUpperText(String labelText){
        SwingUtilities.updateComponentTreeUI(infoPanel);
        infoPanel.removeAll();
        infoLabel = new JLabel(labelText);
        infoPanel.add(infoLabel);

    }

    /**
     * Invoked when the game ends or the enemy players is disconnected
     */
    public void endGame(){
        SwingUtilities.invokeLater(() -> {
            prepareUpperText("Match ended");
        });
    }

    /**
     * Method that removes the listeners on every cell after one of them is pressed
     */
    private void removeCellListeners(){
        for(int i=0; i<25; i++){
            for(int o=0; o<cells[i].getActionListeners().length; o++) {
                cells[i].removeActionListener(cells[i].getActionListeners()[o]);
            }
        }
    }

    /**
     * Method called by the TextInputListener once the submit button is pressed and the input is validated
     * Calls UserInterface to send the message to the server
     * @param input the content of the JTextField
     */
    private void textInput(String input){
        this.input = input;
        SwingUtilities.invokeLater(() -> {
            panel.removeAll();
            JLabel label = new JLabel("Waiting...");
            panel.add(label);
            SwingUtilities.updateComponentTreeUI(panel);
        });
        ui.receivedUiInput(input);
    }

    /**
     * The Listener given to the submit button of the TextInputPanel
     */
    class TextInputListener implements ActionListener{

        JTextField tf;
        public TextInputListener(JTextField tf){
            this.tf = tf;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String input = tf.getText();
            if(isEveryInputValid || Integer.parseInt(input)==2 || Integer.parseInt(input)==3)
                textInput(tf.getText());

        }
    }

    /**
     * The Listener given to the single cell button
     * Once clicked send the input to the server
     */
    class CellListener implements ActionListener{
        private int cellNumber;
        public CellListener(int cellNumber){
            this.cellNumber = cellNumber;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            removeCellListeners();
            ui.receivedUiInput(cellNumber);
            prepareUpperText("Waiting...");
        }
    }
}
