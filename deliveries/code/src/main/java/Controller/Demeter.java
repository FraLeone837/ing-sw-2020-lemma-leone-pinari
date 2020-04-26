package Controller;

import Model.*;

import java.util.ArrayList;

public class Demeter implements God {

    /**
     * this flag is for building once again if the player wants
     */
    private boolean buildAgain;

    public void setBuildAgain(boolean buildAgain) {
        this.buildAgain = buildAgain;
    }

    private Index prevIndex;

    @Override
    public void setPrevIndex(Index prev){
        prevIndex=prev;
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

    @Override
    public ArrayList<Index> whereToMove(Match match, Worker worker, Index index){
        ArrayList<Index> cellsWhereToMove = new ArrayList<Index>();
        int currentX = index.getX();
        int currentY = index.getY();
        int currentZ = index.getZ();
        for(int x = currentX-1; x < currentX+2; x++){
            if(x >= 0 && x < 5){
                for(int y = currentY-1; y < currentY+2; y++){
                    if(y >= 0 && y < 5){
                        if(x != currentX || y != currentY){
                            int z=0;
                            while(z <= currentZ +1){
                                Index checkedIndex = new Index(x,y,z);
                                if(match.selectCell(checkedIndex).isEmpty() || match.selectCell(checkedIndex).getWorker()==worker){
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
    public ArrayList<Index> whereToBuild(Match match, Worker worker, Index index){
        ArrayList<Index> cellsWhereToBuild = new ArrayList<Index>();
        int currentX = index.getX();
        int currentY = index.getY();
        for(int x = currentX-1; x < currentX+2; x++){
            if(x >= 0 && x < 5){
                for(int y = currentY-1; y < currentY+2; y++){
                    if(y >= 0 && y < 5){
                        if(x != currentX || y != currentY){
                            int z=0;
                            while(z < 4){
                                Index checkedIndex = new Index(x,y,z);
                                if(match.selectCell(checkedIndex).isEmpty() || match.selectCell(checkedIndex).getWorker()==worker){
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
        ArrayList<Index> possibleMoves = whereToMove(match, worker, worker.getPosition());
        if(possibleMoves.isEmpty())
            return false;
        for(Index index : possibleMoves){
            ArrayList<Index> possibleBuildings = whereToBuild(match, worker, index);
            if(!possibleBuildings.isEmpty())
                return true;
        }
        return false;
    }

    @Override
    public Boolean checkWin(Match match, Worker worker) {
        Index currentIndex = worker.getPosition();
        if(prevIndex.getZ()==2 && currentIndex.getZ()==3){
            Cell currentCell = match.selectCell(currentIndex);
            ArrayList<Invisible> invisibles = currentCell.getForbidden();
            for(Invisible inv : invisibles){
                if(inv instanceof ForbiddenWin){
                    if(inv.isIn(worker))
                        return false;
                }
            }
            return true;
        }
        return false;
    }
}