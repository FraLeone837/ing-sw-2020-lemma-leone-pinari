package Model;

import Controller.Communication.IntermediaryClass;

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

    public Index getIndex() {
        return index;
    }

    /**
     * Returns a shallow copy of a cell
     * @param cell must already exist in game
     */
    public Cell getCell(Cell cell){
        int idWorker = cell.getWorker().getIdWorker();
        Index indexCopy = cell.getIndex();
        Cell copy = new Cell(indexCopy.getX(), indexCopy.getY(), indexCopy.getZ());
        copy.setWorker(new Worker(idWorker));
        return copy;
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

    @Override
    public String toString() {
        String toReturn = "This cell is found in: " + this.index.toString();
        if(this.worker != null){
            toReturn = toReturn + " it contains worker" + worker.getIdWorker();
        }
        if(this.building){
            if(this.dome){
                toReturn = toReturn + " it contains dome";
            } else{
                toReturn = toReturn + " is building";
            }
        }
        return toReturn + ".";
    }
}
