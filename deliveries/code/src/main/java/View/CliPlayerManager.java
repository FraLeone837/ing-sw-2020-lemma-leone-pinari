package View;

import Model.Index;
import Model.Worker;

import java.util.List;
import java.util.Scanner;

public class CliPlayerManager implements PlayerManager{
    private Scanner scanner;

    public CliPlayerManager(){
        scanner = new Scanner(System.in);
    }

    @Override
    public int listMatch(List<Integer> ids) {
        System.out.println("Puoi connetterti a queste partite");
        for(int i=0; i<ids.size(); i++){
            System.out.println(ids.get(i));
        }
        int matchChoosen;
        do{
            matchChoosen = scanner.nextInt();
        } while(ids.contains(matchChoosen));
        return matchChoosen;
    }

    @Override
    public Index placeWorker(boolean firstWorker) {
        if(firstWorker)
            System.out.println("Scegli dove posizionare il primo lavoratore");
        else
            System.out.println("Scegli dove posizionare il secondo lavoratore");
        Index index;
        do{
            index = correspondingIndex(scanner.next());
        } while(index==null);
        return index;
    }

    @Override
    public Index chooseWorker(List<Worker> workers) {
        System.out.println("Quale lavoratore vuoi selezionare?");
        Index index;
        do{
            index = correspondingIndex(scanner.next());
        } while(index==null);
        return index;
    }

    @Override
    public Index chooseMovement(List<Index> movements) {
        System.out.println("Dove vuoi muovere il lavoratore?");
        Index index;
        do{
            index = correspondingIndex(scanner.next());
        } while(index==null);
        return index;
    }

    @Override
    public Index chooseBuilding(List<Index> buildings) {
        System.out.println("Dove vuoi costruire?");
        Index index;
        do{
            index = correspondingIndex(scanner.next());
        } while(index==null);
        return index;
    }

    /**
     * Given the coordinates from console, this method validates them and give back a corresponding index
     * @param coor a two char String corrersponding to a Cell (A-E)(1-5)
     * @return an Index object corresponding to those coordinates
     */
    public Index correspondingIndex(String coor){
        if(coor.length()!=2)
            return null;
        if(coor.charAt(0)<'a'&& coor.charAt(0)>'e')
            return null;
        if(coor.charAt(1)<'1'&& coor.charAt(1)>'5')
            return null;
        int x = coor.charAt(0)-97;
        int y = coor.charAt(1)-48;
        int z = 0;
        Index index = new Index(x, y, z);
        return index;
    }
}
