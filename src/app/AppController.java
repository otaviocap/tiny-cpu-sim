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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author felipe
 */
public class AppController implements Initializable {
    
    private TinyCPU simulator;
    
    @FXML
    private TableView instMemTableView;
    @FXML
    private TableColumn pcInstTableColumn;
    @FXML
    private TableColumn addressInstTableColumn;
    @FXML
    private TableColumn wordInstTableColumn;
    @FXML
    private TableColumn assemblyTableColumn;
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.simulator = new TinyCPU();
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
        
        if (dataMemFile != null) {
            System.out.println("TODO implement");
        }
    }

    private void initInstMemTableView() {
        this.addressInstTableColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        this.pcInstTableColumn.setCellValueFactory(new PropertyValueFactory<>("pcIsHere"));
        this.assemblyTableColumn.setCellValueFactory(new PropertyValueFactory<>("assembly"));
        this.wordInstTableColumn.setCellValueFactory(new PropertyValueFactory<>("word"));
        
        this.instMemTableView.setItems(this.simulator.getInstMem());
        
        System.out.println(this.simulator.getInstMem());
    }
}
