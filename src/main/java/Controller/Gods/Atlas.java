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
    public void manageBuild(Match match, CommunicationProxy communicationProxy, Worker worker, ArrayList<Index> possibleBuild){
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

}