package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class Atlas extends God {

    /**
     * this flag is for building a dome if the player wants
     */
    private boolean buildDome;

    public void setBuildDome(boolean buildDome){this.buildDome=buildDome;}

    @Override
    public String getName() {
        return "Atlas";
    }

    @Override
    public String getDescription() {
        return  "Titan Shouldering the Heavens\n" +
                "Your Build: Your Worker\n" +
                "may build a dome at any level\n" +
                "including the ground.";
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
        //take index2 where to build from view
        Index tempBuildIndex = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
        Index actualBuildIndex = correctIndex(match,tempBuildIndex);
        //ask to build a building or a dome
        Boolean buildDomeAsked = (Boolean) communicationProxy.sendMessage(Message.MessageType.BUILD_DOME, "Want to build a dome?");
        setBuildDome(buildDomeAsked);
        if(buildDome)
            match.buildDome(worker, actualBuildIndex);
        else
            match.build(worker, actualBuildIndex);
        setBuildDome(false);
    }

    public void turn(Match match, Worker worker, Index index1, Index index2) {
        setPrevIndex(worker.getPosition());
        //take index1 where to move from view
        match.moveWorker(worker, index1);
        checkWin(match, worker);
        //take index2 where to build from view
        //ask to build a building or a dome
        if(buildDome)
            match.buildDome(worker, index2);
        else
            match.build(worker, index2);
    }
}