package Controller;

import Model.Match;
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

}

