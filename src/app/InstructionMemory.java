package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InstructionMemory {
    private ObservableList<Instruction> memList;
    private final int INST_MEM_SIZE = 16;
    
    public InstructionMemory() {
        this.memList = FXCollections.observableArrayList();
        for (int addCount = 0; addCount < INST_MEM_SIZE; addCount++) {
            Instruction inst = new Instruction(addCount);
            this.memList.add(inst);
        }
    }
    
    @SuppressWarnings("empty-statement")
    public InstructionMemory(File memFile) throws FileNotFoundException {
        this.memList = FXCollections.observableArrayList();
        Scanner fileScan = new Scanner(memFile);
        
        while(!fileScan.nextLine().equals("INST"));                    
        
        int addCount = 0;
        
        String line = "";
        line = fileScan.nextLine();
        
        while(!line.equals("DATA") && addCount < INST_MEM_SIZE) {
            Instruction inst = new Instruction(addCount, Integer.parseInt(line, 16));
            this.memList.add(inst);
            addCount += 1;
            
            line = fileScan.nextLine();
        }
        
        while(addCount < INST_MEM_SIZE) {
            Instruction inst = new Instruction(addCount);
            this.memList.add(inst);
            addCount += 1;
        }
        
        this.checkForHLT();
    }

    InstructionMemory(String inlineInstructions) {
        this.memList = FXCollections.observableArrayList();
        String[] listCodes = inlineInstructions.split("\n");
        
        int addCount = 0;
        for(String code : listCodes) {
            if(!code.equals("")) {
                Instruction inst = new Instruction(Integer.parseInt(code, 16), addCount);
                this.memList.add(inst);
                inst.setAssigned(true);
                addCount += 1;
            }
        }
        
        while(addCount < INST_MEM_SIZE) {
            Instruction inst = new Instruction(0, addCount);
            this.memList.add(inst);
            inst.setAssigned(false);
            addCount += 1;
        }
    }
    
    public String getSavedMem() {
        String returnable = "INST\n";
        for (Instruction instruction : memList) {
            if(instruction.getHexWord() != null) {
                returnable += instruction.getHexWord() + "\n";
            }
        }
        return returnable;
    }

    public ObservableList<Instruction> getMemList() {
        return this.memList;
    }   

    void setInst(int address, String hexWord) {
        this.memList.set(address, new Instruction(address, hexWord));
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
            if(inst.isHLT()) {
                hltFound = true;
                continue;
            }
            if(hltFound) {
                inst.setAssigned(false);
            }
        }
    }
    
}
