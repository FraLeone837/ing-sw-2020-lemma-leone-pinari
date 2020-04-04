package Model;

public class Worker {
    private int idWorker;
    private Cell position;
    public Worker(int idWorker){
        this.idWorker = idWorker;
    }
    /**
     * can only build buildings
     * @param c != null
     */
    public void build(Cell c){
        c.setBuilding();
    }

    public void buildDome(Cell c){
        c.setBuilding();
        c.setDome();
    }

    public void move(Cell c){
       c.setWorker(this);
    }

    public void delete(Cell c){
        c.setWorker(null);
    }

    public Cell getPosition() {
        return position;
    }
}
