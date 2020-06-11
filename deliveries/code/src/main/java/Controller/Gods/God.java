package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public abstract class God {

    /**
     * @return a constant string containing god's name
     */
    public abstract String getName();

    /**
     * @return a constant string containing god's power description
     */
    public abstract String getDescription();

    Boolean inGame;

    public void setInGame(Boolean bool){
        this.inGame = bool;
    }

    public Boolean getInGame(){
        return inGame;
    }

    Boolean winner;

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }

    public Boolean getWinner() {
        return winner;
    }

    /**
     * this variable is for store the previous position of the worker that moves,
     * so we can check if the player wins after the movement and, if the god allows the player to move twice,
     * he cannot come back in the previous position when moving the second time
     */
    Index prevIndex;

    public void setPrevIndex(Index prev){
        prevIndex = prev;
    }

    /**
     * this method manages the entire turn, from the movement of the worker to the building, taking into account god's power
     *
     * @param match the match that the server is managing
     * @param worker the worker selected by the player
     */
    public void turn(Match match, CommunicationProxy communicationProxy, Worker worker){
        ArrayList<Index> possibleMove = whereToMove(match, worker, worker.getPosition());
        if(possibleMove.isEmpty()){
            setInGame(false);
            return;
        }
        setPrevIndex(worker.getPosition());
        //take index1 where to move from view
        Index tempMoveIndex = (Index)communicationProxy.sendMessage(Message.MessageType.MOVE_INDEX_REQ, possibleMove);
        Index actualMoveIndex = correctIndex(match,tempMoveIndex);
        match.moveWorker(worker,actualMoveIndex);
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

    public void turn(Match match, Worker worker,Index index1,Index index2) {
        setPrevIndex(worker.getPosition());
        //take index1 where to move from view
        Index tempMoveIndex = index1;
        Index actualMoveIndex = correctIndex(match,tempMoveIndex);
        match.moveWorker(worker,actualMoveIndex);
        checkWin(match, worker);
        //take index2 where to build from view
        Index actualBuildIndex = index2;
        match.build(worker, actualBuildIndex);
    }

    /**
     * helps using powers in general, for example putting invisible block in the cells when there are to many to put
     *
     * @param match the match that the server is managing
     * @param worker the worker that the player chose to move
     */
    public void usePower(Match match, Worker worker){}


    /**
     * this method is used at the beginning of the match to prepare the game board io order to use the power of gods,
     * for example putting the invisible blocks in every cell
     *
     * @param match the match that the server is managing
     * @param player the player whose god's power has to be set
     */
    public void setup(Match match, Player player){}

    /**
     * delete all the workers from the invisible blocks managed by the current player when his god's power ends
     *
     * @param match the current match
     * @param worker the worker selected by the player
     */
    public void resetPower(Match match, Worker worker){}

    /**
     * allows you to know where you could move a worker if it was in a specific cell, taking forbiddenMove-blocks into account
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @param index the index where you imagine the worker is
     * @return the list of indexes of the cells where the worker can moves
     */
    public ArrayList<Index> whereToMove(Match match, Worker worker, Index index){
        ArrayList<Index> cellsWhereToMove = new ArrayList<Index>();
        int currentX = index.getX();
        int currentY = index.getY();
        int currentZ = index.getZ();
        Boolean forbiddenCell = true;
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
                                    if(checkedCell.getWorker() == null || checkedCell.getWorker()==worker) {
                                        ArrayList<Invisible> invisibles = checkedCell.getForbidden();
                                        forbiddenCell = false;
                                        for (Invisible inv : invisibles) {
                                            if (inv instanceof ForbiddenMove && inv.isIn(worker)) {
                                                forbiddenCell = true;
                                                break;
                                            }
                                        }
                                    }
                                    else{
                                        forbiddenCell = true;
                                        break;
                                    }
                                    if (!forbiddenCell) {
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

    /**
     * allows you to know where you could build with a worker if it was in a specific cell, taking forbiddenMove-blocks into account
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @param index the index where you imagine the worker is
     * @return the list of indexes of the cells where the worker can builds
     */
    public ArrayList<Index> whereToBuild(Match match, Worker worker, Index index){
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
                    }
                }
            }
        }
        return cellsWhereToBuild;
    }

    /**
     * allows you to know if you can end the turn with the selected worker
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @return true if you can end the turn with the worker
     */
    public Boolean canMove(Match match, Worker worker){
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

    /**
     * @return true if a winning condition is verified, taking the power of god into account
     */
    public Boolean checkWin(Match match, Worker worker){
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

    /**
     * take from the view a two-dimensional index with no Z info and convert it into a three-dimensional index,
     * according to the current game board
     *
     * @param match the current match
     * @param index the index from the view
     * @return the correct index for the match
     */
    public Index correctIndex(Match match, Index index){
        int x = index.getX();
        int y = index.getY();
        Index currentIndex = index;
        for(int z=0; z<4; z++){
            currentIndex = new Index(x,y,z);
            if(match.selectCell(currentIndex).isEmpty())
                return currentIndex;
        }
        return currentIndex;
    }
}
