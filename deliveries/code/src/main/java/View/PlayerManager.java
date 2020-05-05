package View;

import Model.Index;
import Model.Worker;

import java.util.List;

public interface PlayerManager {
    /**
     * Ask the user the IP of the server it wants to connect to
     * @return the IP written on the cli
     */
    public abstract String getServerIp();

    /**
     * Ask the user the username it wants to use
     * @return the name given by the player
     */
    public abstract String getName();

    /**
     * Print the list of all the matches that the player can join
     * @param ids the ArrayList with the ids of all the matches
     * @return the id of the match that the player has choosen to join
     */
    //public abstract int listMatch(List<Integer> ids);

    /**
     * If there isn't any match on the server, the player is asked to create a new one
     * So this method ask the player how many players it wants this match to have
     * @return the number of player of the match (could be 2 or 3)
     */
    public abstract int chooseNumberPlayers();

    /**
     * Ask the player where to place his workers at the start of the game
     * @param firstWorker true if the player hasn't place any worker before
     * @return the index in which to place the worker
     */
    public abstract int placeWorker(boolean firstWorker, int[] possiblePositions);

    /**
     * Ask the player which of its workers to use in this turn
     * @param workers the list of the workers that can be moved
     * @return the chosen worker
     */
    public abstract int chooseWorker(int workers);

    /**
     * Ask the player in which of the possible cells it wants to move the
     * previously selected worker
     * @param movements list of the possible movements
     * @return the chosen movement
     */
    public abstract int chooseMovement(int[] movements);

    /**
     * Ask the player in which of the possible cells it wants the worker to build
     * @param buildings list of the possible cells to build in
     * @return the chosen cell to build in
     */
    public abstract int chooseBuilding(int[] buildings);
}
