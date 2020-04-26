package Model;

import java.util.ArrayList;

/**
 * this class represents the positions in the game board, either in width, depth and height
 */
public class Cell {
    private Index index;
    private boolean dome;
    private boolean building;
    private Worker worker;
    private ArrayList<Invisible> forbidden = new ArrayList<Invisible>();

    public Cell(int x, int y, int z){
        this.index = new Index(x,y,z);
        this.dome = false;
        this.building = false;
        this.worker = null;
    }

    /**
     * @return a copy of the index of the class
     */
    public Index getIndex() {
        return index;
    }

    /**
     * if dome is true, then it's not empty (res = false)
     * if building is true, then it's not empty (res = false)
     * if both building and dome are false then (res = true)
     *
     * @return a boolean that indicates if there is something in the cell or not
     */
    public boolean isEmpty(){
        if(worker != null)
            return false;

        return !(dome || building);
    }

    /**
     * builds a normal building, not a dome
     */
    public void setBuilding() {
        this.building = true;
    }

    /**
     * builds a dome
     */
    public void setDome(){
        this.setBuilding();
        dome = true;
    }

    public boolean isDome() {
        return dome;
    }

    public boolean isBuilding() {
        return building;
    }

    /**
     * put a worker in the cell
     *
     * @param worker the worker to put in the cell
     */
    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Worker getWorker() {
        return worker;
    }


    /**
     *adds an invisible block in the cell
     *
     * @param forbidden != null
     */
    public void addForbidden(Invisible forbidden){
        this.forbidden.add(forbidden);
    }

    public void removeForbidden(Invisible forbidden) {this.forbidden.remove(forbidden);}

    public ArrayList<Invisible> getForbidden(){return forbidden;}

}
