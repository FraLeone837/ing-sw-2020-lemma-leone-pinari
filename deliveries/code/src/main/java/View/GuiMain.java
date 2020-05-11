package View;

public class GuiMain {
    public static void main(String args[]){
/*        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
*/
        //GameFrame gf = new GameFrame();
        UserInterface ui = new UserInterface(UserInterface.Mode.GUI);
        Thread t = new Thread(ui, "User Interface");
        t.start();
    }
}
