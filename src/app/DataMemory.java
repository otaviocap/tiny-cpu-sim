package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class DataMemory {
    private ObservableList<MemData> memList;
    private final int DATA_MEM_SIZE = 256;

    public DataMemory() {
        memList = FXCollections.observableArrayList();
        for (int add = 0; add < DATA_MEM_SIZE; add++) {
            memList.add(new MemData(Byte.valueOf("0"), add));
        }
        
    }
    
    @SuppressWarnings("empty-statement")
    public DataMemory(File memFile) throws FileNotFoundException {
        memList = FXCollections.observableArrayList();
        Scanner fileScan = new Scanner(memFile);
        
        while(!fileScan.nextLine().equals("DATA"));
        
        int addCount = 0;
        while(fileScan.hasNext()) {
            memList.add(new MemData(Byte.valueOf(fileScan.nextLine(), 10), addCount));
            addCount += 1;
        }
        
        while(addCount < DATA_MEM_SIZE) {
            memList.add(new MemData(Byte.valueOf("0"), addCount));
            addCount += 1;
        }
    }

    public String getSavedMem() {
        String returnable = "DATA\n";
        for (MemData data : memList) {
            returnable += data.getWord() + "\n";
        }
        return returnable;
    }
    
    public ObservableList<MemData> getMemList() {
        return FXCollections.observableArrayList(this.memList);
    }
    
    public void setMemData(int address, String wordStr) {
        MemData data = this.memList.get(address);
        data.setWordStr(wordStr);
    }

    public Integer read(int address) {
        return this.memList.get(address).getWordInt();
    }

    public void write(int address, Integer content) {
        this.memList.get(address).setWordInt(content);
    }
}
