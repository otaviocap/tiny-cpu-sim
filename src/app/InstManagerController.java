package app;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class InstManagerController implements Initializable {

    @FXML 
    private ComboBox instComboBox, regComboBox, ccComboBox, modeComboBox;
    
    @FXML
    private TextField memTextField, assemblyTextField, hexCodeTextField;
    
    @FXML
    private Circle c15, c14, c13, c12, c11, c10, c9, c8, c7, c6, c5, c4, c3, c2, c1, c0;
    
    private ArrayList<Circle> vecBinCircles;    
    
    private String binContent;
    
    private Map<String,String> opcodeToBin;
    private Map<String,String> regToBin;
    private Map<String,String> ccToBin;
    private Map<String,String> modeToBin;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.initTranslationTables();
        
        this.binContent = "xxxxxxxxxxxxxxxx";
        
        this.vecBinCircles = new ArrayList();
        this.vecBinCircles.add(0, c0);
        this.vecBinCircles.add(0, c1);
        this.vecBinCircles.add(0, c2);
        this.vecBinCircles.add(0, c3);
        this.vecBinCircles.add(0, c4);
        this.vecBinCircles.add(0, c5);
        this.vecBinCircles.add(0, c6);
        this.vecBinCircles.add(0, c7);
        this.vecBinCircles.add(0, c8);
        this.vecBinCircles.add(0, c9);
        this.vecBinCircles.add(0, c10);
        this.vecBinCircles.add(0, c11);
        this.vecBinCircles.add(0, c12);
        this.vecBinCircles.add(0, c13);
        this.vecBinCircles.add(0, c14);
        this.vecBinCircles.add(0, c15);
        
        this.initComponents();
    }   
    
    private void initComponents() {
        this.instComboBox.getItems().addAll("","LDR", "STR", "ADD", "SUB", "JMP", "JC", "HLT");
        this.regComboBox.getItems().addAll("", "RA", "RB", "RC", "RX");
        this.ccComboBox.getItems().addAll("", "Z", "N");
        this.modeComboBox.getItems().addAll("", "DIR", "IMM", "IDX"); 
        
        this.regComboBox.setDisable(true);
        this.ccComboBox.setDisable(true);
        this.modeComboBox.setDisable(true);
        this.memTextField.setDisable(false);

        this.instComboBox.getSelectionModel().select(0);
        this.regComboBox.getSelectionModel().select(0);
        this.ccComboBox.getSelectionModel().select(0);
        this.modeComboBox.getSelectionModel().select(0);        
        
        this.instComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.equals("LDR") || newValue.equals("STR") || newValue.equals("ADD") || newValue.equals("SUB")) {
                    regComboBox.setDisable(false);
                    ccComboBox.setDisable(true);
                    ccComboBox.getSelectionModel().select(0);
                    modeComboBox.setDisable(false);
                }
                else if(newValue.equals("JMP") || newValue.equals("HLT") || newValue.equals("")) {
                    regComboBox.setDisable(true);
                    regComboBox.getSelectionModel().select(0);
                    ccComboBox.setDisable(true);
                    ccComboBox.getSelectionModel().select(0);
                    modeComboBox.setDisable(true);
                    modeComboBox.getSelectionModel().select(0);
                    if(newValue.equals("HLT")) {
                        memTextField.setText("");
                    }
                }
                else { //JC
                    regComboBox.setDisable(true);
                    regComboBox.getSelectionModel().select(0);
                    ccComboBox.setDisable(false);
                    modeComboBox.setDisable(true);
                    modeComboBox.getSelectionModel().select(0);
                }
                
                updateBuiltInstruction();
            }           
            
        });
        
        this.regComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateBuiltInstruction();
            }            
        });
        
        this.ccComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateBuiltInstruction();
            }            
        });
        
        this.modeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateBuiltInstruction();
            }            
        });
        
        this.memTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateBuiltInstruction();
            }            
        });
        
        this.updateBuiltInstruction();
    }
    
    private void updateBuiltInstruction() {
        String instSelection = (String) this.instComboBox.getValue();
        String regSelection = (String) this.regComboBox.getValue();
        String ccSelection = (String) this.ccComboBox.getValue();
        String modeSelection = (String) this.modeComboBox.getValue();
        String memText = this.memTextField.getText();
        
        this.binContent = (instSelection == null || "".equals(instSelection)) ? "xxx" : this.opcodeToBin.get(instSelection);
        this.binContent += (regSelection == null || "".equals(regSelection)) ? "xx" : this.regToBin.get(regSelection);
        this.binContent += (ccSelection == null || "".equals(ccSelection)) ? "x" : this.ccToBin.get(ccSelection);
        this.binContent += (modeSelection == null || "".equals(modeSelection)) ? "xx" : this.modeToBin.get(modeSelection);
        
        if(memText == null || "".equals(memText)) {
            this.binContent +=  "xxxxxxxx";
        }
        else {
            try {            
                String binMem = Integer.toBinaryString(Integer.parseInt(memText));
                
                while(binMem.length() < 8) {
                    binMem = "0" + binMem;
                }
                this.binContent += binMem;
            }
            catch(NumberFormatException nfe) {
                this.binContent += "xxxxxxxx";
            }
        }
            
        this.updateBinCircles();
        
        if(this.isValidInstruction()) {
            Instruction inst = new Instruction(0, 0);
            inst.setBinWord(binContent);
            
            this.hexCodeTextField.setText(inst.getHexWord());
            this.assemblyTextField.setText(inst.getAssembly());
        }
        else {
            this.hexCodeTextField.setText("");
            this.assemblyTextField.setText("");
        }
        
    }
    
    private void initTranslationTables() {
        this.opcodeToBin = new TreeMap<>();
        this.opcodeToBin.put("LDR", "000");
        this.opcodeToBin.put("STR", "001");
        this.opcodeToBin.put("ADD", "010");
        this.opcodeToBin.put("SUB", "011");
        this.opcodeToBin.put("JMP", "100");
        this.opcodeToBin.put("JC", "101");
        this.opcodeToBin.put("HLT", "111");

        this.regToBin = new TreeMap<>();
        this.regToBin.put("RA", "00");
        this.regToBin.put("RB", "01");
        this.regToBin.put("RC", "10");
        this.regToBin.put("RX", "11");

        this.ccToBin = new TreeMap<>();
        this.ccToBin.put("Z", "0");
        this.ccToBin.put("N", "1");
        
        this.modeToBin = new TreeMap<>();
        this.modeToBin.put("DIR", "00");
        this.modeToBin.put("IMM", "01");
        this.modeToBin.put("IDX", "10");
    }
     
    private void updateBinCircles() {
        for (int i = 0; i < 16; i++) {
            char bit = this.binContent.charAt(i);
            if(bit == '1') {
                this.vecBinCircles.get(i).setFill(Paint.valueOf("#90ee90")); //green  (ON)
            }
            else if(bit == '0') {
                this.vecBinCircles.get(i).setFill(Paint.valueOf("#737373")); //gray  (OFF)
            }
            else {
                this.vecBinCircles.get(i).setFill(Paint.valueOf("#ff8000")); //red  (ZZZ)
            }
        }
            
    }

    private boolean isValidInstruction() {
        String instSelection = (String) this.instComboBox.getValue();
        String regSelection = (String) this.regComboBox.getValue();
        String ccSelection = (String) this.ccComboBox.getValue();
        String modeSelection = (String) this.modeComboBox.getValue();
        String memText = this.memTextField.getText();
        
        boolean isRegInst = (instSelection.equals("LDR") || instSelection.equals("STR") || instSelection.equals("ADD") || instSelection.equals("SUB"));
        boolean isJC = instSelection.equals("JC");
        boolean isJMP = instSelection.equals("JMP");
        boolean isHLT = instSelection.equals("HLT");

        boolean hasReg = !regSelection.equals("");
        boolean hasCC = !ccSelection.equals("");
        boolean hasMode = !modeSelection.equals("");
        boolean hasMem = !memText.equals("");

        return (((isRegInst && hasReg && hasMode) || (isJC && hasCC) || (isJMP)) && hasMem) || isHLT;
        
    }
}
  
