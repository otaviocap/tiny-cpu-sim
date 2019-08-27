package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InstructionMemory {
    private ArrayList<Instruction> memList;
    
    public InstructionMemory() {
        memList = new ArrayList<Instruction>();
    }
    
    public InstructionMemory(File memFile) throws FileNotFoundException {
        memList = new ArrayList<Instruction>();
        Scanner fileScan = new Scanner(memFile);
        
        int addCount = 0;
        while(fileScan.hasNext()) {
            memList.add(new Instruction(Integer.parseInt(fileScan.nextLine(), 16), addCount));
            addCount += 1;
        }
    }

    public ObservableList<Instruction> getMemList() {
        return FXCollections.observableArrayList(this.memList);
    }   
    
}