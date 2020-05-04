package View;

import Controller.God;
import Model.Island;
import Model.Player;

public interface GameManager {

    /**
     * Invoked to tell the player that is connected to the server
     */
    public abstract void startMatch();

    /**
     * Tell the player that it has to wait for another player to join the match
     */
    public abstract void waitForPlayer();

    /**
     * Tell the player that the game is ready to begin and if it is the first to start
     * @param starting true if the player on this client is the first to start
     * @param assigned the randomly chosen god for the player
     */
    public abstract void printReadyToStart(boolean starting, God assigned);

    /**
     * Show the updated map to the player
     * @param island the updated map
     * @param player the player on this client
     */
    public abstract void updateMap(int[][][] island, Player player);

    /**
     * Invoked when someone wins
     * @param win true if it's the player on this client to have won
     */
    public abstract void printWin(boolean win);
}
