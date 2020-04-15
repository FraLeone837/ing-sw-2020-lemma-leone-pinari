package Controller;

import Model.Index;
import Model.Match;
import Model.Player;
import Model.Worker;

public class Artemis implements God {

    /**
     * this flag is for moving once again if the player wants
     */
    private boolean moveAgain;

    public void setMoveAgain(boolean moveAgain) {
        this.moveAgain = moveAgain;
    }

    /**
     * this variable is for store the previous position of the worker that moves so, if the player wants to move again,
     * he cannot come back in the previous position
     */
    private Index prevIndex;

    public void setPrevIndex(Index prev){
        prevIndex=prev;
    }


    @Override
    public String getName() {
        return "Artemis";
    }

    @Override
    public String getDescription() {
        return "Goddess of the Hunt\n" +
                "Your Move: Your Worker may\n" +
                "move one additional time, but not\n" +
                "back to the space it started on.";
    }

    @Override
    public void turn(Match m, Worker w) {
        //take index1 where to move the first time
        setPrevIndex(w.getPosition());
        m.moveWorker(w, index1);
        //ask to move another time
        if(moveAgain)
            //take index2 where to move a second time
            m.moveWorker(w, index2);
        //take index3 where to build
        m.build(w, index3);
    }

    @Override
    public void setup(Match m, Player p) {
        return;
    }
}