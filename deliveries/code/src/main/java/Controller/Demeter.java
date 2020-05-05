package Controller;

import Model.*;

import java.util.ArrayList;

public class Demeter extends God {

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
    private Index prevBuildIndex;

    public void setPrevBuildIndex(Index prevBuildIndex) {
        this.prevBuildIndex = prevBuildIndex;
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
        setPrevIndex(w.getPosition());
        //index1 = take index1 where to move the first time
            //Stub
            Index index1 = new Index(1,4,3);
        m.moveWorker(w, index1);
        checkWin(m, w);
        //take index2 where to build
            //Stub
            Index index2 = new Index(1,3,3);
        setPrevBuildIndex(index2);
        m.build(w, index2);
        //ask to build another time
        if(buildAgain) {
            Cell cell = m.selectCell(prevBuildIndex);
            ArrayList<Invisible> invisibles = cell.getForbidden();
            for (Invisible inv : invisibles) {
                if (inv instanceof ForbiddenConstruction && w.getOwner() == inv.getCreator())
                    inv.addWorker(w);
            }
            //take index3 where to build a second time
                //Stub
                Index index3 = new Index(2, 3, 3);
            m.build(w, index3);
            resetPower(m, w);
        }
    }


    public void turn(Match m, Worker w,Index index1,Index index2,Index index3) {
        setPrevIndex(w.getPosition());
        //take index1 where to move the first time
        m.moveWorker(w, index1);
        checkWin(m, w);
        //take index2 where to build
        setPrevBuildIndex(index2);
        m.build(w, index2);
        //ask to build another time
        if(buildAgain){
            Cell cell = m.selectCell(prevBuildIndex);
            ArrayList<Invisible> invisibles = cell.getForbidden();
            for (Invisible inv : invisibles) {
                if (inv instanceof ForbiddenConstruction && w.getOwner() == inv.getCreator())
                    inv.addWorker(w);
            }
            //take index3 where to build a second time
            m.build(w, index3);
            resetPower(m,w);
        }
    }



    @Override
    public void setup(Match m, Player p) {
        for(int x=0; x<5; x++){
            for(int y=0; y<5; y++){
                for(int z=0; z<4; z++){
                    Index i=new Index(x,y,z);
                    Invisible invisible = new ForbiddenConstruction(p);
                    m.buildInvisible(invisible, i);
                }
            }
        }
    }

    @Override
    public void resetPower(Match m, Worker w) {
        Cell cell = m.selectCell(prevIndex);
        ArrayList<Invisible> invisibles = cell.getForbidden();
        for(Invisible inv : invisibles){
            if(inv instanceof ForbiddenConstruction && w.getOwner()==inv.getCreator())
                inv.removeWorkers();
        }
    }
}