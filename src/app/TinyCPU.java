package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import javafx.collections.ObservableList;

public class TinyCPU {
    private InstructionMemory instMem;
    
    public TinyCPU() {
        this.instMem = new InstructionMemory();
    }

    public void parseInstMemFile(File instMemFile) throws FileNotFoundException {
        this.instMem = new InstructionMemory(instMemFile);
    }
    
    public ObservableList<Instruction> getInstMem() {
        return instMem.getInstMemList();
    }
    
}
