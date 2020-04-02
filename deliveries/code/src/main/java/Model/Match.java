package Model;

import java.util.ArrayList;

public class Match {
    //decide list or normal array
    private ArrayList<Player> players;
    private int idMatch;
    private Island island;


    //decide list or normal array
    public Match(){
        players = new ArrayList<Player>();
        idMatch =  1;
        island = new Island();
    }

    /**
     * after everything else has been created starts the game and sends a notification to Controller
     *
     */
    public void Start(){
        return;
    }

    /**
     * invokes the methods of island to instantiate all cells and workers
     */
    public void Setup(){

    }

    /**
     * match ends when win/lose condition is declared
     */
    public void endGame(){

    }

    private void notifyView(){

    }
}
