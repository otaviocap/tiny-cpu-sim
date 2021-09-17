package app;

import java.io.File;
import java.io.FileNotFoundException;
import javafx.collections.ObservableList;

public class TinyCPUSimulator {
    private final int TIMEOUT = 10000;
    
    private InstructionMemory instMem;
    private DataMemory dataMem;
    
    private Register regA, regB, regC, regX, regPC, regRI;
    private Boolean ccZ, ccN;   
    
    private Boolean hltMode;
    
    public TinyCPUSimulator() {
        this.instMem = new InstructionMemory();
        this.dataMem = new DataMemory();
        
        this.regA = new Register(0, 8);
        this.regB = new Register(0, 8);
        this.regC = new Register(0, 8);
        this.regX = new Register(0, 8);
        this.regPC = new Register(0, 8);
        this.regRI = new Register(0, 16);
        
        this.ccZ = false;
        this.ccN = false;
        
        this.hltMode = false;
    }
    
    public void run() throws UnrecognizedInstructionException, TimeoutException {  //until HLT
        int contTimeout = 0;
        while(!this.hltMode) {
            this.runNextInstruction();
            contTimeout ++;
            if(contTimeout > TIMEOUT) {
                throw new TimeoutException();
            }
        }
    }
    
    public void runNextInstruction() throws UnrecognizedInstructionException {
        if(this.hltMode || this.regPC.getContent() >= 15) {
            this.hltMode = true;
            return;
        }
        
        Instruction currInst = this.instMem.read(this.regPC.getContent());
        this.regRI.setContent(currInst.getWord());
        this.regPC.add(1);
        
        Register targetReg = null;
        
        if(currInst.isRegInstruction()) {
            targetReg = this.getTargetRegister(currInst);
        }
        
        int memAddress = this.getMemAddress(currInst);
        
        if(currInst.getOpcode().equals("LDR")) {  //LDR    
            if(currInst.getMode().equals("IMM")) {  //immediate mode
                targetReg.setContent(currInst.getMemAddress());
            }
            else {
                targetReg.setContent(this.dataMem.read(memAddress));
            }
        }
        else if(currInst.getOpcode().equals("STR")) {  //STR
            this.dataMem.write(memAddress, targetReg.getContent());
        }
        else if(currInst.getOpcode().equals("ADD")) {  //ADD
            Integer operand;
            if(currInst.getMode().equals("IMM")) {  //immediate mode
                operand = currInst.getMemAddress();
            }
            else {
                operand = this.dataMem.read(memAddress);
            }
            targetReg.add(operand);
        }
        else if(currInst.getOpcode().equals("SUB")) {  //STR
            Integer operand;
            if(currInst.getMode().equals("IMM")) {  //immediate mode
                operand = currInst.getMemAddress();
            }
            else {
                operand = this.dataMem.read(memAddress);
            }
            targetReg.sub(operand);
        }
        else if(currInst.getOpcode().equals("JMP")) {  //JMP
            this.regPC.setContent(memAddress);
        }
        else if(currInst.getOpcode().equals("JC")) {  //JC
            if(currInst.getCC().equals("Z") && this.ccZ || currInst.getCC().equals("N") && this.ccN) {
                this.regPC.setContent(memAddress);
            }            
        }
        else if(currInst.getOpcode().equals("HLT")) {  //HLT
            this.hltMode = true;
        }
        else {
            throw new UnrecognizedInstructionException();
        }
        
        if(currInst.isRegInstruction()) {
            this.updateConditionCodes(targetReg.getContent());
        }
        
        this.instMem.updatePC(this.regPC.getContent());
    }
    
    private int getMemAddress(Instruction inst) {
        if(inst.getMode().equals("DIR")) { //direct mode
            return inst.getMemAddress();
        }
        else { //indexing mode
            return inst.getMemAddress() + this.regX.getContent();
        }
    }
    
    private Register getTargetRegister(Instruction inst) {
        if(inst.getReg().equals("RA")) {
            return this.regA;
        }
        else if(inst.getReg().equals("RB")) {
            return this.regB;
        }
        else if(inst.getReg().equals("RC")) {
            return this.regC;
        }
        else if(inst.getReg().equals("RX")) {
            return this.regX;
        }
        else {
            return null;
        }
    }
    
    public void resetRegisters() {
        this.ccN = false;
        this.ccZ = false;
        this.regA.setContent(0);
        this.regB.setContent(0);
        this.regC.setContent(0);
        this.regX.setContent(0);
        this.regPC.setContent(0);
        this.regRI.setContent(0);
        this.instMem.updatePC(this.regPC.getContent());
        this.hltMode = false;
        
    }

    public Instruction getCurrentInstruction() {
        return this.instMem.read(this.regPC.getContent());
    }
    
    public void parseInstMemFile(File instMemFile) throws FileNotFoundException {
        this.instMem = new InstructionMemory(instMemFile);
    }
    
    void parseInstMemFile(String inlineInstructions) {
        this.instMem = new InstructionMemory(inlineInstructions);
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

    public Register getRegC() {
        return regC;
    }

    public Register getRegX() {
        return regX;
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

    String getSavedMemories() {
        String returnable = "";
        returnable += this.instMem.getSavedMem();
        returnable += this.dataMem.getSavedMem();
        return returnable;
    }

    
}
