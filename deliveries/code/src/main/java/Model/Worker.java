package Model;

public class Worker {
    private int idWorker;
    private Index position;
    private Player owner;

    public void setIdWorker(int id){
        this.idWorker=id;
    }

    public int getIdWorker(){
        int x=idWorker;
        return x;
    }

    public void setOwner(Player p){
        owner=p;
    }
    public Player getOwner() {
        return owner;
    }



    /**
     * can only build buildings
     * @param c != null
     */
    public void build(Cell c) {
        c.setBuilding();
    }

    public void buildDome(Cell c){
        c.setBuilding();
        c.setDome();
    }

    public void move(Cell c){
       c.setWorker(this);
       setPosition(c.getIndex());
    }

    public void delete(Cell c){
        c.setWorker(null);
        setPosition(null);
    }

    public Index getPosition() {
        return position;
    }

    public void setPosition(Index i){
        position= i;
    }

}
