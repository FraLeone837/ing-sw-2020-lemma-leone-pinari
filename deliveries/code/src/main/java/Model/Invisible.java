package Model;

import java.util.ArrayList;

/**
 * this class implements gods' powers that have effects during the opponents turns.
 * each invisible block is a set of conditions that forbid certain actions.
 * in your turn you set these blocks in the cells where the condition has effect, and the opponent has to check during his turn
 * if he can win, build or do whatever he wants before he does it.
 * for example: if you have Athena and one of your workers moves up, at the end of your turn you build an invisible block
 * that forbids movement in each cell one level above opponents' workers, so during their turns the opponents' workers cannot move up.
 * obviously, in each invisible block there are all the useful informations for the power (creator player, players affected by the power,
 * workers affected by the power, ecc).
 */
public class Invisible {

    private Player creator;

    private ArrayList<Worker> workersList;

    public Invisible (Player creator){
        this.creator = creator;
        this.workersList = new ArrayList<Worker>();
    }



    public Player getCreator(){
        return creator;
    }

    /**
     * adds the worker to the list of the workers affected by the forbidden
     *
     * @param w the worker to be added
     */
    public void addWorker(Worker w){
        workersList.add(w);
    }

    /**
     * removes all the workers from the list of the workers affected by the forbidden
     */
    public void removeWorkers(){
        workersList.clear();
    }

    /**
     * checks if the worker is in the list of workers affected by the forbidden
     *
     * @param w the worker to check
     * @return true if the worker is in the list
     */
    public Boolean isIn(Worker w){
        return workersList.contains(w);
    }

}
