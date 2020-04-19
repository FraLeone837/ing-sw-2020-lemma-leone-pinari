package ControllerTest;

import Model.Index;
import Model.Match;
import Model.Worker;

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

    public Index[] getAllLevelAboves(Worker worker){
        Index ix = worker.getPosition();
        int xPos = ix.getX();
        int yPos = ix.getY();
        int zPos = ix.getZ() + 1;
        Index[] inx_array = new Index[8];
        if (zPos == 4) return inx_array;

        int arrayIndex = 0;

        int tempx = 0;
        int tempy = 0;
        for(int i = -1; i<2; i++){
            for(int j = -1; j<2; j++){
                tempx = xPos + i;
                tempy = yPos + j;
                if(tempx>=0 && tempx<5 && tempy>=0 && tempy<5){
                    inx_array[arrayIndex] = new Index(tempx,tempy,zPos);
                }

            }
        }
        return inx_array;
    }

}
