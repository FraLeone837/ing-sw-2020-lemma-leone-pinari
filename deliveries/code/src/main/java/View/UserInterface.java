package View;

import Model.Index;
import Model.Island;
import Model.Player;
import Model.Worker;

import java.util.List;

public class UserInterface {

    private GameManager gameManager;
    private PlayerManager playerManager;

    public UserInterface(){
        gameManager = new CliGameManager();
        playerManager = new CliPlayerManager();
    }
}
