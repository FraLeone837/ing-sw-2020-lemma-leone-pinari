package View.CustomComponent;

import View.GuiPlayerManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TextInputPanel extends JPanel {

    private JLabel label;
    private JTextField textField;
    private JButton submit;

    private String input;

    public TextInputPanel(String labelMessage){
        label = new JLabel(labelMessage);
        textField = new JTextField(10);
        submit = new JButton("SUBMIT");
        prepareGui();
    }

    private void prepareGui(){
        setLayout(new FlowLayout());

        add(label);
        add(textField);
        add(submit);

        submit.addActionListener(new TextInputListener(textField));


        JButton stampo = new JButton("STAMPO");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("EEEEEEE MACARENA");
            }
        });
        add(stampo);



        SwingUtilities.updateComponentTreeUI(this);
    }

    public String getInput(){
        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return input;
    }

    private void textInput(String input){
        this.input = input;
        synchronized (this){
            notifyAll();
        }
    }

    class TextInputListener implements ActionListener {

        JTextField tf;
        public TextInputListener(JTextField tf){
            this.tf = tf;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            textInput(tf.getText());
        }
    }
}
