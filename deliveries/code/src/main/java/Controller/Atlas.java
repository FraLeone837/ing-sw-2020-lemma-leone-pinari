package Controller;

import Model.Match;
import Model.Worker;

public class Atlas implements God {

    /**
     * this flag is for building a dome if the player wants
     */
    private boolean buildDome;

    public void setBuildDome(boolean buildDome){this.buildDome=buildDome;}

    @Override
    public String getName() {
        return "Atlas";
    }

    @Override
    public String getDescription() {
        return "Titan Shouldering the Heavens\n" +
                "Your Build: Your Worker\n" +
                "may build a dome at any level\n" +
                "including the ground.";
    }

    @Override
    public void turn(Match m, Worker w) {
        //take index1 where to move from view
        m.moveWorker(w, index1);
        //take index2 where to build from view
        //ask to build a building or a dome
        if(buildDome)
            m.buildDome(w, index2);
        else
            m.build(w, index2);
    }

    @Override
    public void setup(Match m) {
        return;
    }
}