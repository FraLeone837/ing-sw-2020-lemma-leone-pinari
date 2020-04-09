package Model;

import java.util.ArrayList;

/**
 * this class is used as the interface between the model and the controller, providing all the needed methods to manage a match
 */
public class Match {
    //decide list or normal array
    private ArrayList<Player> players;
    private int idMatch;
    private Island island;


    //decide list or normal array
    public Match(int id){
        players = new ArrayList<Player>();
        idMatch =  id;
        island = new Island();
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
        w.delete(island.getCellByIndex(i));
        w.move(island.getCellByIndex(i));

    }

    /**
     * build a building, not a dome, in the cell in a given position
     * @param w the worker who builds
     * @param i the index of the cell where to build a normal building, not a dome
     */
    public void build(Worker w, Index i){
        w.build(island.getCellByIndex(i));
    }

    /**
     * build a dome in a cell in a given position
     * @param w the worker who builds
     * @param i the index of the cell where to build a dome
     */
    public void buildDome(Worker w, Index i){
        w.buildDome(island.getCellByIndex(i));
    }

    /**
     * add an invisible block in a cell io order to activate gods' powers
     * @param in the invisible block to add in the cell with the index i
     * @param i the index of the cell where to build the invisible block
     */
    public void buildInvisible(Invisible in, Index i){
        island.getCellByIndex(i).addForbidden(in);
    }

    /**
     * initialize a given worker on the game board at the beginning of the game
     * @param w the worker to initialize in the game board at the beginning of the game
     * @param i the position where to put the worker at the beginning of the game
     */
    public void initWorker(Worker w, Index i){
        w.move(island.getCellByIndex(i));
    }

    /**
     * remove a worker from the game board after the owner player loses or the worker is killed by an enemy god
     * @param w the worker to remove from the game board
     */
    public void removeWorker(Worker w){
        w.delete(island.getCellByIndex(w.getPosition()));
    }

    /**
     * provide a copy of a cell in order to check what we want without modify the game board directly
     * @param i the position of the cell we are interested in
     * @return a copy of the cell we are interested in
     */
    public Cell selectCell(Index i){
//to do: deep clone of class island and use it to return a copy of the cell, not the cell itself
        return island.getCellByIndex(i);
    }

    /**
     * give to the view all the information it need to draw the game board of the screen
     */
    public void notifyView(){

    }

}
