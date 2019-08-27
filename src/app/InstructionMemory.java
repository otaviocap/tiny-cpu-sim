package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class InstructionMemory {
    private ArrayList<Instruction> instMemList;
    
    public InstructionMemory() {
        instMemList = new ArrayList<Instruction>();
    }
    
    public InstructionMemory(File memFile) throws FileNotFoundException {
        instMemList = new ArrayList<Instruction>();
        Scanner fileScan = new Scanner(memFile);
        
        int addCount = 0;
        while(fileScan.hasNext()) {
            instMemList.add(new Instruction(Byte.valueOf(fileScan.next(), 10), addCount));
            addCount += 1;
        }
    }

    ObservableList<Instruction> getInstMemList() {
        return FXCollections.observableArrayList(this.instMemList);
    }   
    
}
