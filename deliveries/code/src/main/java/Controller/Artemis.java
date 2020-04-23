package Controller;

import Model.*;

import java.util.ArrayList;

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
            //Stub
            Index index1 = new Index(2,2,1);
        setPrevIndex(w.getPosition());
        m.moveWorker(w, index1);
        //ask to move another time
        if(moveAgain) {
            Cell cell = m.selectCell(prevIndex);
            ArrayList<Invisible> invisibles = cell.getForbidden();
            for (Invisible inv : invisibles) {
                if (inv instanceof ForbiddenMove && w.getOwner() == inv.getCreator())
                    inv.addWorker(w);
            }
            //take index2 where to move a second time
                //Stub
                Index index2 = new Index(1,1,3);
            m.moveWorker(w, index2);
            resetPower(m, w);
        }
        //take index3 where to build
            //Stub
            Index index3 = new Index(1,2,3);
        m.build(w, index3);
    }

    public void turn(Match m, Worker w,Index index1,Index index2,Index index3) {
        //take index1 where to move the first time
        setPrevIndex(w.getPosition());
        m.moveWorker(w, index1);
        //ask to move another time
        if(moveAgain) {
            Cell cell = m.selectCell(prevIndex);
            ArrayList<Invisible> invisibles = cell.getForbidden();
            for (Invisible inv : invisibles) {
                if (inv instanceof ForbiddenMove && w.getOwner() == inv.getCreator())
                    inv.addWorker(w);
            }
            //take index2 where to move a second time
            m.moveWorker(w, index2);
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

    @Override
    public ArrayList<Index> whereToMove(Match match, Worker worker){
        ArrayList<Index> cellsWhereToMove = new ArrayList<Index>();
        int currentX = worker.getPosition().getX();
        int currentY = worker.getPosition().getY();
        int currentZ = worker.getPosition().getZ();
        for(int x = currentX-1; x < currentX+2; x++){
            if(x >= 0 && x < 5){
                for(int y = currentY-1; y < currentY+2; y++){
                    if(y >= 0 && y < 5){
                        if(x != currentX || y != currentY){
                            int z=0;
                            while(z <= currentZ +1){
                                Index checkedIndex = new Index(x,y,z);
                                if(match.selectCell(checkedIndex).isEmpty()){
                                    ArrayList<Invisible> invisibles = match.selectCell(checkedIndex).getForbidden();
                                    Boolean forbiddenCell = false;
                                    for(Invisible inv : invisibles){
                                        if(inv instanceof ForbiddenMove && inv.isIn(worker)){
                                            forbiddenCell = true;
                                            break;
                                        }
                                    }
                                    if (!forbiddenCell)
                                        cellsWhereToMove.add(checkedIndex);
                                    break;
                                }
                                z++;
                            }
                        }
                    }
                }
            }
        }
        return cellsWhereToMove;
    }

    @Override
    public ArrayList<Index> whereToBuild(Match match, Worker worker){
        ArrayList<Index> cellsWhereToBuild = new ArrayList<Index>();
        int currentX = worker.getPosition().getX();
        int currentY = worker.getPosition().getY();
        for(int x = currentX-1; x < currentX+2; x++){
            if(x >= 0 && x < 5){
                for(int y = currentY-1; y < currentY+2; y++){
                    if(y >= 0 && y < 5){
                        if(x != currentX || y != currentY){
                            int z=0;
                            while(z < 4){
                                Index checkedIndex = new Index(x,y,z);
                                if(match.selectCell(checkedIndex).isEmpty()){
                                    ArrayList<Invisible> invisibles = match.selectCell(checkedIndex).getForbidden();
                                    Boolean forbiddenCell = false;
                                    for(Invisible inv : invisibles){
                                        if(inv instanceof ForbiddenConstruction && inv.isIn(worker)){
                                            forbiddenCell = true;
                                            break;
                                        }
                                    }
                                    if (!forbiddenCell)
                                        cellsWhereToBuild.add(checkedIndex);
                                    break;
                                }
                                z++;
                            }
                        }
                    }
                }
            }
        }
        return cellsWhereToBuild;
    }

    @Override
    public Boolean canMove(Match match, Worker worker) {
        return null;
    }
}