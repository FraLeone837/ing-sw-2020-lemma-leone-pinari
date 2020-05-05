package Controller;

import Model.*;

import java.util.ArrayList;

public abstract class God {

    /**
     * @return a constant string containing god's name
     */
    public abstract String getName();

    /**
     * @return a constant string containing god's power description
     */
    public abstract String getDescription();

    /**
     * this variable is for store the previous position of the worker that moves so, if the player wants to move again,
     * he cannot come back in the previous position
     */
    Index prevIndex;

    /**
     * stores the previous index of a worker in an attribute when it moves, in order to check if the owner win
     * @param prev the index of the starting cell of the worker
     */
    public void setPrevIndex(Index prev){
        prevIndex = prev;
    }

    /**
     * this method manages the entire turn, from the movement of the worker to the building, taking into account god's power
     *
     * @param m the match that the server is managing
     * @param w the worker selected by the player
     */
    public void turn(Match m, Worker w){
        setPrevIndex(w.getPosition());
        //take index1 where to move from view
            //stub
            Index index1 = new Index(1,2,3);
        m.moveWorker(w,index1);
        checkWin(m, w);
        //take index2 where to build from view
            //Stub
            Index index2 = new Index(2,3,0);
        m.build(w, index2);
    }


    /**
     * this method is used at the beginning of the match to do actions that will be active for the whole duration of the game,
     * such as putting the invisible blocks in every cell
     *
     * @param m the match that the server is managing
     * @param p the player whose god's power has to be set
     */
    public void setup(Match m, Player p){}

    /**
     * delete all the workers from the invisible blocks managed by the current player when his god's power ends
     *
     * @param m the current match
     * @param w the worker selected by the player
     */
    public void resetPower(Match m, Worker w){}

    /**
     * allows you to know where you could move a worker if it was in a specific cell, taking forbiddenMove-blocks into account
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @param index the index where you imagine the worker is
     * @return the list of indexes of the cells where the worker can moves
     */
    public ArrayList<Index> whereToMove(Match match, Worker worker, Index index){
        ArrayList<Index> cellsWhereToMove = new ArrayList<Index>();
        int currentX = index.getX();
        int currentY = index.getY();
        int currentZ = index.getZ();
        for(int x = currentX-1; x < currentX+2; x++){
            if(x >= 0 && x < 5){
                for(int y = currentY-1; y < currentY+2; y++){
                    if(y >= 0 && y < 5){
                        if(x != currentX || y != currentY){
                            int z=0;
                            while(z <= currentZ +1){
                                Index checkedIndex = new Index(x,y,z);
                                if(match.selectCell(checkedIndex).isEmpty() || match.selectCell(checkedIndex).getWorker()==worker){
                                    ArrayList<Invisible> invisibles = match.selectCell(checkedIndex).getForbidden();
                                    Boolean forbiddenCell = false;
                                    for(Invisible inv : invisibles){
                                        if(inv instanceof ForbiddenMove && inv.isIn(worker)){
                                            forbiddenCell = true;
                                            break;
                                        }
                                    }
                                    if (!forbiddenCell)
                                        cellsWhereToMove.add(checkedIndex);
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

    /**
     * allows you to know where you could build with a worker if it was in a specific cell, taking forbiddenMove-blocks into account
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @param index the index where you imagine the worker is
     * @return the list of indexes of the cells where the worker can builds
     */
    public ArrayList<Index> whereToBuild(Match match, Worker worker, Index index){
        ArrayList<Index> cellsWhereToBuild = new ArrayList<Index>();
        int currentX = index.getX();
        int currentY = index.getY();
        for(int x = currentX-1; x < currentX+2; x++){
            if(x >= 0 && x < 5){
                for(int y = currentY-1; y < currentY+2; y++){
                    if(y >= 0 && y < 5){
                        if(x != currentX || y != currentY){
                            int z=0;
                            while(z < 4){
                                Index checkedIndex = new Index(x,y,z);
                                if(match.selectCell(checkedIndex).isEmpty() || match.selectCell(checkedIndex).getWorker()==worker){
                                    ArrayList<Invisible> invisibles = match.selectCell(checkedIndex).getForbidden();
                                    Boolean forbiddenCell = false;
                                    for(Invisible inv : invisibles){
                                        if(inv instanceof ForbiddenConstruction && inv.isIn(worker)){
                                            forbiddenCell = true;
                                            break;
                                        }
                                    }
                                    if (!forbiddenCell)
                                        cellsWhereToBuild.add(checkedIndex);
                                    break;
                                }
                                z++;
                            }
                        }
                    }
                }
            }
        }
        return cellsWhereToBuild;
    }

    /**
     * allows you to know if you can end the turn with the selected worker
     *
     * @param match the current match
     * @param worker the worker selected by the player
     * @return true if you can end the turn with the worker
     */
    public Boolean canMove(Match match, Worker worker){
        ArrayList<Index> possibleMoves = whereToMove(match, worker, worker.getPosition());
        if(possibleMoves.isEmpty())
            return false;
        for(Index index : possibleMoves){
            ArrayList<Index> possibleBuildings = whereToBuild(match, worker, index);
            if(!possibleBuildings.isEmpty())
                return true;
        }
        return false;
    }

    /**
     * @return true if a winning condition is verified, taking the power of god into account
     */
    public Boolean checkWin(Match match, Worker worker){
        Index currentIndex = worker.getPosition();
        if(prevIndex.getZ()==2 && currentIndex.getZ()==3){
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

