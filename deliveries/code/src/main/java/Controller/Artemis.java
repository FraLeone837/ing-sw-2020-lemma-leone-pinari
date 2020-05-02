package Controller;

import Model.*;

import java.util.ArrayList;

public class Artemis extends God {

    /**
     * this flag is for moving once again if the player wants
     */
    private boolean moveAgain;

    public void setMoveAgain(boolean moveAgain) {
        this.moveAgain = moveAgain;
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
        setPrevIndex(w.getPosition());
        //take index1 where to move the first time
            //Stub
            Index index1 = new Index(2,2,1);
        m.moveWorker(w, index1);
        checkWin(m, w);
        //ask to move another time
        if(moveAgain) {
            Cell cell = m.selectCell(prevIndex);
            ArrayList<Invisible> invisibles = cell.getForbidden();
            for (Invisible inv : invisibles) {
                if (inv instanceof ForbiddenMove && w.getOwner() == inv.getCreator())
                    inv.addWorker(w);
            }
            setPrevIndex(w.getPosition());
            //take index2 where to move a second time
                //Stub
                Index index2 = new Index(1,1,3);
            m.moveWorker(w, index2);
            checkWin(m, w);
            resetPower(m, w);
        }
        //take index3 where to build
            //Stub
            Index index3 = new Index(1,2,3);
        m.build(w, index3);
    }

    public void turn(Match m, Worker w,Index index1,Index index2,Index index3) {
        setPrevIndex(w.getPosition());
        //take index1 where to move the first time
        m.moveWorker(w, index1);
        checkWin(m, w);
        //ask to move another time
        if(moveAgain) {
            Cell cell = m.selectCell(prevIndex);
            ArrayList<Invisible> invisibles = cell.getForbidden();
            for (Invisible inv : invisibles) {
                if (inv instanceof ForbiddenMove && w.getOwner() == inv.getCreator())
                    inv.addWorker(w);
            }
            setPrevIndex(w.getPosition());
            //take index2 where to move a second time
            m.moveWorker(w, index2);
            checkWin(m, w);
            resetPower(m, w);
        }
        //take index3 where to build
        m.build(w, index3);
    }

    @Override
    public void setup(Match m, Player p) {
        for(int x=0; x<5; x++){
            for(int y=0; y<5; y++){
                for(int z=0; z<4; z++){
                    Index i=new Index(x,y,z);
                    Invisible invisible = new ForbiddenMove(p);
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
            if(inv instanceof ForbiddenMove && w.getOwner()==inv.getCreator())
                inv.removeWorkers();
        }
    }

    /**
     * allows you to know if you can end the turn with the selected worker moving twice
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @return true if you can end the turn with the worker moving twice
     */
    public Boolean canMoveTwice(Match match, Worker worker){
        ArrayList<Index> possibleMoves = whereToMove(match, worker, worker.getPosition());
        if(possibleMoves.isEmpty())
            return false;
        for(Index index : possibleMoves){
            ArrayList<Index> possibleSecondMoves = whereToMove(match, worker, index);
            if(!possibleSecondMoves.isEmpty()){
                for(Index index2 : possibleSecondMoves) {
                    ArrayList<Index> possibleBuildings = whereToBuild(match, worker, index2);
                    if (!possibleBuildings.isEmpty())
                        return true;
                }
            }
        }
        return false;
    }
}