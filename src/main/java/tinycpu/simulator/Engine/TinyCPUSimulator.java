package tinycpu.simulator.Engine;

import javafx.collections.ObservableList;
import tinycpu.simulator.Engine.Exceptions.TimeoutException;
import tinycpu.simulator.Engine.Exceptions.UnrecognizedInstructionException;
import tinycpu.simulator.Engine.Instructions.Instruction;
import tinycpu.simulator.Engine.Instructions.InstructionMemory;
import tinycpu.simulator.Engine.Memory.DataMemory;
import tinycpu.simulator.Engine.Memory.MemData;
import tinycpu.simulator.Engine.Memory.Register;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

public class TinyCPUSimulator {

    private final Register regA, regB, regPC, regRI;
    private InstructionMemory instMem;
    private DataMemory dataMem;
    private Boolean ccZ, ccN;

    private Boolean hltMode;

    public TinyCPUSimulator() {
        this.instMem = new InstructionMemory();
        this.dataMem = new DataMemory();

        this.regA = new Register(0, 8);
        this.regB = new Register(0, 8);
        this.regPC = new Register(0, 8);
        this.regRI = new Register(0, 8);

        this.ccZ = false;
        this.ccN = false;

        this.hltMode = false;
    }

    public void run() throws UnrecognizedInstructionException, TimeoutException {  //until HLT
        int contTimeout = 0;
        while (!this.hltMode) {
            this.runNextInstruction();
            contTimeout++;
            int TIMEOUT = 10000;
            if (contTimeout > TIMEOUT) {
                throw new TimeoutException();
            }
        }
    }

    public void runNextInstruction() throws UnrecognizedInstructionException {
        if (this.hltMode || this.regPC.getContent() >= 255) {
            this.hltMode = true;
            return;
        }

        Instruction currInst = this.instMem.read(this.regPC.getContent());
        this.regRI.setContent(currInst.getWord());
        this.regPC.add(1);

        Register targetReg = null;

        if (currInst.isRegInstruction()) {
            targetReg = this.getTargetRegister(currInst);
        }

        int memAddress = currInst.getMemAddress();

        if (currInst.getOpcode().equals("LDR")) {  //LDR
            Objects.requireNonNull(targetReg).setContent(this.dataMem.read(memAddress));

        } else if (currInst.getOpcode().equals("STR")) {  //STR
            this.dataMem.write(memAddress, Objects.requireNonNull(targetReg).getContent());
        } else if (currInst.getOpcode().equals("ADD")) {  //ADD
            Integer operand = this.dataMem.read(memAddress);
            Objects.requireNonNull(targetReg).add(operand);
        } else if (currInst.getOpcode().equals("SUB")) {  //STR
            Integer operand = this.dataMem.read(memAddress);
            Objects.requireNonNull(targetReg).sub(operand);
        } else if (currInst.getOpcode().equals("JMP")) {  //JMP
            this.regPC.setContent(memAddress);
        } else if (currInst.getOpcode().equals("JC")) {  //JC
            if (currInst.getCC().equals("Z") && this.ccZ || currInst.getCC().equals("N") && this.ccN) {
                this.regPC.setContent(memAddress);
            }
        } else if (currInst.getOpcode().equals("HLT")) {  //HLT
            this.hltMode = true;
        } else {
            throw new UnrecognizedInstructionException();
        }

        if (currInst.isRegInstruction()) {
            this.updateConditionCodes(Objects.requireNonNull(targetReg).getContent());
        }

        this.instMem.updatePC(this.regPC.getContent());
    }


    private Register getTargetRegister(Instruction inst) {
        if (inst.getReg().equals("RA")) {
            return this.regA;
        } else if (inst.getReg().equals("RB")) {
            return this.regB;
        } else {
            return null;
        }
    }

    public void resetRegisters() {
        this.ccN = false;
        this.ccZ = false;
        this.regA.setContent(0);
        this.regB.setContent(0);
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

    @SuppressWarnings("unused")
    public void parseInstMemFile(String inlineInstructions) {
        this.instMem = new InstructionMemory(inlineInstructions);
    }

    public void parseDataMemFile(File dataMemFile) throws FileNotFoundException {
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

    public String getSavedMemories() {
        String returnable = "";
        returnable += this.instMem.getSavedMem();
        returnable += this.dataMem.getSavedMem();
        return returnable;
    }

    public void updateCurrEditedInst(Instruction newSelection) {
        if (newSelection != null) {
            int newAddress = newSelection.getAddress();
            this.instMem.getMemList().forEach(instruction ->
                    instruction.setIsBeingEdited(instruction.getAddress() == newAddress)
            );
        }
    }

    public void changeInstPositions(int address1, int address2) {
        Instruction temporary1 = this.instMem.read(address1);
        Instruction temporary2 = this.instMem.read(address2);
        this.instMem.setInst(address2, temporary1.getHexWord());
        this.instMem.setInst(address1, temporary2.getHexWord());

    }


}
