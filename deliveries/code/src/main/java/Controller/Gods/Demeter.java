package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
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
        return  "Goddess of the Harvest\n" +
                "Your Build: Your Worker may\n" +
                "build one additional time, but not\n" +
                "on the same space.";
    }

    @Override
    public void turn(Match match, CommunicationProxy communicationProxy, Worker worker) {
        ArrayList<Index> possibleMove = whereToMove(match, worker, worker.getPosition());
        if(possibleMove.isEmpty()){
            setInGame(false);
            return;
        }
        setPrevIndex(worker.getPosition());
        //take index1 where to move the first time from the view
        Index tempMoveIndex = (Index)communicationProxy.sendMessage(Message.MessageType.MOVE_INDEX_REQ, possibleMove);
        Index actualMoveIndex = correctIndex(match,tempMoveIndex);
        match.moveWorker(worker, actualMoveIndex);
        if(checkWin(match, worker)){
            setWinner(true);
            return;
        }
        ArrayList<Index> possibleBuild = whereToBuild(match, worker, worker.getPosition());
        if(possibleBuild.isEmpty()){
            setInGame(false);
            return;
        }
        //take index2 where to build
        Index tempBuildIndex = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
        Index actualBuildIndex = correctIndex(match,tempBuildIndex);
        setPrevBuildIndex(new Index(actualBuildIndex.getX(), actualBuildIndex.getY(), actualBuildIndex.getZ()+1));
        match.build(worker, actualBuildIndex);
        possibleBuild = whereToBuild(match, worker, worker.getPosition());
        if(possibleBuild.contains(prevBuildIndex))
            possibleBuild.remove(prevBuildIndex);
        if(!possibleBuild.isEmpty()) {
            //ask to build another time
            Boolean buildAgainAsked = (Boolean) communicationProxy.sendMessage(Message.MessageType.BUILD_AGAIN, "Want to build again?");
            setBuildAgain(buildAgainAsked);
        }
        if(buildAgain) {
            //take index3 where to build a second time
            Index tempBuildIndex2 = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
            Index actualBuildIndex2 = correctIndex(match,tempBuildIndex2);
            match.build(worker, actualBuildIndex2);
        }
        setBuildAgain(false);
    }



}