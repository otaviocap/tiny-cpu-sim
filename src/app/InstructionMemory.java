package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InstructionMemory {
    private ObservableList<Instruction> memList;
    
    public InstructionMemory() {
        this.memList = FXCollections.observableArrayList();
        for (int add = 0; add < 16; add++) {
            this.memList.add(new Instruction(0, add));
        }
    }
    
    public InstructionMemory(File memFile) throws FileNotFoundException {
        this.memList = FXCollections.observableArrayList();
        Scanner fileScan = new Scanner(memFile);
        
        int addCount = 0;
        while(fileScan.hasNext()) {
            this.memList.add(new Instruction(Integer.parseInt(fileScan.nextLine(), 16), addCount));
            addCount += 1;
        }
    }

    public ObservableList<Instruction> getMemList() {
        return this.memList;
    }   

    void setInst(int address, String wordStr) {
        Instruction inst = this.memList.get(address);
        inst.setHexWord(wordStr);
    }

    public Instruction read(int address) {
        return this.memList.get(address);
    }

    public void updatePC(Integer pc) {
        for(Instruction inst : this.memList) {
            if(inst.getAddress() == pc) {
                inst.setPcIsHere(true);
            }
            else {
                inst.setPcIsHere(false);
            }
        }
    }
    
}
