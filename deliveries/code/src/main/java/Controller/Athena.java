package Controller;

import Model.*;

import java.util.ArrayList;

public class Athena implements God {

    /**
     * this variable is for store the previous position of the worker that moves so, if the worker moves up,
     * opponents' workers cannot move up during this turn
     */
    private Index prevIndex;

    public void setPrevIndex(Index prev){
        prevIndex=prev;
    }

    @Override
    public String getName() {
        return "Athena";
    }

    @Override
    public String getDescription() {
        return "Goddess of Wisdom\n" +
                "Opponent’s Turn: If one of your\n" +
                "Workers moved up on your last\n" +
                "turn, opponent Workers cannot\n" +
                "move up this turn.";
    }

    @Override
    public void turn(Match m, Worker w) {
            //Stub
            Index index1 = new Index(1,3,3);
        reset(m, w);
        setPrevIndex(w.getPosition());
        //take index1 where to move from view
        m.moveWorker(w, index1);
        //take index2 where to build from view
            //Stub
            Index index2 = new Index(1,2,3);
        m.build(w, index2);
        usePower(m, w);
    }

    public void turn(Match m, Worker w,Index index1, Index index2) {
        reset(m, w);
        setPrevIndex(w.getPosition());
        //take index1 where to move from view
        m.moveWorker(w, index1);
        //take index2 where to build from view
        m.build(w, index2);
        usePower(m, w);
    }

    /**
     * this private method is for removing all the workers from the forbiddenMove blocks, since this power lasts just for a turn.
     * in case, they will be set again in the blocks eventually
     *
     * @param m the match that the server is managing
     * @param w the worker that the player chose to move
     */
    private void reset(Match m, Worker w){
        for(int x=0; x<5; x++){
            for(int y=0; y<5; y++){
                for(int z=0; z<4; z++){
                    Index i=new Index(x,y,z);
                    ArrayList<Invisible> invisibles =m.selectCell(i).getForbidden();
                    for(Invisible inv : invisibles){
                        if(inv instanceof ForbiddenMove && w.getOwner()==inv.getCreator())
                            inv.removeWorkers();
                    }
                }
            }
        }
    }

    /**
     * put the opponents' workers in the forbiddenMove blocks if your worker moves up during this turn
     *
     * @param m the match that the server is managing
     * @param w the worker that the player chose to move
     */
    private void usePower(Match m, Worker w){
        if(prevIndex.getZ() < w.getPosition().getZ()){
            for (Player p : m.getPlayers()){
                if(p.getIdPlayer() != w.getOwner().getIdPlayer()){
                    Worker w1= p.getWorker1();
                    Index i1= w1.getPosition();
                    if(i1.getZ()<3){
                        for (int x = 0; x < 5; x++) {
                            for (int y = 0; y < 5; y++) {
                                Cell c1 = m.selectCell(new Index(x, y, i1.getZ() + 1));
                                ArrayList<Invisible> invisibles= c1.getForbidden();
                                for(Invisible inv : invisibles){
                                    if(inv instanceof ForbiddenMove && w.getOwner()==inv.getCreator())
                                        inv.addWorker(w1);
                                }
                            }
                        }
                    }
                    Worker w2= p.getWorker2();
                    Index i2= w2.getPosition();
                    if(i2.getZ()<3){
                        for (int x = 0; x < 5; x++) {
                            for (int y = 0; y < 5; y++) {
                                Cell c2 = m.selectCell(new Index(x, y, i2.getZ() + 1));
                                ArrayList<Invisible> invisibles= c2.getForbidden();
                                for(Invisible inv : invisibles){
                                    if(inv instanceof ForbiddenMove && w.getOwner()==inv.getCreator())
                                        inv.addWorker(w2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void setup(Match m, Player p) {
        for(int x=0; x<5; x++){
            for(int y=0; y<5; y++){
                for(int z=0; z<4; z++){
                    Index i=new Index(x,y,z);
                    m.buildInvisible(new ForbiddenMove(p), i);
                }
            }
        }
    }
}