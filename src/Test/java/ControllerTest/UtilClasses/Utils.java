package ControllerTest.UtilClasses;

import Controller.Gods.*;
import Model.*;

import java.util.ArrayList;
import java.util.Random;

public class Utils {

    public Index generateRandomIndex(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        int y = rand.nextInt(5);
        int z = rand.nextInt(4);
        return new Index(x,y,z);
    }

    public Index getPseudoAdjacent(Worker worker){
        Index temp = worker.getPosition();
        int x = -1;
        Random rand = new Random();
        while(x<0 || x>4){
            if(rand.nextInt(2)==1) {
                x = temp.getX() + rand.nextInt(2);
            }
            else x = temp.getX() - rand.nextInt(2);
        }

        int y = -1;

        while(y<0 || y>4){
            if(rand.nextInt(2)==1)
                y = temp.getY() + rand.nextInt(2);
            else y = temp.getY() - rand.nextInt(2);
        }
        int z = -1;

        while(z<0 || z>3){
            if(rand.nextInt(2)==1)
                z = temp.getZ() + rand.nextInt(2);
            else z = temp.getZ() - rand.nextInt(2);
        }
        temp = new Index(x,y,z);
        if(temp.equals(worker.getPosition()))
            if(worker.getPosition().getY() == 4)
                return new Index(x,y-1,z);
            else return new Index(x, y+1 ,z);
        return  new Index(x,y,z);
    }


    public Index getPseudoAdjacent(Index index){
        Index temp = index;
        int x = -1;
        Random rand = new Random();
        while(x<0 || x>4){
            if(rand.nextInt(2)==1) {
                x = temp.getX() + rand.nextInt(2);
            }
            else x = temp.getX() - rand.nextInt(2);
        }

        int y = -1;

        while(y<0 || y>4){
            if(rand.nextInt(2)==1)
                y = temp.getY() + rand.nextInt(2);
            else y = temp.getY() - rand.nextInt(2);
        }
        int z = -1;

        while(z<0 || z>3){
            if(rand.nextInt(2)==1)
                z = temp.getZ() + rand.nextInt(2);
            else z = temp.getZ() - rand.nextInt(2);
        }

        temp = new Index(x,y,z);
        if(temp.equals(index))
            if(index.getY() == 4)
                return new Index(x,y-1,z);
            else return new Index(x, y+1 ,z);
        return  new Index(x,y,z);
    }

    public Index getPseudoAdjacentTwoLevelsBelow(Index index){
        Index ix = getPseudoAdjacent(index);
        if(index.getZ() == 2 || index.getZ() == 3){
            System.out.println("HEYA!");
            ix = new Index(ix.getX(), ix.getY(), ix.getZ()-2);
        }
        return ix;
    }


    public Index getPseudoAdjacentTwoLevelsBelow(Worker index){
        return getPseudoAdjacentTwoLevelsBelow(index.getPosition());
    }

    public ArrayList<Index> getAllLevelsAbove(Worker worker){
        Index ix = worker.getPosition();
        System.out.println("Your Position: "+ix);
        int xPos = ix.getX();
        int yPos = ix.getY();
        int zPos = ix.getZ() + 1;
        ArrayList<Index> inx_array = new ArrayList<>();
        if (zPos == 4 || zPos == 5) return inx_array;


        int tempx = 0;
        int tempy = 0;
        for(int i = -1; i<2; i++){
            for(int j = -1; j<2; j++){
                tempx = xPos + i;
                tempy = yPos + j;
                if(tempx>=0 && tempx<5 && tempy>=0 && tempy<5){
                    inx_array.add(new Index(tempx,tempy,zPos));
                }

            }
        }

        return inx_array;
    }

    public God generateRandomGod() {
        Random random = new Random();
        int choice = random.nextInt(9);
        switch (choice){
            case 0:
                return new Apollo();

            case 1:
                return new Artemis();

            case 2:
                return new Athena();

            case 3:
                return new Atlas();

            case 4:
                return new Demeter();

            case 5:
                return new Hephaestus();

            case 6:
                return new Minotaur();

            case 7:
                return new Pan();

            case 8:
                return new Prometheus();

            default:
                return new Apollo();

        }

    }
}
