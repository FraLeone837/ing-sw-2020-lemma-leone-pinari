package View.CliMode;

import Controller.Gods.God;
import View.Interfaces.GameManager;

public class CliGameManager implements GameManager {

    private int idFirstWorker;

    /**
     * used for the text color :)
     */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * to be used for the colors of background
     * aka to mean the level of the construction
     */
    public static final String Black = "\u001b[40m";
    public static final String Red = "\u001b[41m";
    public static final String Green = "\u001b[42m";
    public static final String Yellow = "\u001b[43m";
    public static final String Blue = "\u001b[44m";
    public static final String Magenta = "\u001b[45m";
    public static final String Cyan = "\u001b[46m";
    public static final String White = "\u001b[47m";

    public static final String colorGroundLevel = Green;
    public static final String colorFirstLevel = Yellow;
    public static final String colorSecondLevel = Blue;
    public static final String colorThirdLevel = Magenta;
    public static final String textColor = ANSI_BLACK;

    @Override
    public void startMatch() {
        //System.out.println("Sei connesso");
        System.out.println("Do you want to start a match?");
    }

    @Override
    public void waitForPlayer(){
        System.out.println("Corrispondere iniziare");
    }

    public void printIdWorkers(int idFirstWorker){
        this.idFirstWorker = idFirstWorker;
        System.out.println("Your workers will be the ones with ids " + idFirstWorker + " and " + (idFirstWorker+1));
    }

    @Override
    public void printReadyToStart(boolean starting, God assigned) {
        System.out.println("Both the players are here, the match can start!");
        if(starting)
            System.out.println("You'll play as first player");
        else
            System.out.println("You'll play as second player");
        System.out.println("You've been assigned the God " + assigned.getName());
    }

    @Override
    public void updateMap(int[] island) {
        //stampo le coordinate verticali
        System.out.println("    a - b - c - d - e  ");
        for(int i=0; i<25; i++){
            //ottengo le coordinate come x e y
            int x = i%5;
            int y = i/5 + 1;
            //se x=0 siamo su una nuova riga, perciò prima di tutto stampo l'indice della riga
            if(x==0)
                System.out.print(y+"||");
        /*imposto le tre variabili
            content è il contenuto della casella, di default vuota
            level è il livello su cui ci troviamo (le unità dell'integer)
            workerId è l'id del lavoratore nella casella (le decine dell'integer)
        */
            char content = ' ';
            int level = island[i]%10;
            int workerId = island[i]/10;
            // se level è maggiore di 3 nella casella c'è una cupola, perciò manipolo level e metto c in content
            if(level>3) {
                content = 'c';
                level = level - 4;
            }
            //se il workerId!=0 vuol dire che nella casella c'è un lavoratore, quindi content avrà quel valore
            if(workerId!=0){
                String s = Integer.toString(workerId);
                content = s.charAt(0);
            }
            /*stampo tutte le informazioni di quella cella
             * il livello determina il colore della cella
             * la presenza di una cupola o di un lavoratore ne determina il contenuto
             * */
//            System.out.print("\u001B[4"+(level+1)+"m\u001B[37m"+workerId+" ");

            /**
             * change the color of x level up,
             * do not change this method. thanks
             */
            switch (level){
                case 0:
                    System.out.print(colorGroundLevel + " ");
                    break;
                case 1:
                    System.out.print(colorFirstLevel+  " ");
                    break;
                case 2:
                    System.out.print(colorSecondLevel+ " ");
                    break;
                case 3:
                    System.out.print(colorThirdLevel+ " ");
                    break;

            }
            System.out.print( textColor + content);
            if(x!=4){
                System.out.print("- ");
            }
//            System.out.println(ANSI_RED+ (level+1) + ANSI_GREEN + content+ " ");
            // se x==4 siamo alla fine della riga, quindi vado a capo dopo aver resettato il colore
            if(x==4)
                System.out.println(ANSI_RESET+ "||");
        }
    }

    @Override
    public void showGod(String[] god) {

    }


    @Override
    public void printWin(boolean win) {
        if(win)
            System.out.println("You win!");
        else
            System.out.println("You lose.");
    }


}
