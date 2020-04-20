package Controller;

import Model.Match;
import Model.Player;
import Model.Worker;
import Model.Index;

public class Apollo implements God {

    @Override
    public String getName() {
        return "Apollo";
    }

    @Override
    public String getDescription() {
        return "God Of Music\n" +
                "Your Move: Your Worker may\n" +
                "move into an opponent Worker’s\n" +
                "space (using normal movement\n" +
                "rules) and force their Worker to the space yours\n" +
                "just vacated (swapping their positions).";
    }

    @Override
    public void turn(Match m, Worker w) {
        //take index1 where to move from view
            //stub
            Index index1 = new Index(1,2,3);
        Worker o = m.selectCell(index1).getWorker();
        m.moveWorker(o, w.getPosition());
        m.moveWorker(w,index1);

        //take index2 where to build from view
            //Stub
            Index index2 = new Index(2,3,0);
        m.build(w, index2);
    }

    public void turn(Match m, Worker w, Index index1, Index index2) {
        //take index1 where to move from view
        Worker o = m.selectCell(index1).getWorker();
        m.moveWorker(o, w.getPosition());
        m.moveWorker(w,index1);

        //take index2 where to build from view
        m.build(w, index2);
    }

    @Override
    public void setup(Match m, Player p) {
    }

    @Override
    public void reset(Match m, Worker w) {
    }
}

