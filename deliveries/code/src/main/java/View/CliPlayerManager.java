package View;

import Controller.Communication.Message;

import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Character.toUpperCase;

public class CliPlayerManager implements PlayerManager, Runnable{

    private Scanner scanner;
    private UserInterface ui;

    private int idFirstWorker;
    private String godName;
    private String godDescription;

    private ArrayList circumstantialInput;
    private ArrayList alwaysAvailableInput;
    private boolean isEveryInputValid;

    public CliPlayerManager(UserInterface ui){
        scanner = new Scanner(System.in);
        this.ui = ui;

        circumstantialInput = new ArrayList();
        alwaysAvailableInput = new ArrayList();
        alwaysAvailableInput.add("/help");
    }


    @Override
    public void run() {
        while(true){
            String input = scanner.nextLine();
            input = input.toUpperCase();
            if(alwaysAvailableInput.contains(input)){
                //le risposte a help
                System.out.println(LABEL_HELP);
            }
            else if(circumstantialInput.contains(input) || isEveryInputValid){
                circumstantialInput.clear();
                ui.receivedUiInput(input);
                isEveryInputValid = false;
            }
            else
                System.out.println("INVALID INPUT");
        }
    }

    public void getServerIp(){
        System.out.println(LABEL_SERVER_IP);
        isEveryInputValid = true;
    }

    @Override
    public void getName() {
        System.out.println(LABEL_USERNAME);
        isEveryInputValid = true;
    }

    /* POSSIBLY USELESS NOW
    * */

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
        circumstantialInput.add("2");
        circumstantialInput.add("3");
    }

    @Override
    public void chooseWorker(int workers) {
        int chosenWorker = workers + idFirstWorker - 1;
        switch(workers){
            case 1:
                System.out.println(LABEL_ONLY_FIRST_WORKER_MOVE);
                ui.receivedUiInput(chosenWorker);
                break;
            case 2:
                System.out.println(LABEL_ONLY_SECOND_WORKER_MOVE);
                ui.receivedUiInput(chosenWorker);
                break;
            case 3:
                System.out.println(LABEL_CHOOSE_WORKER_TO_MOVE);
                circumstantialInput.add(Integer.toString(idFirstWorker));
                int temp = idFirstWorker+1;
                circumstantialInput.add(Integer.toString(temp));
                break;
        }
    }

    @Override
    public void placeWorker(boolean firstWorker, int[] possiblePositions) {
        if(firstWorker)
            System.out.println(LABEL_FIRST_WORKER);
        else
            System.out.println(LABEL_SECOND_WORKER);
        for(int position : possiblePositions){
            circumstantialInput.add((correspondingCoords(position).toUpperCase()));
        }
        convertToCellNumeration(possiblePositions);
    }

    @Override
    public void chooseMovement(int[] movements) {
        System.out.println(LABEL_CHOOSE_WHERE_TO_MOVE);
        System.out.println("Possible movements are: ");
        for (int movement : movements){
            circumstantialInput.add((correspondingCoords(movement)));
        }
        convertToCellNumeration(movements);
    }

    @Override
    public void chooseBuilding(int[] buildings) {
        System.out.println(LABEL_CHOOSE_WHERE_TO_BUILD);
        System.out.println("Possible places where to build are: ");
        for(int building : buildings){
            circumstantialInput.add((correspondingCoords(building)));
        }
        convertToCellNumeration(buildings);
    }

    @Override
    public void buildDome() {
        System.out.println(LABEL_BUILD_DOME);
        System.out.println("Please write dome or building, to select your choice.");
        circumstantialInput.add("DOME");
        circumstantialInput.add("BUILDING");
    }

    @Override
    public void doItAgain(Message.MessageType moveAgain) {
        System.out.print(LABEL_MOVE_AGAIN);
        if(moveAgain == Message.MessageType.MOVE_AGAIN){
            System.out.print("Move again?");
        } else if(moveAgain == Message.MessageType.BUILD_AGAIN){
            System.out.println("Build again?");
        }
        circumstantialInput.add("YES");
        circumstantialInput.add("NO");

    }

    @Override
    public void buildBefore(){
        System.out.println(LABEL_BUILD_BEFORE);
        circumstantialInput.add("YES");
        circumstantialInput.add("NO");
    }

    @Override
    public void showTurn(String object) {
        System.out.println(LABEL_TURN + object + LABEL_TURN_2);

    }

    @Override
    public void showGods(String[] god) {
        //immagino che in questo metodo andrebbe fatto qualcosa tipo
        //alwaysAvailableInput.add("/godName");
        //alwaysAvailableInput.add("/godDescription");
        this.godName = god[0];
        this.godDescription = god[1];
        System.out.println(LABEL_YOUR_GOD + godName + LABEL_YOUR_GOD_DESC + godDescription);
        ui.receivedUiInput("Ok!");
    }

    /**
     * Set for first worker's id
     * @param id worker's id
     */
    public void setIdFirstWorker(int id){
        idFirstWorker = id;
    }

    /* POSSIBLY USELESS NOW
    * */

    /**
     * Check if the given input from the user actually correspond to a cell
     * in which it can do that action
     * @param possibleCell the list of the cells in which he can move/build
     * @return the numeration index of the cell
     */
   /* private int validCoordinatesInput(int[] possibleCell){
        int chosenCell;
        boolean invalidInput = true;
        do{
            System.out.println("Choose one of the coordinates given above.");
            chosenCell = correspondingCellNumeration(scanner.nextLine());
            for(int i=0; i<possibleCell.length; i++){
                if(chosenCell == possibleCell[i])
                    invalidInput = false;
            }
        } while(invalidInput);
        return chosenCell;
    }*/

    /* POSSIBLY USELESS NOW
    * */


    /**
     * Given the numeration of the cell, this method returns back the input
     * the user should write to refer to that cell
     * @param numeration the cell numeration (0-24)
     * @return the corresponding coordinates (a-e 0-4)
     */
    private String correspondingCoords(int numeration){
        int x = numeration%5;
        int y = numeration/5;
        String coords = "";
        coords += (char)(x+65);
        coords += y;
        return coords;
    }

    private void convertToCellNumeration(int[] buildings) {
        char xPos;
        char yPos;

        for(int building : buildings){
            xPos =(char)(65 + building%5);
            yPos =(char)(48 + building/5);
            System.out.print(toUpperCase(xPos));
            System.out.print(yPos + ", ");
        }
        System.out.println();
    }

}
