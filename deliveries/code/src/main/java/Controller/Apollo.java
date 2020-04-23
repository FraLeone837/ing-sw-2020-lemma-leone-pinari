package Controller;

import Model.*;

import java.util.ArrayList;

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
    public void resetPower(Match m, Worker w) {
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
                                if(!match.selectCell(checkedIndex).isBuilding() && !match.selectCell(checkedIndex).isDome()  ){
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

