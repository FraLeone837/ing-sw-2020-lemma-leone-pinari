package View;

import Controller.God;
import Model.Cell;
import Model.Index;
import Model.Island;
import Model.Player;

public class CliGameManager implements GameManager {


    @Override
    public void startMatch() {
        System.out.println("Sei connesso");
        System.out.println("Iniziare una partita?");
    }

    @Override
    public void waitForPlayer(){
        System.out.println("Corrispondere iniziare");
    }

    @Override
    public void printReadyToStart(boolean starting, God assigned) {
        System.out.println("Ci sono entrambi i giocatori, la partita può iniziare!");
        if(starting)
            System.out.println("Partirai per primo");
        else
            System.out.println("Partirai per secondo");
        System.out.println("Ti è stato assegnato il dio " + assigned.getName());
    }

    @Override
    public void updateMap(Island island, Player player) {
        System.out.println("  a b c d e ");
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
    }

    @Override
    public void printWin(boolean win) {
        if(win)
            System.out.println("Hai vinto!");
        else
            System.out.println("Hai perso.");
    }


}
