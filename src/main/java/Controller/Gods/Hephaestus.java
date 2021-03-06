package Controller.Gods;

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

    public void manageBuild(Match match, CommunicationProxy communicationProxy, Worker worker, ArrayList<Index> possibleBuild){
        //take index2 where to build
        Index tempBuildIndex = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
        Index actualBuildIndex = correctIndex(match,tempBuildIndex);
        setPrevBuildIndex(actualBuildIndex);
        match.build(worker, actualBuildIndex);
        possibleBuild = whereToBuild(match, worker, worker.getPosition());
        Index checkedIndex = new Index(prevBuildIndex.getX(), prevBuildIndex.getY(), prevBuildIndex.getZ()+1);
        if(prevBuildIndex.getZ()<2 && possibleBuild.contains(checkedIndex)) {
            //ask to build another time
            Boolean buildAgainAsked = (Boolean) communicationProxy.sendMessage(Message.MessageType.BUILD_AGAIN, "Want to build again?");
            setBuildAgain(buildAgainAsked);
        }
        if(buildAgain) {
            match.build(worker, checkedIndex);
            setBuildAgain(false);
        }
    }
}
