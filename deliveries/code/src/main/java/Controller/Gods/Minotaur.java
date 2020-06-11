package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class Minotaur extends God {

    @Override
    public String getName() {
        return "Minotaur";
    }

    @Override
    public String getDescription() {
        return "Bull-headed Monster\n" +
                "Your Move: Your Worker may\n" +
                "move into an opponent Worker’s\n" +
                "space (using normal movement\n" +
                "rules), if the next space in the same direction is\n" +
                "unoccupied. Their Worker is forced into that space\n" +
                "(regardless of its level).";
    }

    @Override
    public void turn(Match match, CommunicationProxy communicationProxy, Worker worker) {
        ArrayList<Index> possibleMove = whereToMove(match, worker, worker.getPosition());
        if(possibleMove.isEmpty()){
            setInGame(false);
            return;
        }
        setPrevIndex(worker.getPosition());
        //take index1 where to move from view
        Index tempMoveIndex = (Index)communicationProxy.sendMessage(Message.MessageType.MOVE_INDEX_REQ, possibleMove);
        Index actuaMovelIndex = correctIndex(match,tempMoveIndex);
        Worker opponent = match.selectCell(actuaMovelIndex).getWorker();
        if(opponent!=null){
            Index moveOpponent = indexForward(match, worker, opponent);
            match.moveWorker(opponent, moveOpponent, false);
        }
        match.moveWorker(worker, actuaMovelIndex, true);
        if(checkWin(match, worker)){
            setWinner(true);
            return;
        }
        ArrayList<Index> possibleBuild = whereToBuild(match, worker, worker.getPosition());
        if(possibleBuild.isEmpty()){
            setInGame(false);
            return;
        }
        //take index2 where to build from view
        Index tempBuildIndex = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
        Index actualBuildIndex = correctIndex(match,tempBuildIndex);
        match.build(worker, actualBuildIndex);
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
                            Boolean forbiddenCell = true;
                            while(z <= currentZ +1){
                                Index checkedIndex = new Index(x,y,z);
                                Cell checkedCell = match.selectCell(checkedIndex);
                                if(checkedCell.isDome()){
                                    break;
                                }
                                if(checkedCell.getWorker()!=null && checkedCell.getWorker()!=worker && checkedCell.getWorker().getOwner()==worker.getOwner())
                                    break;
                                if(!checkedCell.isBuilding()){
                                    if(checkedCell.getWorker() == null || checkedCell.getWorker()==worker) {
                                        ArrayList<Invisible> invisibles = checkedCell.getForbidden();
                                        forbiddenCell = false;
                                        for (Invisible inv : invisibles) {
                                            if (inv instanceof ForbiddenMove && inv.isIn(worker)) {
                                                forbiddenCell = true;
                                                break;
                                            }
                                        }
                                        if (!forbiddenCell) {
                                            cellsWhereToMove.add(checkedIndex);
                                            forbiddenCell = true;
                                        }
                                        break;
                                    }
                                    else{
                                        Worker opponent = checkedCell.getWorker();
                                        Index indexForward = indexForward(match, worker, opponent);
                                        if(indexForward == null)
                                            break;
                                        ArrayList<Invisible> invisibles = checkedCell.getForbidden();
                                        forbiddenCell = false;
                                        for (Invisible inv : invisibles) {
                                            if (inv instanceof ForbiddenMove && inv.isIn(worker)) {
                                                forbiddenCell = true;
                                                break;
                                            }
                                        }
                                        if (!forbiddenCell) {
                                            cellsWhereToMove.add(checkedIndex);
                                            forbiddenCell = true;
                                        }
                                        break;
                                    }
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

    /**
     * used for knowing the index where current player's worker can push opponent's worker,
     * if there is one and if he can
     * @param match the current match
     * @param myWorker the worker the player chose to move
     * @param opponent opponent's worker to move forward
     * @return the index where to move opponent's worker
     */
    private Index indexForward(Match match, Worker myWorker, Worker opponent){
        int currentX = myWorker.getPosition().getX();
        int currentY = myWorker.getPosition().getY();
        int nextX = opponent.getPosition().getX();
        int nextY = opponent.getPosition().getY();
        int newX;
        int newY;
        if(currentX > nextX)
            newX = nextX-1;
        else if(currentX == nextX)
            newX = nextX;
        else
            newX = nextX+1;
        if(newX<0 || newX>4)
            return null;
        if(currentY > nextY)
            newY = nextY-1;
        else if(currentY == nextY)
            newY = nextY;
        else
            newY = nextY+1;
        if(newY<0 || newY>4)
            return null;
        int newZ = 0;
        while(newZ < 4) {
            Index checkedIndex = new Index(newX, newY, newZ);
            Cell checkedCell = match.selectCell(checkedIndex);
            if (checkedCell.isDome() || checkedCell.getWorker() != null) {
                return null;
            }
            if (!checkedCell.isBuilding()) {
                ArrayList<Invisible> invisibles = checkedCell.getForbidden();
                for (Invisible inv : invisibles) {
                    if (inv instanceof ForbiddenMove && inv.isIn(opponent)) {
                        return null;
                    }
                }
                return checkedIndex;
            }
            newZ++;
        }
        return null;
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
