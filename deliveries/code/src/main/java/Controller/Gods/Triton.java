package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.Index;
import Model.Match;
import Model.Worker;

import java.util.ArrayList;

public class Triton extends God{

    @Override
    public String getName() {
        return "Triton";
    }

    @Override
    public String getDescription() {
        return "God of the Waves\n" +
                "Your Move: Each time your\n" +
                "Worker moves onto a perimeter\n" +
                "space (ground or block), it may\n" +
                "immediately move again.";
    }

    @Override
    public void turn(Match match, CommunicationProxy communicationProxy, Worker worker) {
        ArrayList<Index> possibleMove = whereToMove(match, worker, worker.getPosition());
        if(possibleMove.isEmpty()){
            setInGame(false);
            return;
        }
        setPrevIndex(worker.getPosition());
        //take index1 where to move the first time
        Index tempMoveIndex = (Index)communicationProxy.sendMessage(Message.MessageType.MOVE_INDEX_REQ, possibleMove);
        Index actualMoveIndex = correctIndex(match,tempMoveIndex);
        match.moveWorker(worker, actualMoveIndex);
        if(checkWin(match, worker)){
            setWinner(true);
            return;
        }
        int actualX = worker.getPosition().getX();
        int actualY = worker.getPosition().getY();
        while (actualX==0 || actualX==4 || actualY==0 || actualY==4) {
            possibleMove = whereToMove(match, worker, worker.getPosition());
            if (possibleMove.isEmpty())
                break;
            //ask to move another time
            Boolean moveAgain = (Boolean) communicationProxy.sendMessage(Message.MessageType.MOVE_AGAIN, "Want to move again?");
            if (moveAgain) {
                setPrevIndex(worker.getPosition());
                //take index2 where to move a second time
                Index tempMoveIndex2 = (Index) communicationProxy.sendMessage(Message.MessageType.MOVE_INDEX_REQ, possibleMove);
                Index actualMoveIndex2 = correctIndex(match, tempMoveIndex2);
                match.moveWorker(worker, actualMoveIndex2);
                if (checkWin(match, worker)) {
                    setWinner(true);
                    return;
                }
            }
            else
                break;
            actualX = worker.getPosition().getX();
            actualY = worker.getPosition().getY();
        }
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
}
