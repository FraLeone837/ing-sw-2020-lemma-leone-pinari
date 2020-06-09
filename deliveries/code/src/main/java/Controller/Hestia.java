package Controller;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.Index;
import Model.Match;
import Model.Worker;

import java.util.ArrayList;

public class Hestia extends God{

    @Override
    public String getName() {
        return "Hestia";
    }

    @Override
    public String getDescription() {
        return "Goddess of Hear th and Home\n" +
                "Your Build: Your Worker may\n" +
                "build one additional time. The\n" +
                "additional build cannot be on a\n" +
                "perimeter space.";
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
        match.build(worker, actualBuildIndex);
        possibleBuild = whereToBuild(match, worker, worker.getPosition());
        for(int x=0; x<5; x++) {
            for(int y=0; y<5; y++) {
                if(x==0 || x==4 || y==0 || y==4){
                    for (int z = 0; z < 5; z++) {
                        Index index = new Index(x,y,z);
                        if (possibleBuild.contains(index))
                            possibleBuild.remove(index);
                    }
                }
            }
        }
        if(possibleBuild.isEmpty())
            return;
        //ask to build another time
        Boolean buildAgain = (Boolean) communicationProxy.sendMessage(Message.MessageType.BUILD_AGAIN, "Want to build again?");
        if(buildAgain) {
            //take index3 where to build a second time
            Index tempBuildIndex2 = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
            Index actualBuildIndex2 = correctIndex(match,tempBuildIndex2);
            match.build(worker, actualBuildIndex2);
        }
    }

}
