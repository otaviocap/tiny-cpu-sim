/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import app.SimCPU;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

/**
 *
 * @author felipe
 */
public class AppController implements Initializable {
    
    private TinyCPUSimulator simulator;
    
    @FXML
    private TableView<Instruction> instMemTableView;
    @FXML
    private TableColumn<Instruction, Integer> pcInstTableColumn;
    @FXML
    private TableColumn<Instruction, Integer> addressInstTableColumn;
    @FXML
    private TableColumn<Instruction, String> wordInstTableColumn;
    @FXML
    private TableColumn<Instruction, String> assemblyTableColumn;
    
    @FXML
    private TableView<MemData> dataMemTableView;
    @FXML
    private TableColumn<MemData, Integer> addressDataTableColumn;
    @FXML
    private TableColumn<MemData, String> wordDataTableColumn;
    
    @FXML
    private TextField raTextField, rbTextField, pcTextField, riTextField;
    
    @FXML
    private Circle zCircle, nCircle;
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.simulator = new TinyCPUSimulator();
        this.initTableViews();
        this.updateDataInGUI();
    }
    
    @FXML
    public void handleInstructionMemoryLoad(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        
        File instMemFile = fileChooser.showOpenDialog(SimCPU.getScene().getWindow());
        
        try { 
            if (instMemFile != null) {
                this.simulator.parseInstMemFile(instMemFile);
                this.initInstMemTableView();
            }
        }
        catch(FileNotFoundException fnfe) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Erro ao abrir o arquivo!");
        }
    }
    
    @FXML
    public void handleDataMemoryLoad(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        
        File dataMemFile = fileChooser.showOpenDialog(SimCPU.getScene().getWindow());
        
        try { 
            if (dataMemFile != null) {
                this.simulator.parseDataMemFile(dataMemFile);
                this.initDataMemTableView();
            }
        }
        catch(FileNotFoundException fnfe) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Erro ao abrir o arquivo!");
        }
        
    }

    private void initInstMemTableView() {
        this.addressInstTableColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        this.pcInstTableColumn.setCellValueFactory(new PropertyValueFactory<>("pcIsHere"));
        this.assemblyTableColumn.setCellValueFactory(new PropertyValueFactory<>("assembly"));
        this.wordInstTableColumn.setCellValueFactory(new PropertyValueFactory<>("hexWord"));
        
        this.instMemTableView.setItems(this.simulator.getInstMem());
        
        this.wordInstTableColumn.setCellFactory(TextFieldTableCell.<Instruction>forTableColumn());
        this.wordInstTableColumn.setOnEditCommit(
            ((CellEditEvent<Instruction, String> t) -> {
                Instruction inst = (Instruction) t.getTableView().getItems().get(t.getTablePosition().getRow());
                int address = inst.getAddress();
                this.simulator.setInstMemPosition(address, t.getNewValue());
                t.getTableView().getSelectionModel().clearSelection();
                this.updateDataInGUI();
            })
        );
        
        this.instMemTableView.setRowFactory((tv) -> new TableRow<Instruction>() {
            @Override
            public void updateItem(Instruction item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                }
                else {
                    if (Objects.equals(item.getAddress(), simulator.getPC().getContent())) {
                        setStyle("-fx-background-color: tomato;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        this.instMemTableView.focusedProperty().addListener((ov, oldV, newV) -> {
            if(!newV) {
                instMemTableView.getSelectionModel().clearSelection();
            }
        });
        
    }
     

    private void initDataMemTableView() {
        this.addressDataTableColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        this.wordDataTableColumn.setCellValueFactory(new PropertyValueFactory<>("wordStr"));
        
        this.dataMemTableView.setItems(this.simulator.getDataMem());
        
        this.wordDataTableColumn.setCellFactory(TextFieldTableCell.<MemData>forTableColumn());
        this.wordDataTableColumn.setOnEditCommit(
            ((CellEditEvent<MemData, String> t) -> {
                MemData data = (MemData) t.getTableView().getItems().get(t.getTablePosition().getRow());
                int address = data.getAddress();
                this.simulator.setDataMemPosition(address, t.getNewValue());
                updateDataInGUI();
                t.getTableView().getSelectionModel().clearSelection();
            })
        );
        
        this.dataMemTableView.setRowFactory((tv) -> new TableRow<MemData>() {
            @Override
            public void updateItem(MemData item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null) {
                    setStyle("");
                }
                else {
                    
                    if (Objects.equals(item.getAddress(), simulator.getCurrentInstruction().getMemAddress())) {
                        setStyle("-fx-background-color: lightgreen;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        this.instMemTableView.focusedProperty().addListener((ov, oldV, newV) -> {
            if(!newV) {
                instMemTableView.getSelectionModel().clearSelection();
            }
        });
        
    }

    private void initTableViews() {
        this.initDataMemTableView();
        this.initInstMemTableView();
    }
    
    @FXML
    private void handleStepButton(ActionEvent event) {
        try {
            this.simulator.runNextInstruction();
            this.updateDataInGUI();
            
        } catch (UnrecognizedInstructionException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Instrução não reconhecida!");
        }
        
    }
    
    @FXML
    private void handleRunButton(ActionEvent event) {
        try {
            this.simulator.run();
            this.updateDataInGUI();
        } catch (UnrecognizedInstructionException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Instrução não reconhecida!");        }
    }
    
    @FXML 
    private void handleResetButton(ActionEvent event) {
        this.simulator = new TinyCPUSimulator();
        this.initTableViews();
        this.updateDataInGUI();
    }

    private void updateDataInGUI() {
        this.dataMemTableView.refresh();
        this.instMemTableView.refresh();
        
        this.raTextField.setText(this.simulator.getRegA().getContent().toString());
        this.rbTextField.setText(this.simulator.getRegB().getContent().toString());
        this.pcTextField.setText(this.simulator.getPC().getContent().toString());
        this.riTextField.setText(this.simulator.getRI().getContent().toString());   
                
        if(this.simulator.getCcZ()) {
            this.zCircle.setFill(Paint.valueOf("#90ee90")); //red  (ON)
        }
        else {
            this.zCircle.setFill(Paint.valueOf("#737373")); //gray (OFF)
        }
        
        if(this.simulator.getCcN()) {
            this.nCircle.setFill(Paint.valueOf("#90ee90")); //red  (ON)
        }
        else {
            this.nCircle.setFill(Paint.valueOf("#737373")); //gray (OFF)
        }
        
    }
}
