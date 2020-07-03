package Controller.Gods;

import Model.*;

import java.util.ArrayList;

public class Hera extends God {

    @Override
    public String getName() {
        return "Hera";
    }

    @Override
    public String getDescription() {
        return "Goddess of Marriage\n" +
                "Opponent’s Turn: An opponent\n" +
                "cannot win by moving on to a\n" +
                "perimeter space.";
    }

    @Override
    public void setup(Match match, Player player) {
        for(int x=0; x<5; x++){
            for(int y=0; y<5; y++){
                if(x==0 || x==4 || y==0 || y==4) {
                    for(int z=1; z<4; z++){
                        Index index = new Index(x, y, z);
                        Invisible invisible = new ForbiddenWin(player);
                        ArrayList<Player> players = match.getPlayers();
                        for(Player opponent: players){
                            if(opponent != player){
                                invisible.addWorker(opponent.getWorker1());
                                invisible.addWorker(opponent.getWorker2());
                            }
                        }
                        match.buildInvisible(invisible, index);
                    }
                }
            }
        }
    }
}
