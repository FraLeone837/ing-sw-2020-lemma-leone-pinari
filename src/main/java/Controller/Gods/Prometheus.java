package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Controller.Communication.Message;
import Model.*;

import java.util.ArrayList;

public class Prometheus extends God {

    /**
     * this attribute is for asking the player if he wants to build before he moves.
     * in this case, he cannot move up in the same turn.
     */
    private Boolean buildBefore;

    public void setBuildBefore(Boolean buildBefore) {
        this.buildBefore = buildBefore;
    }

    @Override
    public String getName() {
        return "Prometheus";
    }

    @Override
    public String getDescription() {
        return "Titan Benefactor of Mankind\n" +
                "Your Turn: If your Worker does\n" +
                "not move up, it may build both\n" +
                "before and after moving.";
    }

    @Override
    public void turn(Match match, CommunicationProxy communicationProxy, Worker worker) {
        ArrayList<Index> possibleBuild = whereToBuild(match, worker, worker.getPosition());
        buildBeforeMoving(match, communicationProxy, worker, possibleBuild);
        ArrayList<Index> possibleMove = whereToMove(match, worker, worker.getPosition());
        if(possibleMove.isEmpty()){
            setInGame(false);
            return;
        }
        manageMove(match, communicationProxy, worker, possibleMove);
        if(checkWin(match, worker)){
            setWinner(true);
            return;
        }
        possibleBuild = whereToBuild(match, worker, worker.getPosition());
        if(possibleBuild.isEmpty()){
            setInGame(false);
            return;
        }
        manageBuild(match, communicationProxy, worker, possibleBuild);
        if(buildBefore) {
            resetPower(match, worker);
        }
    }

    public void buildBeforeMoving(Match match, CommunicationProxy communicationProxy, Worker worker, ArrayList<Index> possibleBuild){
        if(!possibleBuild.isEmpty()) {
            //ask the player if he wants to move before building
            Boolean buildBeforeAsked = (Boolean) communicationProxy.sendMessage(Message.MessageType.BUILD_BEFORE, "Want to build before to move?");
            setBuildBefore(buildBeforeAsked);
        }
        if(buildBefore) {
            manageBuild(match, communicationProxy, worker, possibleBuild);
            usePower(match, worker);
        }
    }

    @Override
    public void usePower(Match match, Worker worker){
        int upperZ = worker.getPosition().getZ()+1;
        if(upperZ>3){
            return;
        }
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                Cell cell = match.selectCell(new Index(x, y, upperZ));
                ArrayList<Invisible> invisibles = cell.getForbidden();
                for (Invisible inv : invisibles) {
                    if (inv instanceof ForbiddenMove && worker.getOwner() == inv.getCreator())
                        inv.addWorker(worker);
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
        setBuildBefore(false);
        int upperZ = prevIndex.getZ()+1;
        if(upperZ>3){
            return;
        }
        for(int x=0; x<5; x++){
            for(int y=0; y<5; y++){
                Index index = new Index(x,y,upperZ);
                ArrayList<Invisible> invisibles = match.selectCell(index).getForbidden();
                for(Invisible inv : invisibles){
                    if(inv instanceof ForbiddenMove && worker.getOwner()==inv.getCreator())
                        inv.removeWorkers();
                }
            }
        }
    }
}
