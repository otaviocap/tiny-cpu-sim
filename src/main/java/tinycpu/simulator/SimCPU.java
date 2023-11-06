package tinycpu.simulator;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class SimCPU extends Application {

    private static Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    public static Scene getScene() {
        return scene;
    }

    private static void handleExit(WindowEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);

        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to close TinyCPU-Sim?");

        Optional<ButtonType> result = alert.showAndWait();

        result.ifPresent(value -> {
            if (value == ButtonType.OK) {
                System.exit(0);
            }
        });

        event.consume();
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Better exit handling
        Platform.setImplicitExit(true);

        FXMLLoader fxmlLoader = new FXMLLoader(SimCPU.class.getResource("AppView.fxml"));
        scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(SimCPU::handleExit);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double x = bounds.getMinX() + (bounds.getWidth() - scene.getWidth()) * 0.7;
        stage.setX(x);
        stage.setResizable(false);
        stage.setTitle("TinyCPU Simulator");
    }

}
