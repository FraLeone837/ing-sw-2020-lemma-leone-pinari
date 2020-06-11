package View;

import javax.swing.*;
import java.util.Random;

public class GuiMain {
    public static void main(String args[]){

        UserInterface ui = new UserInterface(UserInterface.Mode.GUI);
        Thread t = new Thread(ui, "User Interface");
        t.start();
        /*GuiPlayerManager g = new GuiPlayerManager(null);
        int island[] = new int[25];
        for(int i=0; i<25; i++){
            Random ran = new Random();
            island[i] = ran.nextInt(7);
            int worker = ran.nextInt(6);
            island[i] += worker * 10;
        }
        g.setUpMap(island);
        JFrame f = new JFrame();
        f.add(g.getPanel());
        f.setSize(800, 800);
        f.setVisible(true);*/

    }
}
