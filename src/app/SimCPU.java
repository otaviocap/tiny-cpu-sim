package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SimCPU extends Application {
    
    private static Scene scene;
  
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("AppView.fxml"));
        
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double y = bounds.getMinY() + (bounds.getHeight() - scene.getHeight()) * 0.7;
        stage.setY(y);
    }  
    
    public static Scene getScene() {
        return scene;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
