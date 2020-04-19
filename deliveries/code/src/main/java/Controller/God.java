package Controller;

import Model.Index;
import Model.Match;
import Model.Player;
import Model.Worker;

public interface God {

    public String getName();

    public String getDescription();

    /**
     * this method manages the entire turn, from the movement of the worker to the building, taking into account god's power
     *
     * @param m the match that the server is managing
     * @param w the worker that the player chose to move
     */
    public void turn(Match m, Worker w);


    /**
     * this method is used at the beginning of the match to do actions that will be active for the whole duration of the game,
     * such as putting the invisible blocks in every cell
     *
     * @param m the match that the server is managing
     * @param p the worker that the player chose to move
     */
    public void setup(Match m, Player p);

}

