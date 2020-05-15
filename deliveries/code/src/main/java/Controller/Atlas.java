package Controller;

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
    public void turn(Match m, CommunicationProxy communicationProxy, Worker w) {
        ArrayList<Index> possibleMove = whereToMove(m, w, w.getPosition());
        if(possibleMove.isEmpty()){
            setInGame(false);
            return;
        }
        setPrevIndex(w.getPosition());
        //take index1 where to move from view
        Index tempMoveIndex = (Index)communicationProxy.sendMessage(Message.MessageType.MOVE_INDEX_REQ, possibleMove);
        Index actualMoveIndex = correctIndex(m,tempMoveIndex);
        m.moveWorker(w, actualMoveIndex);
        if(checkWin(m, w)){
            setWinner(true);
            return;
        }
        ArrayList<Index> possibleBuild = whereToBuild(m, w, w.getPosition());
        if(possibleBuild.isEmpty()){
            setInGame(false);
            return;
        }
        //take index2 where to build from view
        Index tempBuildIndex = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
        Index actualBuildIndex = correctIndex(m,tempBuildIndex);
        //ask to build a building or a dome
        Boolean buildDomeAsked = (Boolean) communicationProxy.sendMessage(Message.MessageType.BUILD_DOME, "Want to build a dome?");
        setBuildDome(buildDomeAsked);
        if(buildDome)
            m.buildDome(w, actualBuildIndex);
        else
            m.build(w, actualBuildIndex);
        setBuildDome(false);
    }

    public void turn(Match m, Worker w, Index index1, Index index2) {
        setPrevIndex(w.getPosition());
        //take index1 where to move from view
        m.moveWorker(w, index1);
        checkWin(m, w);
        //take index2 where to build from view
        //ask to build a building or a dome
        if(buildDome)
            m.buildDome(w, index2);
        else
            m.build(w, index2);
    }
}