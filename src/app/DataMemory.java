package app;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class DataMemory {
    private ArrayList<MemData> memList;

    public DataMemory() {
        memList = new ArrayList<MemData>();
    }
    
    public DataMemory(File memFile) throws FileNotFoundException {
        memList = new ArrayList<MemData>();
        Scanner fileScan = new Scanner(memFile);
        
        int addCount = 0;
        while(fileScan.hasNext()) {
            memList.add(new MemData(Byte.valueOf(fileScan.nextLine(), 10), addCount));
            addCount += 1;
        }
    }

    ObservableList<MemData> getMemList() {
        return FXCollections.observableArrayList(this.memList);
    }   
}
