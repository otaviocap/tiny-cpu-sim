package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Observable;
import javafx.collections.ObservableList;

public class TinyCPUSimulator {
    private InstructionMemory instMem;
    private DataMemory dataMem;
    
    public TinyCPUSimulator() {
        this.instMem = new InstructionMemory();
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

    
}
