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
import javafx.stage.FileChooser;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Screen;
import javafx.stage.Stage;

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
    private TextField raTextField, rbTextField, rcTextField, rxTextField, pcTextField, riTextField;
    
    @FXML
    private Circle zCircle, nCircle;
    
    private InstManagerController instManagerController;
    
    private int selectedPos = -1;
    
    /*@FXML
    private TextArea loadInlineTextArea;
    */
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.simulator = new TinyCPUSimulator();
        this.initTableViews();
        this.updateDataInGUI();       
        
        try {
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("InstManagerView.fxml"));
            Parent root = loader.load();

            instManagerController = loader.getController();
            instManagerController.setAppController(this);
            instManagerController.setCurrentInst(this.simulator.getInstMem().get(0));
            
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show(); 
            
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            double y = bounds.getMinY() + (bounds.getHeight() - scene.getHeight()) * 0.1;
            stage.setY(y);
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    @FXML
    public void handleLoadMemoryButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        
        File memFile = fileChooser.showOpenDialog(SimCPU.getScene().getWindow());
        
        try { 
            if (memFile != null) {
                this.simulator.parseInstMemFile(memFile);
                this.initInstMemTableView();
                this.simulator.parseDataMemFile(memFile);
                this.initDataMemTableView();
                this.updateDataInGUI();
            }
        }
        catch(FileNotFoundException fnfe) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Erro ao abrir o arquivo!");
            alert.showAndWait();
        }
    }
    
    @FXML
    public void handleSaveMemoryButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        
        File memFile = fileChooser.showSaveDialog(SimCPU.getScene().getWindow());
        
        try {
            FileWriter writer = new FileWriter(memFile);
            writer.write(this.simulator.getSavedMemories());
            writer.close();
        }
        catch(IOException ioe) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Erro ao salvar o arquivo!");
            alert.showAndWait();
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
                    boolean wasPainted = false;
                    if (Objects.equals(item.getAddress(), simulator.getPC().getContent())) {
                        setStyle("-fx-background-color: tomato;");
                        wasPainted = true;
                    }
                    if (simulator.getCurrentInstruction().isJumpInstruction() && Objects.equals(simulator.getCurrentInstruction().getMemAddress(), item.getAddress())) {
                        setStyle("-fx-background-color: yellow;");
                        wasPainted = true;
                    }
                    if(!wasPainted) {
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
        
        this.instMemTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(newSelection != null) {
                this.instManagerController.setCurrentInst(newSelection);
            }
            //selectedPos = this.instMemTableView.getSelectionModel().getSelectedIndex();
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
                    
                    if (simulator.getCurrentInstruction().isAssigned() &&
                            !simulator.getCurrentInstruction().isJumpInstruction() && 
                            Objects.equals(item.getAddress(), simulator.getCurrentInstruction().getMemAddress())) {
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
            alert.showAndWait();
        }
        
    }
    
    @FXML
    private void handleRunButton(ActionEvent event) {
        try {
            this.simulator.run();
            this.updateDataInGUI();
        } 
        catch (UnrecognizedInstructionException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Instrução não reconhecida!");        
            alert.showAndWait();
        }
        catch (TimeoutException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Programa em looping!");     
            alert.showAndWait();
            /* Reseting CPU */
            this.simulator.resetRegisters();
            this.updateDataInGUI();
        }
        
    }
    
    @FXML 
    private void handleClearButton(ActionEvent event) {
        this.simulator = new TinyCPUSimulator();
        this.initTableViews();
        this.updateDataInGUI();
    }
    
    @FXML void handleResetButton(ActionEvent event) {
        this.simulator.resetRegisters();
        this.updateDataInGUI();
    }

    private void updateDataInGUI() {
        this.dataMemTableView.refresh();
        this.instMemTableView.refresh();
        
        this.raTextField.setText(this.simulator.getRegA().getContent().toString());
        this.rbTextField.setText(this.simulator.getRegB().getContent().toString());
        this.rcTextField.setText(this.simulator.getRegC().getContent().toString());
        this.rxTextField.setText(this.simulator.getRegX().getContent().toString());
        
        this.pcTextField.setText(this.simulator.getPC().getContent().toString());
        this.riTextField.setText(this.simulator.getRI().getHexContent());   
                
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

    /*@FXML
    private void handleLoadInline(ActionEvent event) {
        String inlineInstructions = this.loadInlineTextArea.getText();
        this.simulator.parseInstMemFile(inlineInstructions);
        this.initInstMemTableView();
        this.updateDataInGUI();
        this.loadInlineTextArea.setText("");
    }*/

    public void setInstruction(Instruction inst) {
        this.simulator.setInstMemPosition(inst.getAddress(), inst.getHexWord());
        this.updateDataInGUI();
    }
    
    @FXML
    private void handleEditButton(ActionEvent event) {
        /*System.out.println(selectedPos);
        if(selectedPos >= 0) {
            Instruction selectedInst = this.simulator.getInstMem().get(selectedPos);
            this.instManagerController.setCurrentInst(selectedInst);
        }*/
    }
}
