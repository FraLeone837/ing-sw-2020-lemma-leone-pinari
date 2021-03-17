package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class Athena extends God {

    @Override
    public String getName() {
        return "Athena";
    }

    @Override
    public String getDescription() {
        return  "Goddess of Wisdom\n" +
                "Opponent’s Turn: If one of your\n" +
                "Workers moved up on your last\n" +
                "turn, opponent Workers cannot\n" +
                "move up this turn.";
    }

    @Override
    public void turn(Match match, CommunicationProxy communicationProxy, Worker worker){
        resetPower(match, worker);
        super.turn(match, communicationProxy, worker);
    }

    @Override
    public void manageBuild(Match match, CommunicationProxy communicationProxy, Worker worker, ArrayList<Index> possibleBuild){
        //take index2 where to build from view
        Index tempBuildIndex = (Index)communicationProxy.sendMessage(Message.MessageType.BUILD_INDEX_REQ, possibleBuild);
        Index actualBuildIndex = correctIndex(match,tempBuildIndex);
        match.build(worker, actualBuildIndex);
        if(prevIndex.getZ() < worker.getPosition().getZ())
            usePower(match, worker);
    }


    @Override
    public void usePower(Match match, Worker worker){
        ArrayList<Player> players = match.getPlayers();
        for (Player player : players){
            if(player != worker.getOwner()) {
                Worker w1 = player.getWorker1();
                if(w1 != null){
                    Index i1 = w1.getPosition();
                    int upperZ1 = i1.getZ() + 1;
                    if (upperZ1 < 4) {
                        for (int x = 0; x < 5; x++) {
                            for (int y = 0; y < 5; y++) {
                                Cell c1 = match.selectCell(new Index(x, y, upperZ1));
                                ArrayList<Invisible> invisibles = c1.getForbidden();
                                for (Invisible inv : invisibles) {
                                    if (inv instanceof ForbiddenMove && worker.getOwner() == inv.getCreator())
                                        inv.addWorker(w1);
                                }
                            }
                        }
                    }
                }
                Worker w2 = player.getWorker2();
                if (w2 != null) {
                    Index i2 = w2.getPosition();
                    int upperZ2 = i2.getZ() + 1;
                    if (upperZ2 < 4) {
                        for (int x = 0; x < 5; x++) {
                            for (int y = 0; y < 5; y++) {
                                Cell c2 = match.selectCell(new Index(x, y, upperZ2));
                                ArrayList<Invisible> invisibles = c2.getForbidden();
                                for (Invisible inv : invisibles) {
                                    if (inv instanceof ForbiddenMove && worker.getOwner() == inv.getCreator())
                                        inv.addWorker(w2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setup(Match match, Player player) {
        for(int x=0; x<5; x++){
            for(int y=0; y<5; y++){
                for(int z=1; z<4; z++){
                    Index index = new Index(x,y,z);
                    Invisible invisible = new ForbiddenMove(player);
                    match.buildInvisible(invisible, index);
                }
            }
        }
    }


    @Override
    public void resetPower(Match match, Worker worker){
        for(int x=0; x<5; x++){
            for(int y=0; y<5; y++){
                for(int z=1; z<4; z++){
                    Index index = new Index(x,y,z);
                    ArrayList<Invisible> invisibles = match.selectCell(index).getForbidden();
                    for(Invisible inv : invisibles){
                        if(inv instanceof ForbiddenMove && worker.getOwner()==inv.getCreator())
                            inv.removeWorkers();
                    }
                }
            }
        }
    }
}