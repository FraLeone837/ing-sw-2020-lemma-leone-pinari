package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class Zeus extends God {

    @Override
    public String getName() {
        return "Zeus";
    }

    @Override
    public String getDescription() {
        return "God of the Sky\n" +
                "Your Build: Your Worker may\n" +
                "build under itself in its current\n" +
                "space, forcing it up one level. You\n" +
                "do not win by forcing yourself up to the third level.";
    }

    @Override
    public void manageBuild(Match match, CommunicationProxy communicationProxy, Worker worker, ArrayList<Index> possibleBuild){
        //take index2 where to build from view
        Index tempBuildIndex = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
        Index actualBuildIndex = correctIndex(match,tempBuildIndex,worker);
        if(actualBuildIndex.equals(worker.getPosition())){
            Index moveWorker = new Index(actualBuildIndex.getX(), actualBuildIndex.getY(), actualBuildIndex.getZ()+1);
            match.moveWorker(worker, moveWorker, false);
        }
        match.build(worker, actualBuildIndex);
    }

    @Override
    public ArrayList<Index> whereToBuild(Match match, Worker worker, Index index) {
        ArrayList<Index> cellsWhereToBuild = new ArrayList<Index>();
        int currentX = index.getX();
        int currentY = index.getY();
        Boolean forbiddenCell = true;
        for(int x = currentX-1; x < currentX+2; x++){
            if(x >= 0 && x < 5){
                for(int y = currentY-1; y < currentY+2; y++){
                    if(y >= 0 && y < 5){
                        if(x != currentX || y != currentY){
                            int z=0;
                            while(z < 4){
                                Index checkedIndex = new Index(x,y,z);
                                Cell checkedCell = match.selectCell(checkedIndex);
                                if(checkedCell.isDome()){
                                    break;
                                }
                                if(!checkedCell.isBuilding()){
                                    if(checkedCell.getWorker()==null || checkedCell.getWorker()==worker) {
                                        ArrayList<Invisible> invisibles = checkedCell.getForbidden();
                                        forbiddenCell = false;
                                        for (Invisible inv : invisibles) {
                                            if (inv instanceof ForbiddenConstruction && inv.isIn(worker)) {
                                                forbiddenCell = true;
                                                break;
                                            }
                                        }
                                    }
                                    else{
                                        forbiddenCell = true;
                                        break;
                                    }
                                    if (!forbiddenCell){
                                        cellsWhereToBuild.add(checkedIndex);
                                        forbiddenCell = true;
                                    }
                                    break;
                                }
                                z++;
                            }
                        }
                        else{
                            if(index.getZ()<3)
                                cellsWhereToBuild.add(index);
                        }
                    }
                }
            }
        }
        return cellsWhereToBuild;
    }

    public Index correctIndex(Match match, Index index, Worker worker){
        int x = index.getX();
        int y = index.getY();
        Index currentIndex = index;
        for(int z=0; z<4; z++){
            currentIndex = new Index(x,y,z);
            if(match.selectCell(currentIndex).isEmpty() || match.selectCell(currentIndex).getWorker()==worker)
                return currentIndex;
        }
        return currentIndex;
    }
}
