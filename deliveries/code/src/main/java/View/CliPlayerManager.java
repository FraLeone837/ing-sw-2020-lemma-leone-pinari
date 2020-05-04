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

    public String getServerIp(){
        System.out.println("Which server do you want to connect to?");
        return scanner.nextLine();
    }

    @Override
    public String getName() {
        System.out.println("Write your username");
        return scanner.nextLine();
    }

    @Override
    public int listMatch(List<Integer> ids) {
        System.out.println("You can join these matches");
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
            System.out.println("Choose where to locate the first worker");
        else
            System.out.println("Choose where to locate the second worker");
        Index index;
        do{
            index = correspondingIndex(scanner.next());
        } while(index==null);
        return index;
    }

    @Override
    public Index chooseWorker(List<Worker> workers) {
        System.out.println("Which worker do you want to select?");
        Index index;
        do{
            index = correspondingIndex(scanner.next());
        } while(index==null);
        return index;
    }

    @Override
    public Index chooseMovement(List<Index> movements) {
        System.out.println("Where do you want to move the worker?");
        Index index;
        do{
            index = correspondingIndex(scanner.next());
        } while(index==null);
        return index;
    }

    @Override
    public Index chooseBuilding(List<Index> buildings) {
        System.out.println("Where do you want to build?");
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
