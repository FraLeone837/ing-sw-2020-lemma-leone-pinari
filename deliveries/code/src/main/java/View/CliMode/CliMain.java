package View.CliMode;

import View.UserInterface;

public class CliMain {
    public static void main(String[] args){
        UserInterface ui = new UserInterface(UserInterface.Mode.CLI);
        Thread t = new Thread(ui);
        t.start();
    }
}
