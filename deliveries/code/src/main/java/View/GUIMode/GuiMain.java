package View.GUIMode;

import View.UserInterface;

import javax.swing.*;
import java.util.Random;

public class GuiMain {
    public static void main(String args[]){

        UserInterface ui = new UserInterface(UserInterface.Mode.GUI);
        Thread t = new Thread(ui, "User Interface");
        t.start();

    }
}
