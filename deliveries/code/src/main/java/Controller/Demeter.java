package Controller;

import Model.Index;
import Model.Match;
import Model.Player;
import Model.Worker;

public class Demeter implements God {

    /**
     * this flag is for building once again if the player wants
     */
    private boolean buildAgain;

    public void setBuildAgain(boolean buildAgain) {
        this.buildAgain = buildAgain;
    }

    /**
     * this variable is for storing the position where the worker builds the first time, so if the player wants to build again,
     * he cannot do it in the same position
     */
    private Index prevIndex;

    public void setPrevIndex(Index prev){
        prevIndex=prev;
    }

    @Override
    public String getName() {
        return "Demeter";
    }

    @Override
    public String getDescription() {
        return "Goddess of the Harvest\n" +
                "Your Build: Your Worker may\n" +
                "build one additional time, but not\n" +
                "on the same space.";
    }

    @Override
    public void turn(Match m, Worker w) {
        //take index1 where to move the first time
        m.moveWorker(w, index1);
        //take index2 where to build
        m.build(w, index2);
        //ask to build another time
        if(buildAgain)
            //take index3 where to build a second time
            m.build(w, index3);
    }

    @Override
    public void setup(Match m, Player p) {
        return;
    }
}