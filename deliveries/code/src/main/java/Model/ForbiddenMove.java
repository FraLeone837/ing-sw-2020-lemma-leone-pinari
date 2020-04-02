package Model;


public class ForbiddenMove extends Invisible {
    /**
     * Contains a list (dim=4) of the _id_ of all the workers that
     *      cannot move on a certain cell
     */
    private int[] idList;
    public ForbiddenMove(){
        this.idList = new int[4];
    }

    public ForbiddenMove(Player creator){
        super(creator);
        this.idList = new int[4];
    }

}
