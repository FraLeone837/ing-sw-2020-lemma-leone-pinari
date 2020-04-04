package Model;

public class Worker {
    private int idWorker;
    private Index position;

    public void setIdWorker(int id){
        this.idWorker=id;
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

    public Index getPosition() {
        return position;
    }

    public void setPosition(Cell c){
        position= c.getIndex();
    }
}
