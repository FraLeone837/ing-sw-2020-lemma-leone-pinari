package Model;

import java.util.ArrayList;

public class Cell {
    private boolean dome;
    private boolean building;
    private Worker worker;
    private ArrayList<Invisible> forbidden = new ArrayList<Invisible>();

    public Cell(){
        this.dome = false;
        this.building = false;
        this.worker = null;
    }

    /**
     * if dome is true, then it's not empty (res = false)
     * if building is true, then it's not empty (res = false)
     * if both building and dome are false then (res = true)
     */
    public boolean isEmpty(){
        if(!(worker.equals(null)))
            return true;
        return !(dome || building);
    }

    public void setBuilding() {
        this.building = true;
    }

    public void setDome(){
        this.setBuilding();
        dome = true;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Worker getWorker() {
        return worker;
    }

    /**
     *
     * @param forbidden != null
     */
    public void addForbidden(Invisible forbidden){
        this.forbidden.add(forbidden);
    }
}
