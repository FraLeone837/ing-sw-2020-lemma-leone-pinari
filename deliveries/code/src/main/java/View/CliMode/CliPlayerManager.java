package View.CliMode;

import Controller.Communication.Message;
import View.Interfaces.PlayerManager;
import View.UserInterface;

import java.util.ArrayList;
import java.util.Scanner;

import static View.CliMode.CliGameManager.*;
import static java.lang.Character.toUpperCase;
import static java.lang.System.exit;

public class CliPlayerManager implements PlayerManager, Runnable{

    private Scanner scanner;
    private UserInterface ui;

    private int idFirstWorker;
    private String godName = LABEL_NO_INPUT;
    private String godDescription = LABEL_NO_INPUT;
    private String id = LABEL_NO_INPUT;
    private String name = LABEL_NO_INPUT;
    private String validInput = LABEL_WAIT_YOUR_TURN;
    private String turn = LABEL_NO_INPUT;

    private ArrayList circumstantialInput;
    private ArrayList alwaysAvailableInput;
    private boolean isEveryInputValid;

    public CliPlayerManager(UserInterface ui){
        scanner = new Scanner(System.in);
        this.ui = ui;

        circumstantialInput = new ArrayList();
        alwaysAvailableInput = new ArrayList();
        alwaysAvailableInput.add("/HELP");
        alwaysAvailableInput.add("/GOD");
        alwaysAvailableInput.add("/ID");
        alwaysAvailableInput.add("/NAME");
        alwaysAvailableInput.add("/TURN");
        alwaysAvailableInput.add("/MAP");
        alwaysAvailableInput.add("/INPUT");
        alwaysAvailableInput.add("/EXIT");
    }


    @Override
    public void run() {
        while(true){
            String input = scanner.nextLine();
            if(input.length() == 0){
                System.out.println("Please write something");
                continue;
            }
            input = input.toUpperCase();
            if(alwaysAvailableInput.contains(input)){
                System.out.println(checkInput(input));
            }
            else if(circumstantialInput.contains(input) || isEveryInputValid){
                circumstantialInput.clear();
                ui.receivedUiInput(input);
                isEveryInputValid = false;
            }
            else{
                System.out.println("Invalid Input. Please read below:");
                System.out.println(LABEL_HELP);
            }
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    private String checkInput(String input) {
        switch (input){
            case "/HELP":
                return LABEL_HELP;
            case "/GOD":
                return godName +", " + godDescription;
            case "/ID":
                return this.id;
            case "/TURN":
                return this.turn;
            case "/INPUT":
                return showAvailableInput();
            case "/MAP":
                return mapLegend();
            case "/NAME":
                return this.name;
            case "/EXIT":
                exit (-1);
            default:
                return "Error!! Contact admin to fix.";
        }
    }

    private String showAvailableInput() {
        if(isEveryInputValid){
            return "Every input is available";
        }
        String array = "The available input is:"+ System.lineSeparator() + ANSI_CYAN;
        for(Object s : circumstantialInput){
            array = array + (String)s + " ";
        }
        array = array + ANSI_RESET;
        return array;
    }

    /**
     * gives the legend of the maps with colors
     * and the meaning of the characters found
     * @return
     */
    private String mapLegend() {
        String toReturn = "The buildings are denoted each one with a certain color: " + ANSI_BLACK +
                colorGroundLevel + "Ground level " + ANSI_RESET  + ANSI_BLACK + colorFirstLevel + "First level " + ANSI_RESET + ANSI_BLACK +
                colorSecondLevel + "Second level " + ANSI_RESET + ANSI_BLACK  + colorThirdLevel + "Third level" + ANSI_RESET
                + "." + System.lineSeparator()+ "If there is a dome it is denoted by the character 'c'."+  System.lineSeparator()+ "Meanwhile if there is a player it is denoted" +
                " by one of the numbers 1-6.";
        return toReturn;
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
                ui.receivedUiInput(Integer.toString(chosenWorker));
                break;
            case 2:
                System.out.println(LABEL_ONLY_SECOND_WORKER_MOVE);
                ui.receivedUiInput(Integer.toString(chosenWorker));
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
            System.out.println("Move again?");
        } else if(moveAgain == Message.MessageType.BUILD_AGAIN){
            System.out.println("Build again?");
        }
        circumstantialInput.add("YES");
        circumstantialInput.add("Y");
        circumstantialInput.add("N");
        circumstantialInput.add("NO");

    }

    @Override
    public void buildBefore(){
        System.out.println(LABEL_BUILD_BEFORE);
        circumstantialInput.add("YES");
        circumstantialInput.add("Y");
        circumstantialInput.add("N");
        circumstantialInput.add("NO");
    }

    @Override
    public void showTurn(String object) {
        if(object.toUpperCase().equals(this.name))
            this.turn = LABEL_YOUR_TURN;
        else
            this.turn = LABEL_TURN + object + LABEL_TURN_2;
        System.out.println(this.turn);
    }

    @Override
    public void getName(String object) {
        System.out.println(object);
        isEveryInputValid = true;
    }

    /**
     * asks if build with other worker
     */
    @Override
    public void buildOtherWorker() {
        System.out.println(LABEL_POSEIDON_POWER);

        circumstantialInput.add("YES");
        circumstantialInput.add("Y");
        circumstantialInput.add("N");
        circumstantialInput.add("NO");

    }

    @Override
    public void printLoser(String object) {
        System.out.println(object.toUpperCase() + LABEL_OTHER_PLAYER_LOST);
    }

    @Override
    public void showGods(String[] god, String name) {
        if(this.name.equals(name.toUpperCase())){
            this.godName = "Your god is " + ANSI_BLUE + god[0] + ANSI_RESET;
            this.godDescription = god[1];
            System.out.println(godName + "," +godDescription);
            return;
        }
        System.out.println(name + "'s God is:" + god[0] + ", " + god[1]);
    }

    /**
     * Set for first worker's id
     * @param id worker's id
     */
    public void setIdFirstWorker(int id){
        idFirstWorker = id;
        this.id = LABEL_ID_BEGIN + id + LABEL_ID_FINAL + id + " " + (id+1);
    }




    /**
     * Given the numeration of the cell, this method returns back the input
     * the user should write to refer to that cell
     * @param numeration the cell numeration (0-24)
     * @return the corresponding coordinates (a-e 1-5)
     */
    private String correspondingCoords(int numeration){
        int x = numeration%5;
        int y = numeration/5;
        String coords = "";
        coords += (char)(x+65);
        coords += y + 1;
        return coords;
    }

    /**
     * same but opposite of method correspondingCoords
     * @param buildings
     */
    private void convertToCellNumeration(int[] buildings) {
        char xPos;
        char yPos;
        int comma=0;
        for(int building : buildings){
            xPos =(char)(65 + building%5);
            yPos =(char)(49 + building/5);
            if(comma>0) {
                System.out.print(", ");
            }
            comma++;
            System.out.print(toUpperCase(xPos));
            System.out.print(yPos);
        }
        System.out.println();
    }
}
