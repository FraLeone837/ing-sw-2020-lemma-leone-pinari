package Controller;

import Model.Match;
import Model.Player;
import Model.Worker;

public class PlayerManager {
    private Player player;
    private God god;

    public PlayerManager(Player player){
        this.player = player;
    }

    public void setGod(God god){
        this.god = god;
    }

    public void setup(Match match, Player player){
        god.setup(match, player);
    }

    public void turn(Match match, Worker worker){
        god.turn(match, worker);
    }
}

