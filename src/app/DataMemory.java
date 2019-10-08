package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class DataMemory {
    private ObservableList<MemData> memList;

    public DataMemory() {
        memList = FXCollections.observableArrayList();
        for (int add = 0; add < 16; add++) {
            memList.add(new MemData(Byte.valueOf("0"), add));
        }
        
    }
    
    public DataMemory(File memFile) throws FileNotFoundException {
        memList = FXCollections.observableArrayList();
        Scanner fileScan = new Scanner(memFile);
        
        int addCount = 0;
        while(fileScan.hasNext()) {
            memList.add(new MemData(Byte.valueOf(fileScan.nextLine(), 10), addCount));
            addCount += 1;
        }
        
        while(addCount < 16) {
            memList.add(new MemData(Byte.valueOf("0"), addCount));
        }
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
