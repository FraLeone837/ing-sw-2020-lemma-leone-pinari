package ControllerTest.UtilClasses;

import Model.Cell;

import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * class that writes the state of the match on a file in path path
 */
public class WriterClass {
    public String LABEL_IGNORE_PATH = "/ignore/logText.txt";
    public String LABEL_IGNORE_PATHINT = "/ignore/logTextInt.txt";
    //default folder
    public String LABEL_IGNORE_FOLDER = "/ignore";
    private String path;
    private boolean append;
    private boolean wroteTime;

    public WriterClass(String path, boolean append){
        this.path = LABEL_IGNORE_FOLDER + path;
        this.append = append;
        this.wroteTime = false;
    }



    public WriterClass(boolean append) throws IOException{
        this.path = LABEL_IGNORE_FOLDER;
        this.append = append;
        this.wroteTime = false;
        openFile();
    }

    public void writeOnFile(String toPrint, Cell c) throws IOException{

        FileWriter writer = new FileWriter(path, append);
        PrintWriter printWriter = new PrintWriter(writer);

        if(!wroteTime){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            this.wroteTime = true;
            printWriter.printf("This file shows the real state of the match in question. Match started in: ");
            printWriter.printf(dtf.format(now) + "%n");
            printWriter.flush();

        }
        printWriter.printf(toPrint + "%n");
        printWriter.flush();
        printWriter.close();
    }

    public void writeOnFile(String toPrint) throws IOException{

        FileWriter writer = new FileWriter(LABEL_IGNORE_PATHINT, append);
        PrintWriter printWriter = new PrintWriter(writer);

        if(!wroteTime){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            this.wroteTime = true;
            printWriter.printf("This file shows the real state of the match in question. Match started in: ");
            printWriter.printf(dtf.format(now) + "%n");
            printWriter.flush();

        }
        printWriter.printf(toPrint + "%n");
        printWriter.flush();
        printWriter.close();
    }

    public void writeOnFile(String toPrint, String pathName) throws IOException{

        File tmpDir = new File(pathName);
        boolean exists = tmpDir.exists();
        if(exists)
            return;

        FileWriter writer = new FileWriter(pathName, append);
        PrintWriter printWriter = new PrintWriter(writer);

        printWriter.printf(toPrint + "%n");
        printWriter.flush();
        printWriter.close();
    }

    public String readFromFile(String pathName){
        File file = new File(pathName);
        Scanner scanner;
        try{
            scanner = new Scanner(file);
        } catch (FileNotFoundException e){
            e.printStackTrace();
            return "Error";
        }
        //suppose that scanner always has next line
        return scanner.nextLine();
    }

    public String returnString(String phrase){
        return phrase;
    }

    public void writeOnFile(int[] toPrint) throws IOException{

        String temp = path;
        this.path = LABEL_IGNORE_PATHINT;
        openFile();
        FileWriter writer = new FileWriter(path, append);
        PrintWriter printWriter = new PrintWriter(writer);
        for(int i = 0; i<25 ; i++){
                if(i%5 == 0)
            printWriter.printf("%n");
            printWriter.printf(Integer.toString(toPrint[i]) + " ");

        }
        this.path = temp;
        printWriter.flush();
        printWriter.close();
    }

    private void openFile() throws IOException{
        File folder = new File("/ignore");
        if (!folder.exists()) {
            if (folder.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }

        File file = new File(this.path);
        if(file.createNewFile()){
            System.out.println("File created" + file.getName());
        } else {
            System.out.println("File already exists");
        }

        //first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if(file.exists()) desktop.open(file);
    }

}
