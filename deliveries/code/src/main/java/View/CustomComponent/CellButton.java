package View.CustomComponent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class CellButton extends JButton {

    private int level;
    private int worker;

    public CellButton(int val){
        setBorder(null);
        setLayout(null);
        setBackground(null);

        level = val % 10;
        worker = val / 10;

        checkDome();
        checkWorker();

        setLevel(level);
    }

    private void checkDome(){
        if(level>3) {
            level -= 4;
            setDome();
        }
    }

    private void setDome(){
        ImageIcon image;
        Image scaledImg;
        image = new ImageIcon(getClass().getResource("/Placeholder/dome.png"));
        scaledImg = image.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel dome = new JLabel(new ImageIcon(scaledImg));
        dome.setBounds(40, 40, 20, 20);
        add(dome);
    }

    private void checkWorker(){
        if(worker!=0)
            setWorker();
    }

    private void setWorker(){
        ImageIcon image;
        Image scaledImg;
        JLabel workerIdLabel = new JLabel(Integer.toString(worker));
        workerIdLabel.setForeground(Color.white);
        workerIdLabel.setBounds(40, 40, 20, 20);
        add(workerIdLabel);
        image = new ImageIcon(getClass().getResource("/Placeholder/worker.png"));
        scaledImg = image.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel workerLabel = new JLabel(new ImageIcon(scaledImg));
        workerLabel.setBounds(40, 40, 20, 20);
        add(workerLabel);
    }

    private void setLevel(int level){
        ImageIcon image;
        Image scaledImg;
        switch (level){
            case 3:
                image = new ImageIcon(getClass().getResource("/Placeholder/building3.png"));
                scaledImg = image.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                JLabel buildingThirdLevel = new JLabel(new ImageIcon(scaledImg));
                buildingThirdLevel.setBounds(30, 30, 40, 40);
                add(buildingThirdLevel);
            case 2:
                image = new ImageIcon(getClass().getResource("/Placeholder/building2.png"));
                scaledImg = image.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                JLabel buildingSecondLevel = new JLabel(new ImageIcon(scaledImg));
                buildingSecondLevel.setBounds(20, 20, 60, 60);
                add(buildingSecondLevel);
            case 1:
                image = new ImageIcon(getClass().getResource("/Placeholder/building1.png"));
                scaledImg = image.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                JLabel buildingFirstLevel = new JLabel(new ImageIcon(scaledImg));
                buildingFirstLevel.setBounds(10, 10, 80, 80);
                add(buildingFirstLevel);
                break;
        }
    }

    public int idPlayerWorker(int firstPlayer){
        if(worker==firstPlayer)
            return 1;
        if(worker==firstPlayer+1)
            return 2;
        return -1;
    }

    /**
     * Add a border to the cell if there is a listener on it
     * @param selectable
     */
    public void setSelectable(boolean selectable){

    }
    public void setSelected(){
        SwingUtilities.updateComponentTreeUI(this);
        System.out.println("magariii");
        setBackground(Color.red);
    }
}
