package tinycpu.simulator.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import tinycpu.simulator.Components.ImageIcon;
import tinycpu.simulator.Engine.Exceptions.TimeoutException;
import tinycpu.simulator.Engine.Exceptions.UnrecognizedInstructionException;
import tinycpu.simulator.Engine.Instructions.Instruction;
import tinycpu.simulator.Engine.Memory.MemData;
import tinycpu.simulator.Engine.TinyCPUSimulator;
import tinycpu.simulator.SimCPU;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AppController implements Initializable {
    public static boolean isInstManagerOpen = false;
    public Button stepButton;
    public Button runButton;
    public Button resetButton;
    public Font x1;
    public Color x2;
    private TinyCPUSimulator simulator;
    @FXML
    private TableView<Instruction> instMemTableView;
    @FXML
    private TableColumn<Instruction, Integer> edInstTableColumn;
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

    @FXML
    private Button moveUpButton, moveDownButton;

    private InstManagerController instManagerController;

    private int selectedPos = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.simulator = new TinyCPUSimulator();
        this.initTableViews();
        this.updateDataInGUI();

        this.openInstManager();

        ImageIcon moveUpIM = new ImageIcon("move-up.png");
        this.moveUpButton.setGraphic(moveUpIM);

        ImageIcon moveDownIM = new ImageIcon("move-down.png");
        this.moveDownButton.setGraphic(moveDownIM);
    }

    @FXML
    public void handleLoadMemoryButton() {
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
        } catch (FileNotFoundException fileNotFoundException) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Erro ao abrir o arquivo!");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleSaveMemoryButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        File memFile = fileChooser.showSaveDialog(SimCPU.getScene().getWindow());

        try {
            String fileName = memFile.getAbsolutePath();
            if (!fileName.endsWith(".mem")) {
                memFile = new File(fileName + ".mem");
            }
            FileWriter writer = new FileWriter(memFile);
            writer.write(this.simulator.getSavedMemories());
            writer.close();
        } catch (IOException ioException) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Erro ao salvar o arquivo!");
            alert.showAndWait();
        }

    }

    private void initInstMemTableView() {
        this.addressInstTableColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        this.edInstTableColumn.setCellValueFactory(new PropertyValueFactory<>("isBeingEdited"));
        this.assemblyTableColumn.setCellValueFactory(new PropertyValueFactory<>("assembly"));
        this.wordInstTableColumn.setCellValueFactory(new PropertyValueFactory<>("hexWord"));

        this.instMemTableView.setItems(this.simulator.getInstMem());

        this.wordInstTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.wordInstTableColumn.setOnEditCommit(
                ((CellEditEvent<Instruction, String> t) -> {
                    Instruction inst = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    int address = inst.getAddress();
                    this.simulator.setInstMemPosition(address, t.getNewValue());
                    t.getTableView().getSelectionModel().clearSelection();
                    this.updateDataInGUI();
                })
        );

        this.instMemTableView.setRowFactory((tableView) -> new TableRow<>() {
            @Override
            public void updateItem(Instruction item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else {
                    boolean wasPainted = false;
                    if (Objects.equals(item.getAddress(), simulator.getPC().getContent())) {
                        setStyle("-fx-background-color: tomato;");
                        wasPainted = true;
                    }
                    if (simulator.getCurrentInstruction().isJumpInstruction() && Objects.equals(simulator.getCurrentInstruction().getMemAddress(), item.getAddress())) {
                        setStyle("-fx-background-color: yellow;");
                        wasPainted = true;
                    }
                    if (!wasPainted) {
                        setStyle("");
                    }
                }
            }
        });

        this.instMemTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.instManagerController.setCurrentInst(newSelection);
                this.simulator.updateCurrEditedInst(newSelection);

                this.updateDataInGUI();
                selectedPos = newSelection.getAddress();
            } else {
                selectedPos = -1;
            }
        });

    }


    private void initDataMemTableView() {
        this.addressDataTableColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        this.wordDataTableColumn.setCellValueFactory(new PropertyValueFactory<>("wordStr"));

        this.dataMemTableView.setItems(this.simulator.getDataMem());

        this.wordDataTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.wordDataTableColumn.setOnEditCommit(
                ((CellEditEvent<MemData, String> t) -> {
                    MemData data = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    int address = data.getAddress();
                    this.simulator.setDataMemPosition(address, t.getNewValue());
                    updateDataInGUI();
                })
        );

        this.dataMemTableView.setRowFactory((tv) -> new TableRow<>() {
            @Override
            public void updateItem(MemData item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else {

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

    }

    private void initTableViews() {
        this.initDataMemTableView();
        this.initInstMemTableView();
    }

    @FXML
    private void handleStepButton() {
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
    private void handleRunButton() {
        try {
            this.simulator.run();
            this.updateDataInGUI();
        } catch (UnrecognizedInstructionException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro!");
            alert.setHeaderText("Instrução não reconhecida!");
            alert.showAndWait();
        } catch (TimeoutException ex) {
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
    private void handleClearButton() {
        this.simulator = new TinyCPUSimulator();
        this.initTableViews();
        this.updateDataInGUI();
    }

    @FXML
    void handleResetButton() {
        this.simulator.resetRegisters();
        this.updateDataInGUI();
    }

    private void updateDataInGUI() {
        this.dataMemTableView.refresh();
        this.instMemTableView.refresh();

        this.raTextField.setText(this.simulator.getRegA().getContent().toString());
        this.rbTextField.setText(this.simulator.getRegB().getContent().toString());

        this.pcTextField.setText(this.simulator.getPC().getContent().toString());
        this.riTextField.setText(this.simulator.getRI().getHexContent());

        if (this.simulator.getCcZ()) {
            this.zCircle.setFill(Paint.valueOf("#90ee90")); //red  (ON)
        } else {
            this.zCircle.setFill(Paint.valueOf("#737373")); //gray (OFF)
        }

        if (this.simulator.getCcN()) {
            this.nCircle.setFill(Paint.valueOf("#90ee90")); //red  (ON)
        } else {
            this.nCircle.setFill(Paint.valueOf("#737373")); //gray (OFF)
        }

    }

    public void setInstruction(Instruction inst) {
        this.simulator.setInstMemPosition(inst.getAddress(), inst.getHexWord());
        this.updateDataInGUI();
    }

    private void openInstManager() {
        try {

            FXMLLoader loader = new FXMLLoader(SimCPU.class.getResource("InstManagerView.fxml"));
            Parent root = loader.load();

            instManagerController = loader.getController();
            instManagerController.setAppController(this);
            instManagerController.setCurrentInst(this.simulator.getInstMem().get(0));

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Instruction Manager");
            stage.show();

            stage.setOnCloseRequest(event -> isInstManagerOpen = false);

            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            double x = bounds.getMinX() + (bounds.getWidth() - scene.getWidth()) * 0.1;
            stage.setX(x);

            stage.setResizable(false);

            isInstManagerOpen = true;
        } catch (IOException ioe) {
            //ToDo: Remove this and make better logging
            ioe.printStackTrace();
        }
    }

    @FXML
    private void handleOpenInstructionManagerButton() {
        if (!isInstManagerOpen) {
            this.openInstManager();
        }
    }

    @FXML
    public void handleAboutButton() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About TinySPU!");
        alert.setHeaderText("TinyCPUSim v1.0");
        alert.setContentText("""
                    TinyCPU-Sim is an JavaFX-based open-source project for an instruction-level simulator for the teaching-purpose Central Processing Unit TinyCPU.
                        
                    Developed by Felipe Martin Sampaio.
                """);

        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(480, 240);

        alert.setWidth(300);
        alert.showAndWait();
    }

    @FXML
    void handleMoveUpButton() {
        int tmpSelectedPos = selectedPos;
        if (selectedPos != -1 && selectedPos > 0) {
            Instruction selectedInst = this.simulator.getInstMem().get(selectedPos);
            if (selectedInst.isAssigned() && !selectedInst.getOpcode().equals("HLT")) {
                this.simulator.changeInstPositions(selectedPos, selectedPos - 1);
                this.updateDataInGUI();
                this.instMemTableView.getSelectionModel().select(tmpSelectedPos - 1);
            }
        }
    }

    @FXML
    void handleMoveDownButton() {
        int tmpSelectedPos = selectedPos;
        if (selectedPos != -1 && selectedPos < 255) {
            Instruction selectedInst = this.simulator.getInstMem().get(selectedPos);
            Instruction postSelectedInst = this.simulator.getInstMem().get(selectedPos + 1);
            if (postSelectedInst.isAssigned()) {
                if (selectedInst.isAssigned() && !selectedInst.getOpcode().equals("HLT") && !postSelectedInst.getOpcode().equals("HLT")) {
                    this.simulator.changeInstPositions(selectedPos, selectedPos + 1);
                    this.updateDataInGUI();
                    this.instMemTableView.getSelectionModel().select(tmpSelectedPos + 1);
                }
            }
        }
    }

    public void setNextSelectedInstruction(int nextAddress) {
        this.instMemTableView.getSelectionModel().select(nextAddress);
    }

}
