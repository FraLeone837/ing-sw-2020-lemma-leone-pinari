package Controller;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class Hephaestus extends God {

    /**
     * this flag is for building once again if the player wants
     */
    private boolean buildAgain;

    public void setBuildAgain(boolean buildAgain) {
        this.buildAgain = buildAgain;
    }

    /**
     * this variable is for storing the position where the worker builds the first time,
     * so if the player wants to build again, that will be the position where he can build
     */
    private Index prevBuildIndex;

    public void setPrevBuildIndex(Index prevBuildIndex) {
        this.prevBuildIndex = prevBuildIndex;
    }

    @Override
    public String getName() {
        return "Hephaestus";
    }

    @Override
    public String getDescription() {
        return  "God of Blacksmiths\n" +
                "Your Build: Your Worker may\n" +
                "build one additional block (not\n" +
                "dome) on top of your first block.";
    }

    @Override
    public void turn(Match m, CommunicationProxy communicationProxy, Worker w) {
        ArrayList<Index> possibleMove = whereToMove(m, w, w.getPosition());
        if(possibleMove.isEmpty()){
            setInGame(false);
            return;
        }
        setPrevIndex(w.getPosition());
        //take index1 where to move the first time from the view
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
        //take index2 where to build
        Index tempBuildIndex = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
        Index actualBuildIndex = correctIndex(m,tempBuildIndex);
        setPrevBuildIndex(actualBuildIndex);
        m.build(w, actualBuildIndex);
        possibleBuild = whereToBuild(m, w, w.getPosition());
        Index checkedIndex = new Index(prevBuildIndex.getX(), prevBuildIndex.getY(), prevBuildIndex.getZ()+1);
        if(prevBuildIndex.getZ()<2 && possibleBuild.contains(checkedIndex)) {
            //ask to build another time
            Boolean buildAgainAsked = (Boolean) communicationProxy.sendMessage(Message.MessageType.BUILD_AGAIN, "Want to build again?");
            setBuildAgain(buildAgainAsked);
        }
        if(buildAgain) {
            m.build(w, checkedIndex);
            setBuildAgain(false);
        }
    }
}
