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
     * can only build buildings, not dome
     * @param c != null
     */
    public void build(Cell c) {
        c.setBuilding();
    }

    public void buildDome(Cell c){
        c.setBuilding();
        c.setDome();
    }

    /**
     * put the worker in a cell and updates its position, without removing it in the previous
     *
     * @param c the cell where to put the worker
     */
    public void move(Cell c){
       c.setWorker(this);
       setPosition(c.getIndex());
    }

    /**
     * removes the player from its current cell and set its position to null
     * @param c the current cell of the worker
     */
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
