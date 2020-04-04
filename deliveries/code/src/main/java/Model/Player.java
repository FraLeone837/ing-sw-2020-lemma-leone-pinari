package Model;
public class Player {
    private String name;
    private int idPlayer;
    //Need to import package Controller
    //private God yourGod;

    public Player (String name, int idPlayer){
        this.name = name;
        this.idPlayer = idPlayer;
    }

    public int getIdPlayer() {
        int x = idPlayer;
        return x;
    }
    public String getName(){
        return name;
    }
}
