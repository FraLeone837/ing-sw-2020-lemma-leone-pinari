package Model;

public class ForbiddenWin extends Invisible {
    private boolean allGame;
    public ForbiddenWin(Player creator,boolean lastsAllGame){
        super(creator);
        this.allGame = lastsAllGame;
    }
}
