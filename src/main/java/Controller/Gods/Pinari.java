package Controller.Gods;

import Controller.Communication.CommunicationProxy;
import Model.*;

import java.util.ArrayList;

public class Pinari extends God {

    @Override
    public String getName() {
        return "Pinari";
    }

    @Override
    public String getDescription() {
        return "Goddess of Fra\n" +
                "Your Turn:\n" +
                "You cannot you cannot move\n" +
                "diagonally.\n";
    }

    @Override
    public ArrayList<Index> whereToMove(Match match, Worker worker, Index index){
        ArrayList<Index> cellsWhereToMove = new ArrayList<Index>();
        int currentX = index.getX();
        int currentY = index.getY();
        int currentZ = index.getZ();
        Boolean forbiddenCell = true;
        for(int x = currentX-1; x < currentX+2; x++){
            if(x >= 0 && x < 5){
                for(int y = currentY-1; y < currentY+2; y++){
                    if(y >= 0 && y < 5){
                        if(x != currentX ^ y != currentY){
                            int z=0;
                            while(z <= currentZ +1){
                                Index checkedIndex = new Index(x,y,z);
                                Cell checkedCell = match.selectCell(checkedIndex);
                                if(checkedCell.isDome()){
                                    break;
                                }
                                if(!checkedCell.isBuilding()){
                                    if(checkedCell.getWorker() == null || checkedCell.getWorker()==worker) {
                                        ArrayList<Invisible> invisibles = checkedCell.getForbidden();
                                        forbiddenCell = false;
                                        for (Invisible inv : invisibles) {
                                            if (inv instanceof ForbiddenMove && inv.isIn(worker)) {
                                                forbiddenCell = true;
                                                break;
                                            }
                                        }
                                    }
                                    else{
                                        forbiddenCell = true;
                                        break;
                                    }
                                    if (!forbiddenCell) {
                                        cellsWhereToMove.add(checkedIndex);
                                        forbiddenCell = true;
                                    }
                                    break;
                                }
                                z++;
                            }
                        }
                    }
                }
            }
        }
        return cellsWhereToMove;
    }
}
