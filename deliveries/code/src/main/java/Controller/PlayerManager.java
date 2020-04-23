package Controller;

import Model.*;

import java.util.ArrayList;

public class PlayerManager {
    private Player player;
    private God god;

    public PlayerManager(Player player){
        this.player = player;
    }

    public void setGod(God god){
        this.god = god;
    }

    /**
     * has to be invoked at the beginning of the match in order to set a block Invisible in each cell of the
     * game board, if the god's power foresees that
     *
     * @param match the current match
     * @param player the player who owns the god
     */
    public void setup(Match match, Player player){
        god.setup(match, player);
    }

    /**
     * manages the progress of the turn after you select a worker, according to what god's power makes you able/unable to do
     *
     * @param match the current match
     * @param worker the worker selected by the player to execute the turn
     */
    public void turn(Match match, Worker worker){
        god.turn(match, worker);
    }

}

