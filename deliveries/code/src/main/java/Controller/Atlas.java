package Controller;

import Model.*;

import java.util.ArrayList;

public class Atlas extends God {

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
        setPrevIndex(w.getPosition());
        //take index1 where to move from view
            //Stub
            Index index1 = new Index(0,0,1);
        m.moveWorker(w, index1);
        checkWin(m, w);
        //take index2 where to build from view
        //ask to build a building or a dome
            //Stub
            Index index2 = new Index(2,3,1);
        if(buildDome)
            m.buildDome(w, index2);
        else
            m.build(w, index2);
    }

    public void turn(Match m, Worker w, Index index1, Index index2) {
        setPrevIndex(w.getPosition());
        //take index1 where to move from view
        m.moveWorker(w, index1);
        checkWin(m, w);
        //take index2 where to build from view
        //ask to build a building or a dome
        if(buildDome)
            m.buildDome(w, index2);
        else
            m.build(w, index2);
    }
}