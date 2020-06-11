package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
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

    /**
     * this variable is for storing the position where the worker moves the first time,
     * so if the player wants to move again, he cannot do it in the same position
     */
    private Index prevMoveIndex;

    public void setPrevMoveIndex(Index prevMoveIndex) {
        this.prevMoveIndex = prevMoveIndex;
    }


    @Override
    public String getName() {
        return "Artemis";
    }

    @Override
    public String getDescription() {
        return  "Goddess of the Hunt\n" +
                "Your Move: Your Worker may\n" +
                "move one additional time, but not\n" +
                "back to the space it started on.";
    }

    @Override
    public void turn(Match match, CommunicationProxy communicationProxy, Worker worker) {
        ArrayList<Index> possibleMove = whereToMove(match, worker, worker.getPosition());
        if(possibleMove.isEmpty()){
            setInGame(false);
            return;
        }
        setPrevIndex(worker.getPosition());
        setPrevMoveIndex(worker.getPosition());
        //take index1 where to move the first time
        Index tempMoveIndex = (Index)communicationProxy.sendMessage(Message.MessageType.MOVE_INDEX_REQ, possibleMove);
        Index actualMoveIndex = correctIndex(match,tempMoveIndex);
        match.moveWorker(worker, actualMoveIndex);
        if(checkWin(match, worker)){
            setWinner(true);
            return;
        }
//        Cell cell = match.selectCell(prevMoveIndex);
//        ArrayList<Invisible> invisibles = cell.getForbidden();
//        for (Invisible inv : invisibles) {
//            if (inv instanceof ForbiddenMove && worker.getOwner() == inv.getCreator())
//                inv.addWorker(worker);
//        }
        possibleMove = whereToMove(match, worker, worker.getPosition());
        if(possibleMove.contains(prevMoveIndex))
            possibleMove.remove(prevMoveIndex);
        if(!possibleMove.isEmpty()) {
            //ask to move another time
            Boolean moveAgainAsked = (Boolean) communicationProxy.sendMessage(Message.MessageType.MOVE_AGAIN, "Want to move again?");
            setMoveAgain(moveAgainAsked);
        }
        if(moveAgain) {
            setPrevIndex(worker.getPosition());
            //take index2 where to move a second time
            Index tempMoveIndex2 = (Index)communicationProxy.sendMessage(Message.MessageType.MOVE_INDEX_REQ, possibleMove);
            Index actualMoveIndex2 = correctIndex(match,tempMoveIndex2);
            match.moveWorker(worker, actualMoveIndex2);
            if(checkWin(match, worker)){
                setWinner(true);
                return;
            }
        }
        setMoveAgain(false);
        ArrayList<Index> possibleBuild = whereToBuild(match, worker, worker.getPosition());
        if(possibleBuild.isEmpty()){
            setInGame(false);
            return;
        }
        //take index3 where to build
        Index tempBuildIndex = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
        Index actualBuildIndex = correctIndex(match,tempBuildIndex);
        match.build(worker, actualBuildIndex);
    }

    public void turn(Match match, Worker worker,Index index1,Index index2,Index index3) {
        setPrevIndex(worker.getPosition());
        //take index1 where to move the first time
        match.moveWorker(worker, index1);
        checkWin(match, worker);
        //ask to move another time
        if(moveAgain) {
            Cell cell = match.selectCell(prevIndex);
            ArrayList<Invisible> invisibles = cell.getForbidden();
            for (Invisible inv : invisibles) {
                if (inv instanceof ForbiddenMove && worker.getOwner() == inv.getCreator())
                    inv.addWorker(worker);
            }
            setPrevIndex(worker.getPosition());
            //take index2 where to move a second time
            match.moveWorker(worker, index2);
            checkWin(match, worker);
            resetPower(match, worker);
        }
        //take index3 where to build
        match.build(worker, index3);
    }

//    @Override
//    public void setup(Match match, Player player) {
//        for(int x=0; x<5; x++){
//            for(int y=0; y<5; y++){
//                for(int z=0; z<4; z++){
//                    Index index = new Index(x,y,z);
//                    Invisible invisible = new ForbiddenMove(player);
//                    match.buildInvisible(invisible, index);
//                }
//            }
//        }
//    }

//    @Override
//    public void resetPower(Match match, Worker worker) {
//        setMoveAgain(false);
//        Cell cell = match.selectCell(prevMoveIndex);
//        ArrayList<Invisible> invisibles = cell.getForbidden();
//        for(Invisible inv : invisibles){
//            if(inv instanceof ForbiddenMove && worker.getOwner()==inv.getCreator())
//                inv.removeWorkers();
//        }
//    }

    /**
     * allows you to know if you can end the turn with the selected worker by moving once
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @return true if you can end the turn with the worker
     */
    private Boolean canMoveOnce(Match match, Worker worker){
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
     * allows you to know if you can end the turn with the selected worker by moving twice
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @return true if you can end the turn with the worker moving twice
     */
    private Boolean canMoveTwice(Match match, Worker worker){
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

    @Override
    public Boolean canMove(Match match, Worker worker){
        if(canMoveOnce(match, worker) || canMoveTwice(match, worker))
            return true;
        return false;
    }
}