package View;

import java.util.Scanner;

public class CliPlayerManager implements PlayerManager{
    private Scanner scanner;
    private UserInterface ui;
    private int idFirstWorker;

    public CliPlayerManager(UserInterface ui){
        scanner = new Scanner(System.in);
        this.ui = ui;
    }

    public void getServerIp(){
        System.out.println(LABEL_SERVER_IP);
        String ip = scanner.nextLine();
        ui.receivedUiInput(ip);
    }

    @Override
    public void getName() {
        System.out.println(LABEL_USERNAME);
        String name = scanner.nextLine();
        ui.receivedUiInput(name);
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
    public void chooseNumberPlayers() {
        System.out.println(LABEL_NUMBER_PLAYERS);
        int number;
        do{
            number = scanner.nextInt();
        } while(number!=2 && number!=3);
        ui.receivedUiInput(number);
    }

    @Override
    public void placeWorker(boolean firstWorker, int[] possiblePositions) {
        if(firstWorker)
            System.out.println(LABEL_FIRST_WORKER);
        else
            System.out.println(LABEL_SECOND_WORKER);
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
        ui.receivedUiInput(chosenPosition);
    }

    @Override
    public void chooseWorker(int workers) {
        int chosenWorker = workers + idFirstWorker - 1;
        switch(workers){
            case 1:
                System.out.println(LABEL_ONLY_FIRST_WORKER_MOVE);
                break;
            case 2:
                System.out.println(LABEL_ONLY_SECOND_WORKER_MOVE);
                break;
            case 3:
                System.out.println(LABEL_CHOOSE_WORKER_TO_MOVE);
                do{
                    chosenWorker = scanner.nextInt();
                } while(chosenWorker!=idFirstWorker && chosenWorker!=(idFirstWorker+1));
                break;
        }
        ui.receivedUiInput(chosenWorker);
    }

    @Override
    public void chooseMovement(int[] movements) {
        System.out.println(LABEL_CHOOSE_WHERE_TO_MOVE);
        System.out.println("Possible movements are: " + movements);
        ui.receivedUiInput(validCoordinatesInput(movements));
    }

    @Override
    public void chooseBuilding(int[] buildings) {
        System.out.println(LABEL_CHOOSE_WHERE_TO_BUILD);
        System.out.println("Possible buildings are: " + buildings);
        ui.receivedUiInput(validCoordinatesInput(buildings));
    }

    @Override
    public void buildDome() {
        System.out.println(LABEL_BUILD_DOME);
        String answer;
        do{
            answer = scanner.nextLine();
        }while(answer!="dome" && answer!="building");
        ui.receivedUiInput(answer.equals("dome"));
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
