package Model;

public class Invisible {
    /**
     * adds in the list<<Model.Invisible>> of cell
     * @param c
     */
    private Player creator;

    public Invisible(){
        this.creator = null;
    }

    public Invisible (Player creator){
        this.creator = creator;
    }

    public void putOnCell(Cell c){
        c.addForbidden(this);
    }
}
