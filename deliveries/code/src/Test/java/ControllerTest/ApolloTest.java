package ControllerTest;

import Controller.Apollo;
import Controller.God;
import Model.Index;
import Model.Match;
import Model.Worker;
import org.junit.Assert;
import java.util.Random;


import static org.junit.Assert.*;

public class ApolloTest {

    //Workers coordinates before I move
    private int x;
    private int y;
    private int z;

    //Enemy coordinates before I move
    private int ex;
    private int ey;
    private int ez;

    private Match match;
    private Worker myWorker;
    private Worker enemyWorker;
    private Apollo apollo;

    public int absolute(int x){
        if (x >= 0) return x;
        return -x;
    }

    private int getAdjacent(int x){
        Random rand = new Random();
        int v = rand.nextInt(2);
        int sign = rand.nextInt(2);
        if(sign == 1) v = -v;
        int fx;
        if(x == 0){
            fx = x+absolute(v);
        } else if(x == 5){
            fx = x-absolute(v);
        }
        else{
            fx = x+v;
        }
        return fx;
    }

    private Index getAdjacent(Worker x){
        int fx = getAdjacent(x.getPosition().getX());
        int fy = getAdjacent(x.getPosition().getY());
        int fz = getAdjacent(x.getPosition().getZ());
        return new Index(fx,fy,fz);
    }

    public void placeAdjacent(){
        this.ex = getAdjacent(this.x);
        this.ey = getAdjacent(this.y);
        this.ez = getAdjacent(this.z);
        Index newIndex = new Index(ex,ey,ez);
        match.initWorker(enemyWorker,newIndex);

    }



    @org.junit.Before
    public void setUp(){
        Random rand = new Random();
        this.x = rand.nextInt(5);
        this.y = rand.nextInt(5);
        this.z = rand.nextInt(4);
        this.apollo = new Apollo();
        match = new Match(1);
        match.initWorker(myWorker, new Index(x,y,z));

        placeAdjacent();


    }

    @org.junit.Test
    public void testTurn_CorrectInput_UsePower(){
        apollo.turn(match,myWorker,myWorker.getPosition(),enemyWorker.getPosition());
        //Controls if coordinates of myworker are those that enemyWorker previously had
        int xpos = myWorker.getPosition().getX();
        int ypos = myWorker.getPosition().getY();
        int zpos = myWorker.getPosition().getZ();
        assertEquals(xpos, ex);
        assertEquals(ypos, ey);
        assertEquals(zpos, ey);

        //Controls if coordinates of enemyWorker are those that myWorker previously had
        xpos = enemyWorker.getPosition().getX();
        ypos = enemyWorker.getPosition().getY();
        zpos = enemyWorker.getPosition().getZ();

        assertEquals(xpos, x);
        assertEquals(ypos, y);
        assertEquals(zpos, z);
    }

    @org.junit.Test
    public void testTurn_CorrectInput_NormalMovement(){
        apollo.turn(match,myWorker,myWorker.getPosition(),getAdjacent(myWorker));
        int xpos = myWorker.getPosition().getX();
        int ypos = myWorker.getPosition().getY();
        int zpos = myWorker.getPosition().getZ();

        assertEquals(xpos, ex);
        assertEquals(ypos, ey);
        assertEquals(zpos, ey);

    }
}
