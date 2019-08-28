package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import javafx.collections.ObservableList;

public class TinyCPUSimulator {
    private InstructionMemory instMem;
    private DataMemory dataMem;
    
    private Register regA, regB, regPC, regRI;
    private Boolean ccZ, ccN;   
    
    private Boolean alreadyInHLT;
    
    public TinyCPUSimulator() {
        this.instMem = new InstructionMemory();
        this.dataMem = new DataMemory();
        
        this.regA = new Register(0, 8);
        this.regB = new Register(0, 8);
        this.regPC = new Register(0, 8);
        this.regRI = new Register(0, 8);
        
        this.ccZ = false;
        this.ccN = false;
        
        this.alreadyInHLT = false;
    }
    
    public void run() {  //until HLT
        //TODO implement it
    }
    
    public void runNextInstruction() throws UnrecognizedInstructionException {
        if(this.alreadyInHLT) {
            return;
        }
        
        Instruction current = this.instMem.read(this.regPC.getContent());
        this.regRI.setContent(current.getWord());
        this.regPC.add(1);
        
        Register targetReg = null;
        
        if(current.isRegInstruction()) {
            targetReg = current.getReg().equals("RA") ? this.regA : this.regB;
        }
        
        if(current.getOpcode().equals("LDR")) {  //LDR    
            int readAddress = current.getMemAddress();
            targetReg.setContent(this.dataMem.read(readAddress));
        }
        else if(current.getOpcode().equals("STR")) {  //STR
            int writeAddress = current.getMemAddress();
            this.dataMem.write(writeAddress, targetReg.getContent());
        }
        else if(current.getOpcode().equals("ADD")) {  //ADD
            int readOperandAddress = current.getMemAddress();
            Integer operand = this.dataMem.read(readOperandAddress);
            targetReg.add(operand);
        }
        else if(current.getOpcode().equals("SUB")) {  //STR
            int readOperandAddress = current.getMemAddress();
            Integer operand = this.dataMem.read(readOperandAddress);
            targetReg.sub(operand);
        }
        else if(current.getOpcode().equals("JMP")) {  //JMP
            int jumpAddress = current.getMemAddress();
            this.regPC.setContent(jumpAddress);
        }
        else if(current.getOpcode().equals("JC")) {  //JC
            int jumpAddress = current.getMemAddress();
            if(current.getCC().equals("Z") && this.ccZ || current.getCC().equals("N") && this.ccN) {
                this.regPC.setContent(jumpAddress);
            }            
        }
        else if(current.getOpcode().equals("HLT")) {  //HLT
            this.alreadyInHLT = true;
        }
        else {
            throw new UnrecognizedInstructionException();
        }
        
        if(current.isRegInstruction()) {
            this.updateConditionCodes(targetReg.getContent());
        }
        
        this.instMem.updatePC(this.regPC.getContent());
    }

    public Instruction getCurrentInstruction() {
        return this.instMem.read(this.regPC.getContent());
    }
    
    public void parseInstMemFile(File instMemFile) throws FileNotFoundException {
        this.instMem = new InstructionMemory(instMemFile);
    }
    
    void parseDataMemFile(File dataMemFile) throws FileNotFoundException {
        this.dataMem = new DataMemory(dataMemFile);
    }
    
    public ObservableList<Instruction> getInstMem() {
        return instMem.getMemList();
    }
    
    public ObservableList<MemData> getDataMem() {
        return dataMem.getMemList();
    }
    
    public void setInstMemPosition(int address, String wordStr) {
        this.instMem.setInst(address, wordStr);
    }    
    
    public void setDataMemPosition(int address, String wordStr) {
        this.dataMem.setMemData(address, wordStr);
    }

    public Register getRegA() {
        return regA;
    }

    public Register getRegB() {
        return regB;
    }

    public Register getPC() {
        return regPC;
    }

    public Register getRI() {
        return regRI;
    }

    private void updateConditionCodes(Integer data) {
        this.ccZ = (data == 0);
        this.ccN = (data < 0);
    }

    public Boolean getCcZ() {
        return ccZ;
    }

    public Boolean getCcN() {
        return ccN;
    }
    
}
