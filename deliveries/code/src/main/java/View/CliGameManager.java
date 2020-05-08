package View;

import Controller.God;

public class CliGameManager implements GameManager {

    private int idFirstWorker;

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
    public void updateMap(int[][][] island) {
        System.out.println("  a b c d e ");
        for(int y=0; y<5; y++){
            System.out.print(y+1+" ");
            for(int x=0; x<5; x++){
                char content = ' ';
                int level = 0;
                if(island[x][y][0] > 3) {
                    content = 'c';
                    level = island[x][y][0] - 4;
                }
                else
                    level = island[x][y][0];
                if(island[x][y][0] != 0)
                    content = (char)(island[x][y][0]);
                System.out.print("\u001B[4"+(level+1)+"m\u001B[37m"+content+" ");
            }
            System.out.println("\u001B[0m");
        }
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
