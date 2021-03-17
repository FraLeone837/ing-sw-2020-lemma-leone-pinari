package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Controller.Gods.God;
import Model.Index;
import Model.Match;
import Model.Player;
import Model.Worker;

import java.util.ArrayList;

public class Poseidon extends God {

    @Override
    public String getName() {
        return "Poseidon";
    }

    @Override
    public String getDescription() {
        return "God of the Sea\n" +
                "End of Your Turn: If your unmoved\n" +
                "Worker is on the ground level,\n" +
                "it may build up to three times in\n" +
                "neighboring spaces.";
    }

    @Override
    public void manageBuild(Match match, CommunicationProxy communicationProxy, Worker worker, ArrayList<Index> possibleBuild){
        build(match, communicationProxy, worker, possibleBuild);
        worker = otherWorker(worker);
        if(worker.getPosition().getZ()>0)
            return;
        possibleBuild = whereToBuild(match, worker, worker.getPosition());
        if(possibleBuild.isEmpty())
            return;
        //ask to build with other worker
        Boolean buildWithOther = (Boolean) communicationProxy.sendMessage(Message.MessageType.BUILD_OTHER_WORKER, "Do you want to build with the other worker?");
        //Boolean buildWithOther = (Boolean) communicationProxy.sendMessage(Message.MessageType.BUILD_AGAIN, "Want to build with other worker?");
        if(!buildWithOther)
            return;
        build(match, communicationProxy, worker, possibleBuild);
        for(int i=0; i<2; i++) {
            possibleBuild = whereToBuild(match, worker, worker.getPosition());
            if (possibleBuild.isEmpty())
                return;
            //ask to build another time
            Boolean buildAgain = (Boolean) communicationProxy.sendMessage(Message.MessageType.BUILD_AGAIN, "Want to build again?");
            if (!buildAgain)
                return;
            build(match, communicationProxy, worker, possibleBuild);
        }
    }

    public void build(Match match, CommunicationProxy communicationProxy, Worker worker, ArrayList<Index> possibleBuild){
        //take index2 where to build
        Index tempBuildIndex = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
        Index actualBuildIndex = correctIndex(match,tempBuildIndex);
        match.build(worker, actualBuildIndex);
    }

    private Worker otherWorker(Worker actualWorker){
        Player owner = actualWorker.getOwner();
        if(actualWorker == owner.getWorker1())
            return owner.getWorker2();
        else
            return owner.getWorker1();
    }
}
