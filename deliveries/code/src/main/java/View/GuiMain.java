package View;

import javax.swing.*;

public class GuiMain {
    public static void main(String args[]){

        UserInterface ui = new UserInterface(UserInterface.Mode.GUI);
        Thread t = new Thread(ui, "User Interface");
        t.start();
        /*GuiPlayerManager g = new GuiPlayerManager(null);
        g.setUpMap();
        JFrame f = new JFrame();
        f.add(g.getPanel());
        f.pack();
        f.show();*/

    }
}
