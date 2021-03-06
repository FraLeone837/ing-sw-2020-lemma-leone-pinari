package View.CustomComponent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.IOException;

public class CellButton extends JButton {

    private int level;
    private int worker;

    public CellButton(int val){
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(null);
        setLayout(null);
        setBackground(null);
        setOpaque(false);

        level = val % 10;
        worker = val / 10;

        checkDome();
        checkWorker();

        setLevel(level);
    }

    /**
     * Check whether there is a dome (level>3)
     * if so, invoke the method setDome
     */
    private void checkDome(){
        if(level>3) {
            level -= 4;
            setDome();
        }
    }

    /**
     * Invoked by checkDome()
     * Create a JLabel with the image of the dome and add it to the button
     */
    private void setDome(){
        ImageIcon image;
        Image scaledImg;
        image = new ImageIcon(getClass().getResource("/Buildings/Dome.png"));
        scaledImg = image.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel dome = new JLabel(new ImageIcon(scaledImg));
        dome.setBounds(40, 40, 20, 20);
        add(dome);
    }

    /**
     * Check whether there is a worker (worker!=0)
     * if so, invoke the method setWorker()
     */
    private void checkWorker(){
        if(worker!=0)
            setWorker();
    }

    /**
     * Invoked by checkWorker()
     * Create a JLabel with the image of the worker and add it to the button
     */
    private void setWorker(){
        ImageIcon image;
        Image scaledImg;
        /*JLabel workerIdLabel = new JLabel(Integer.toString(worker));
        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/LillyBelle.ttf"));
            Font biggerFont = font.deriveFont(Font.BOLD, 20f);
            workerIdLabel.setFont(biggerFont);
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        workerIdLabel.setForeground(Color.white);
        workerIdLabel.setBounds(45, 40, 20, 20);
        add(workerIdLabel);*/
        image = new ImageIcon(getClass().getResource("/Worker/player"+((worker-1)/2)+".png"));
        scaledImg = image.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel workerLabel = new JLabel(new ImageIcon(scaledImg));
        workerLabel.setBounds(20, 20, 60, 60);
        add(workerLabel);
    }

    /**
     * Add, one on top of the others, the images of the buildings to the button
     * @param level the top level on which there's a building
     */
    private void setLevel(int level){
        ImageIcon image;
        Image scaledImg;
        switch (level){
            case 3:
                image = new ImageIcon(getClass().getResource("/Buildings/BuildingBlock03.png"));
                scaledImg = image.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel buildingThirdLevel = new JLabel(new ImageIcon(scaledImg));
                buildingThirdLevel.setBounds(30, 30, 40, 40);
                add(buildingThirdLevel);
            case 2:
                image = new ImageIcon(getClass().getResource("/Buildings/BuildingBlock02.png"));
                scaledImg = image.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                JLabel buildingSecondLevel = new JLabel(new ImageIcon(scaledImg));
                buildingSecondLevel.setBounds(20, 20, 60, 60);
                add(buildingSecondLevel);
            case 1:
                image = new ImageIcon(getClass().getResource("/Buildings/BuildingBlock01.png"));
                scaledImg = image.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                JLabel buildingFirstLevel = new JLabel(new ImageIcon(scaledImg));
                buildingFirstLevel.setBounds(10, 10, 80, 80);
                add(buildingFirstLevel);
                break;
        }
    }

    /**
     * Check whether the worker on this cell is owned by the player on this client
     * @param firstWorker the id of the first worker owned by the player on this client (can be 1,3,5)
     * @return 1 if it's the first worker, 2 if the second, -1 if there isn't any worker of if it's owned by
     */
    public int idPlayerWorker(int firstWorker){
        if(worker==firstWorker)
            return 1;
        if(worker==firstWorker+1)
            return 2;
        return -1;
    }

    /**
     * Add a border to the cell if there is a listener on it
     * @param selectable used to decide whether the cell can be clicked or not (so you can move/build on it)
     */
    public void setSelectable(boolean selectable){
        if(selectable){
            SwingUtilities.updateComponentTreeUI(this);
            setBackground(Color.yellow);
            setOpaque(true);
        }
        else{
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorder(null);
            setLayout(null);
            setBackground(null);
            setOpaque(false);
        }
    }

    /**
    * if the cell is clicked set his background color to red
     */
    public void setSelected(){
        SwingUtilities.updateComponentTreeUI(this);
        setBackground(Color.red);
    }
}
