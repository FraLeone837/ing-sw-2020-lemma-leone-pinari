package View;

import java.util.Scanner;

public class CliPlayerManager implements PlayerManager{
    private Scanner scanner;
    private int idFirstWorker;

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
/*
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
    }*/

    @Override
    public int chooseNumberPlayers() {
        System.out.println("How many players do you want in this match? (2 or 3)");
        int number;
        do{
            number = scanner.nextInt();
        } while(number!=2 && number!=3);
        return number;
    }

    @Override
    public int placeWorker(boolean firstWorker, int[] possiblePositions) {
        if(firstWorker)
            System.out.println("Choose where to locate the first worker");
        else
            System.out.println("Choose where to locate the second worker");
        System.out.println("Possible positions are: " + possiblePositions);
        int chosenPosition;
        boolean invalidInput = true;
        do{
            chosenPosition = scanner.nextInt();
            for(int i=0; i<possiblePositions.length; i++){
                if(chosenPosition == possiblePositions[i])
                    invalidInput = false;
            }
        } while(invalidInput);
        return chosenPosition;
    }

    @Override
    public int chooseWorker(int workers) {
        int chosenWorker = workers + idFirstWorker - 1;
        switch(workers){
            case 1:
                System.out.println("You can only move the first worker in this turn");
                break;
            case 2:
                System.out.println("You can only move the second worker in this turn");
                break;
            case 3:
                System.out.println("Which worker do you want to select?");
                do{
                    chosenWorker = scanner.nextInt();
                } while(chosenWorker!=idFirstWorker && chosenWorker!=(idFirstWorker+1));
                break;
        }
        return chosenWorker;
    }

    @Override
    public int chooseMovement(int[] movements) {
        System.out.println("Where do you want to move the worker?");
        System.out.println("Possible movements are: " + movements);
        return validCoordinatesInput(movements);
    }

    @Override
    public int chooseBuilding(int[] buildings) {
        System.out.println("Where do you want to build?");
        System.out.println("Possible buildings are: " + buildings);
        return validCoordinatesInput(buildings);
    }

    /**
     * Set for first worker's id
     * @param id worker's id
     */
    public void setIdFirstWorker(int id){
        idFirstWorker = id;
    }

    /**
     * Check if the given input from the user actually correspond to a cell
     * in which it can do that action
     * @param possibleCell the list of the cells in which he can move/build
     * @return the numeration index of the cell
     */

    private int validCoordinatesInput(int[] possibleCell){
        int chosenCell;
        boolean invalidInput = true;
        do{
            chosenCell = correspondingCellNumeration(scanner.nextLine());
            for(int i=0; i<possibleCell.length; i++){
                if(chosenCell == possibleCell[i])
                    invalidInput = false;
            }
        } while(invalidInput);
        return chosenCell;
    }
    /**
     * Given the coordinates from console, this method validates them
     * and give back a corresponding int according to the numeration
     * @param coor a two char String corrersponding to a Cell (A-E)(1-5)
     * @return an int corresponding to the numeration of the cell
     */
    private int correspondingCellNumeration(String coor){
        if(coor.length()!=2)
            return -1;
        if(coor.charAt(0)<'a'&& coor.charAt(0)>'e')
            return -1;
        if(coor.charAt(1)<'1'&& coor.charAt(1)>'5')
            return -1;
        int x = coor.charAt(0)-97;
        int y = coor.charAt(1)-48;
        return y * 5 + x;
    }
}
