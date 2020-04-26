package Controller;

import Model.Index;
import Model.Match;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;

public interface God {

    /**
     * @return a constant string containing god's name
     */
    public String getName();

    /**
     * @return a constant string containing god's power description
     */
    public String getDescription();

    /**
     * stores the previous index of a worker in an attribute when it moves, in order to check if the owner win
     * @param prev the index of the starting cell of the worker
     */
    public void setPrevIndex(Index prev);

    /**
     * this method manages the entire turn, from the movement of the worker to the building, taking into account god's power
     *
     * @param m the match that the server is managing
     * @param w the worker selected by the player
     */
    public void turn(Match m, Worker w);


    /**
     * this method is used at the beginning of the match to do actions that will be active for the whole duration of the game,
     * such as putting the invisible blocks in every cell
     *
     * @param m the match that the server is managing
     * @param p the player whose god's power has to be set
     */
    public void setup(Match m, Player p);

    /**
     * delete all the workers from the invisible blocks managed by the current player when his god's power ends
     *
     * @param m the current match
     * @param w the worker selected by the player
     */
    public void resetPower(Match m, Worker w);

    /**
     * allows you to know where you could move a worker if it was in a specific cell, taking forbiddenMove-blocks into account
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @param index the index where you imagine the worker is
     * @return the list of indexes of the cells where the worker can moves
     */
    public ArrayList<Index> whereToMove(Match match, Worker worker, Index index);

    /**
     * allows you to know where you could build with a worker if it was in a specific cell, taking forbiddenMove-blocks into account
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @param index the index where you imagine the worker is
     * @return the list of indexes of the cells where the worker can builds
     */
    public ArrayList<Index> whereToBuild(Match match, Worker worker, Index index);

    /**
     * allows you to know if you can end the turn with the selected worker
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @return true if you can end the turn with the worker
     */
    public Boolean canMove(Match match, Worker worker);

    /**
     * @return true if a winning condition is verified, taking the power of god into account
     */
    public Boolean checkWin(Match match, Worker worker);

}

