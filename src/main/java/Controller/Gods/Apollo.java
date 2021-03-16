package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class Apollo extends God {

    @Override
    public String getName() {
        return "Apollo";
    }

    @Override
    public String getDescription() {
        return  "God Of Music\n" +
                "Your Move: Your Worker may\n" +
                "move into an opponent Worker’s\n" +
                "space (using normal movement\n" +
                "rules) and force their Worker to the space yours\n" +
                "just vacated (swapping their positions).";
    }

    @Override
    public void manageMove(Match match, CommunicationProxy communicationProxy, Worker worker, ArrayList<Index> possibleMove) {
        setPrevIndex(worker.getPosition());
        //take index1 where to move from view
        Index tempMoveIndex = (Index)communicationProxy.sendMessage(Message.MessageType.MOVE_INDEX_REQ, possibleMove);
        Index actuaMovelIndex = correctIndex(match,tempMoveIndex);
        Worker opponent = match.selectCell(actuaMovelIndex).getWorker();
        if(opponent != null) {
            match.moveWorker(opponent, worker.getPosition(), false);
            match.initWorker(worker, actuaMovelIndex);
        }
        else
            match.moveWorker(worker, actuaMovelIndex);
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
                                Cell checkedCell = match.selectCell(checkedIndex);
                                if(checkedCell.isDome()){
                                    break;
                                }
                                if(!checkedCell.isBuilding()){
                                    if(checkedCell.getWorker()!=null && checkedCell.getWorker().getOwner()==worker.getOwner() && checkedCell.getWorker()!=worker){
                                        break;
                                    }
                                    ArrayList<Invisible> invisibles = checkedCell.getForbidden();
                                    Boolean forbiddenCell = false;
                                    for(Invisible inv : invisibles){
                                        if(inv instanceof ForbiddenMove && inv.isIn(worker)){
                                            forbiddenCell = true;
                                            break;
                                        }
                                    }
                                    if (!forbiddenCell){
                                        cellsWhereToMove.add(checkedIndex);
                                        forbiddenCell = true;
                                    }
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
    public Index correctIndex(Match match, Index index){
        int x = index.getX();
        int y = index.getY();
        Index currentIndex = index;
        for(int z=0; z<4; z++){
            currentIndex = new Index(x,y,z);
            if(!(match.selectCell(currentIndex).isBuilding()))
                return currentIndex;
        }
        return currentIndex;
    }

}

