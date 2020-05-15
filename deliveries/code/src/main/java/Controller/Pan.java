package Controller;

import Model.*;

import java.util.ArrayList;

public class Pan extends God {

    @Override
    public String getName() {
        return "Pan";
    }

    @Override
    public String getDescription() {
        return  "God of the Wild\n" +
                "Win Condition: You also win if\n" +
                "your Worker moves down two or\n" +
                "more levels.";
    }

    @Override
    public Boolean checkWin(Match match, Worker worker) {
        Index currentIndex = worker.getPosition();
        if((prevIndex.getZ()==2 && currentIndex.getZ()==3) || (prevIndex.getZ()-currentIndex.getZ()>=2)){
            Cell currentCell = match.selectCell(currentIndex);
            ArrayList<Invisible> invisibles = currentCell.getForbidden();
            for(Invisible inv : invisibles){
                if(inv instanceof ForbiddenWin){
                    if(inv.isIn(worker))
                        return false;
                }
            }
            return true;
        }
        return false;
    }
}
