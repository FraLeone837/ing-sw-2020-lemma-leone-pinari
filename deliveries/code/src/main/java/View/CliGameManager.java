package View;

import Controller.God;

import static View.PlayerManager.ANSI_RESET;

public class CliGameManager implements GameManager {

    private int idFirstWorker;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

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
        System.out.println("  a b c d e  ");
        for(int i=0; i<25; i++){
            //ottengo le coordinate come x e y
            int x = i%5;
            int y = i/5;
            //se x=0 siamo su una nuova riga, perciò prima di tutto stampo l'indice della riga
            if(x==0)
                System.out.print(y+" ");
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
            if(workerId!=0)
                content = (char)(workerId);
            /*stampo tutte le informazioni di quella cella
             * il livello determina il colore della cella
             * la presenza di una cupola o di un lavoratore ne determina il contenuto
             * */
            System.out.print("\u001B[4"+(level+1)+"m\u001B[37m"+workerId+" ");
//            System.out.println(ANSI_RED+ (level+1) + ANSI_GREEN + content+ " ");
            // se x==4 siamo alla fine della riga, quindi vado a capo dopo aver resettato il colore
            if(x==4)
                System.out.println(ANSI_RESET);
        }
    }

    @Override
    public void showGod(String[] god) {

    }
    /*@Override
    public void updateMap(Island island, Player player) {
        /*System.out.println("  a b c d e ");
        for(int y=0; y<5; y++){
            System.out.print(y+1+" ");
            for(int x=0; x<5; x++){
                char content = ' ';
                int level = 0;
                for(int z=0; z<4; z++){
                    Cell cell = island.getCell(new Index(x, y, z));
                    if(cell.isEmpty())
                        break;
                    level = z;
                    if(cell.getWorker()!=null){
                        if(cell.getWorker().getOwner()==player)
                            content = 'y';
                        else
                            content = 'e';
                        level=z-1;
                    }
                }
                System.out.print("\u001B[4"+(level+1)+"m\u001B[37m"+content+" ");
            }
            System.out.println("\u001B[0m");
        }
    }*/

    @Override
    public void printWin(boolean win) {
        if(win)
            System.out.println("You win!");
        else
            System.out.println("You lose.");
    }


}
