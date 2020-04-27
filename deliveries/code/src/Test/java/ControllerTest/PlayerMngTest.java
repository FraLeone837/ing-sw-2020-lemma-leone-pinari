package ControllerTest;

import Controller.PlayerManager;
import Model.Player;
import org.junit.Before;

public class PlayerMngTest {
    PlayerManager pmg;
    Player player;
    @Before
    public void setUp(){
        player = new Player("Bonby", 1);
        pmg = new PlayerManager(player);
    }
}
