package Model;
public class Player {
    private String name;
    private int idPlayer;
    private Worker worker1;
    private Worker worker2;
    //Need to import package Controller
    //private God yourGod;

    public Player(String name, int idPlayer) {
        this.name = name;
        this.idPlayer = idPlayer;
        this.worker1= new Worker();
        this.worker2= new Worker();
        worker1.setOwner(this);
        worker2.setOwner(this);
    }

    public int getIdPlayer() {
        int x = idPlayer;
        return x;
    }

    public String getName() {
        return name;
    }

    /**
     * adds an id in the worker1. this action is not done in the constructor because we do not know the id initially
     *
     * @param id the id to set in worker1
     */
    public void setWorker1Id(int id) {
        this.worker1.setIdWorker(id);
    }

    /**
     * adds an id in the worker2. this action is not done in the constructor because we do not know the id initially
     *
     * @param id the id to set in worker2
     */
    public void setWorker2Id(int id) {
        this.worker2.setIdWorker(id);
    }

    public Worker getWorker1(){
        return worker1;
    }

    public Worker getWorker2() {
        return worker2;
    }


}
