package tinycpu.simulator.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import tinycpu.simulator.Engine.Exceptions.IllegalInstructionException;
import tinycpu.simulator.Engine.Instructions.Instruction;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class InstManagerController implements Initializable {
    boolean updateByApp;
    @FXML
    private ComboBox<String> instComboBox, regComboBox, ccComboBox;
    @FXML
    private TextField memTextField, assemblyTextField, hexCodeTextField, addressTextField;
    @FXML
    private Circle c7, c6, c5, c4, c3, c2, c1, c0;
    @FXML
    private Button updateButton;
    private ArrayList<Circle> vecBinCircles;
    private Instruction currInstruction;

    private AppController appController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.currInstruction = new Instruction(0);
        this.updateByApp = false;

        this.vecBinCircles = new ArrayList<>();
        this.vecBinCircles.add(0, c0);
        this.vecBinCircles.add(0, c1);
        this.vecBinCircles.add(0, c2);
        this.vecBinCircles.add(0, c3);
        this.vecBinCircles.add(0, c4);
        this.vecBinCircles.add(0, c5);
        this.vecBinCircles.add(0, c6);
        this.vecBinCircles.add(0, c7);

        this.initComponents();
    }

    private void initComponents() {
        this.instComboBox.getItems().addAll("", "LDR", "STR", "ADD", "SUB", "JMP", "JC", "HLT");
        this.regComboBox.getItems().addAll("", "RA", "RB");
        this.ccComboBox.getItems().addAll("", "Z", "N");

        this.regComboBox.setDisable(true);
        this.ccComboBox.setDisable(true);
        this.memTextField.setDisable(false);

        this.instComboBox.getSelectionModel().select(0);
        this.regComboBox.getSelectionModel().select(0);
        this.ccComboBox.getSelectionModel().select(0);

        this.instComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("LDR") || newValue.equals("STR") || newValue.equals("ADD") || newValue.equals("SUB")) {
                regComboBox.setDisable(false);
                ccComboBox.setDisable(true);
                ccComboBox.getSelectionModel().select(0);
            } else if (newValue.equals("JMP") || newValue.equals("HLT") || newValue.isEmpty()) {
                regComboBox.setDisable(true);
                regComboBox.getSelectionModel().select(0);
                ccComboBox.setDisable(true);
                ccComboBox.getSelectionModel().select(0);
                if (newValue.equals("HLT")) {
                    memTextField.setText("");
                }
            } else { //JC
                regComboBox.setDisable(true);
                regComboBox.getSelectionModel().select(0);
                ccComboBox.setDisable(false);
            }

            if (!updateByApp)
                updateCurrInstruction();
        });

        this.regComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!updateByApp)
                updateCurrInstruction();
        });

        this.ccComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!updateByApp)
                updateCurrInstruction();
        });

        this.memTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!updateByApp)
                updateCurrInstruction();
        });

    }

    private void updateCurrInstruction() {
        String instSelection = this.instComboBox.getValue();
        String regSelection = this.regComboBox.getValue();
        String ccSelection = this.ccComboBox.getValue();
        String memText = this.memTextField.getText();

        try {
            this.currInstruction = new Instruction(currInstruction.getAddress(), instSelection, regSelection, ccSelection, memText);
            this.updateInstructionGUI();
        } catch (IllegalInstructionException e) {
            throw new RuntimeException(e);
        }

    }


    private void updateBinCircles() {
        for (int i = 0; i < vecBinCircles.size(); i++) {
            char bit = this.currInstruction.getBinWord().charAt(i);
            if (bit == '1') {
                this.vecBinCircles.get(i).setFill(Paint.valueOf("#90ee90")); //green  (ON)
            } else if (bit == '0') {
                this.vecBinCircles.get(i).setFill(Paint.valueOf("#737373")); //gray  (OFF)
            } else {
                this.vecBinCircles.get(i).setFill(Paint.valueOf("#ff8000")); //red  (ZZZ)
            }
        }
    }

    private void resetBinCircles() {
        for (Circle vecBinCircle : vecBinCircles) {
            vecBinCircle.setFill(Paint.valueOf("#737373")); //gray  (OFF)
        }
    }

    @FXML
    public void handleUpdateButton() {
        this.appController.setInstruction(currInstruction);
        this.appController.setNextSelectedInstruction(currInstruction.getAddress() + 1);
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

        if (this.currInstruction.isAssigned()) {
            this.instComboBox.getSelectionModel().select(this.currInstruction.getOpcode());
            this.regComboBox.getSelectionModel().select(this.currInstruction.getReg());
            this.ccComboBox.getSelectionModel().select(this.currInstruction.getCC());
            this.memTextField.setText(this.currInstruction.getMemAddress().toString());
        } else {
            this.instComboBox.getSelectionModel().select(0);
            this.regComboBox.getSelectionModel().select(0);
            this.ccComboBox.getSelectionModel().select(0);
            this.memTextField.setText("");
        }
    }

    private void updateInstructionGUI() {

        this.addressTextField.setText(currInstruction.getAddress().toString());

        if (currInstruction.isAssigned()) {
            this.updateBinCircles();
            this.addressTextField.setText(currInstruction.getAddress().toString());
            this.hexCodeTextField.setText(currInstruction.getHexWord());
            this.assemblyTextField.setText(currInstruction.toString());
            this.updateButton.setDisable(false);
        } else {
            this.resetBinCircles();
            this.hexCodeTextField.setText("");
            this.assemblyTextField.setText("");
            this.updateButton.setDisable(true);
        }
    }
}
  
