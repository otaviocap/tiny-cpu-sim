package app;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
    private TextField memTextField, assemblyTextField, hexCodeTextField, addressTextField;
    
    @FXML
    private Circle c15, c14, c13, c12, c11, c10, c9, c8, c7, c6, c5, c4, c3, c2, c1, c0;
    
    private ArrayList<Circle> vecBinCircles;  
    
    boolean updateByApp;
    private Instruction currInstruction;
    
    private AppController appController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.currInstruction = new Instruction(0);
        this.updateByApp = false;
        
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
                
                if(!updateByApp)
                    updateCurrInstruction();
            }           
            
        });
        
        this.regComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!updateByApp)
                    updateCurrInstruction();
            }            
        });
        
        this.ccComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!updateByApp)
                    updateCurrInstruction();
            }            
        });
        
        this.modeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!updateByApp)
                    updateCurrInstruction();
            }            
        });
        
        this.memTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!updateByApp)
                    updateCurrInstruction();
            }            
        });
        
    }
    
    private void updateCurrInstruction() {
        String instSelection = (String) this.instComboBox.getValue();
        String regSelection = (String) this.regComboBox.getValue();
        String ccSelection = (String) this.ccComboBox.getValue();
        String modeSelection = (String) this.modeComboBox.getValue();
        String memText = this.memTextField.getText();
        
        try {
            Instruction tmp = new Instruction(currInstruction.getAddress(), instSelection, regSelection, ccSelection, modeSelection, memText);
            this.currInstruction = tmp;
            this.updateInstructionGUI();            
        }
        catch(IllegalInstructionException iie) {
            
        } 
        
    }

     
    private void updateBinCircles() {
        for (int i = 0; i < 16; i++) {
            char bit = this.currInstruction.getBinWord().charAt(i);
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
    
    @FXML
    public void handleAddButton(ActionEvent event) {
        System.out.println(currInstruction);
        this.appController.setInstruction(currInstruction);
    }

    public void setAppController(AppController aThis) {
        this.appController = aThis;
    }
    
    public void setCurrentInst(Instruction inst) {
        this.updateByApp = true;
        
        this.currInstruction = new Instruction(inst);
        this.updateComboBoxes();
        this.updateInstructionGUI();
        
        this.updateByApp = false;
    }
    
    private void updateComboBoxes() {
        
        if(this.currInstruction.isAssigned()) {
            this.instComboBox.getSelectionModel().select(this.currInstruction.getOpcode());
            this.regComboBox.getSelectionModel().select(this.currInstruction.getReg());
            this.ccComboBox.getSelectionModel().select(this.currInstruction.getCC());
            this.modeComboBox.getSelectionModel().select(this.currInstruction.getMode());
            this.memTextField.setText(this.currInstruction.getMemAddress().toString());
        }
        else {
            this.instComboBox.getSelectionModel().select(0);
            this.regComboBox.getSelectionModel().select(0);
            this.ccComboBox.getSelectionModel().select(0);
            this.modeComboBox.getSelectionModel().select(0);
            this.memTextField.setText("");
        }
    }

    private void updateInstructionGUI() {
        
        this.addressTextField.setText(currInstruction.getAddress().toString());
        
        if(currInstruction.isAssigned()) {
            this.updateBinCircles();
            this.addressTextField.setText(currInstruction.getAddress().toString());
            this.hexCodeTextField.setText(currInstruction.getHexWord());
            this.assemblyTextField.setText(currInstruction.toString()); 
        }
        else {
            this.hexCodeTextField.setText("");
            this.assemblyTextField.setText("");
        }
    }
}
  
