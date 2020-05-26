package Model;

import Controller.Communication.IntermediaryClass;
import Controller.Communication.Message;

import java.io.IOException;
import java.lang.management.PlatformLoggingMXBean;
import java.util.ArrayList;

/**
 * this class is used as the interface between the model and the controller, providing all the needed methods to manage a match
 */
public class Match {
    //decide list or normal array
    private ArrayList<Player> players;
    private int idMatch;
    private Island island;

    private IntermediaryClass intermediaryClass;
    private WriterClass wc;

    //decide list or normal array
    public Match(int id){
        players = new ArrayList<Player>();
        idMatch =  id;
        island = new Island();
    }
    public void setIntermediaryClass(IntermediaryClass ic){
        this.intermediaryClass = ic;
    }

    /**
     *
     * @return the list of players
     */
    public ArrayList<Player> getPlayers(){
        return players;
    }

    /**
     * takes a player from the player manager and stores him in the match class
     * @param p the player to be added in match's players list
     */
    public void initPlayers(Player p) {
        players.add(p);
    }

    /**
     * removes a player when he loses
     * @param p the player to be removed from the game board
     */
    public void removePlayer(Player p)
    {
        players.remove(p);
    }

    /**
     * move a worker from a cell to another
     * @param w the worker to move
     * @param i the position where to move the worker
     */
    public void moveWorker(Worker w, Index i){
        if(w != null){
            w.delete(selectCell(w.getPosition()));
            w.move(selectCell(i));
            notifyView();
        }
    }

    /**
     * move a worker from a cell to another
     * @param w the worker to move
     * @param i the position where to move the worker
     * @param print is true if the view has to print the game board on the screen after this movement
     */
    public void moveWorker(Worker w, Index i, Boolean print){
        if(w != null){
            w.delete(selectCell(w.getPosition()));
            w.move(selectCell(i));
            if(print == true)
                notifyView();
        }
    }

    /**
     * build a building in the cell in a given position.Automatically builds a dome if the cell is in the 4th level
     * @param w the worker who builds
     * @param i the index of the cell where to build
     */
    public void build(Worker w, Index i){
        if(i.getZ()==3)
            w.buildDome(selectCell(i));
        else
            w.build(selectCell(i));
        notifyView();
    }

    /**
     * build a dome in a cell in a given position
     * @param w the worker who builds
     * @param i the index of the cell where to build a dome
     */
    public void buildDome(Worker w, Index i){
        w.buildDome(selectCell(i));

    }

    /**
     * add an invisible block in a cell io order to activate gods' powers
     * @param in the invisible block to add in the cell with the index i
     * @param i the index of the cell where to build the invisible block
     */
    public void buildInvisible(Invisible in, Index i){
        selectCell(i).addForbidden(in);
    }

    /**
     * initialize a given worker on the game board at the beginning of the game
     * @param w the worker to initialize in the game board at the beginning of the game
     * @param i the position where to put the worker at the beginning of the game
     */
    public void initWorker(Worker w, Index i){
        if(w!=null) {
            w.move(selectCell(i));
            notifyView();
        }
    }

    /**
     * remove a worker from the game board after the owner player loses or the worker is killed by an enemy god
     * @param w the worker to remove from the game board
     */
    public void removeWorker(Worker w){
        w.delete(selectCell(w.getPosition()));
        notifyView();
    }

    /**
     * provide a copy of a cell in order to check what we want without modify the game board directly
     * @param i the position of the cell we are interested in
     * @return a copy of the cell we are interested in
     */
    public Cell selectCell(Index i){
//to do: deep clone of class island and use it to return a copy of the cell, not the cell itself
        return island.getCell(i);
    }

    /** Creates a 1D copy of the cells as seen from above, with information laid in a certain protocol
     *
     * @return a copy of the key information about the board
     */
    public int[] getInformationArray(){
        Cell cell ;
        int[] informationArray = new int[25];
        if (this.wc == null)
        try{
            this.wc = new WriterClass(true);

        }catch (IOException e){
            e.printStackTrace();
            return new int[1];
        }
        for(int i = 0; i<5; i++){
            for(int j=0; j<5; j++){
                for(int k = 3; k >= 0; k--){
                    cell = selectCell(new  Index(i,j,k));
                    if(!(cell.isEmpty())){
                        if(cell.isBuilding()){
                            if(cell.isDome()){
                                //if k = 3 then dome is built in level 4 else k = 2 dome is built in level 3 (val = 5) else k = 1 (val = 6) else val = 7
                                informationArray[i+5*j] += 4 + k;
                            } else {
                                //if there are no domes built then we give the lowest level of the building built
                                informationArray[i+5*j] += k + 1;
                            }
                        }
                        if(cell.getWorker() != null){
                            //Based on the id we connect the players (10-20 player 1) (30-40 player 2) ecc
                            informationArray[i+5*j] = informationArray[i+5*j] + 10*cell.getWorker().getIdWorker();
                            //next cell might be a building else ground floor
                            continue;
                        }
                        break;
                    }
                    else
                    //else if cell is empty
                    informationArray[i+5*j] = 0;
                }
            }
        }
        try{
            wc.writeOnFile(informationArray);
        } catch (IOException e){
            e.printStackTrace();
        }
        return informationArray;
    }

    /**
     * give to the view all the information it need to draw the game board of the screen
     */
    public void notifyView(){
        if(intermediaryClass == null){
            return;
        }
        intermediaryClass.Broadcast(new Message(Message.MessageType.ISLAND_INFO,getInformationArray()));
    }

}
