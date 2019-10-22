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
        for (int addCount = 0; addCount < 16; addCount++) {
            Instruction inst = new Instruction(0, addCount);
            this.memList.add(inst);
            inst.setAssigned(false);
        }
    }
    
    public InstructionMemory(File memFile) throws FileNotFoundException {
        this.memList = FXCollections.observableArrayList();
        Scanner fileScan = new Scanner(memFile);
        
        int addCount = 0;
        while(fileScan.hasNext() && addCount < 16) {
            Instruction inst = new Instruction(Integer.parseInt(fileScan.nextLine(), 16), addCount);
            this.memList.add(inst);
            inst.setAssigned(true);
            addCount += 1;
        }
        
        while(addCount < 16) {
            Instruction inst = new Instruction(0, addCount);
            this.memList.add(inst);
            inst.setAssigned(false);
            addCount += 1;
        }
        
        this.checkForHLT();
    }

    public ObservableList<Instruction> getMemList() {
        return this.memList;
    }   

    void setInst(int address, String wordStr) {
        Instruction inst = this.memList.get(address);
        inst.setHexWord(wordStr);
        this.checkForHLT();
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
    
    private void checkForHLT() {
        boolean hltFound = false;
        for(Instruction inst : this.memList) {
            if(inst.getOpcode().equals("HLT")) {
                hltFound = true;
                continue;
            }
            if(hltFound) {
                inst.setAssigned(false);
            }
        }
    }
    
}
