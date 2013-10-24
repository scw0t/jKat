/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

//import com.javafx.experiments.scenicview.ScenicView;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class JavaFXApplication1 extends Application {

    private Stage stage;

    @Override
    public void start(final Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setOnCloseRequest(
                new EventHandler<WindowEvent>() {
            public void handle(final WindowEvent event) {
                stage.close();
            }
        });


        Parent root = FXMLLoader.load(getClass().getResource("Sample.fxml"));

        Scene scene = new Scene(root);
        //ScenicView.show(scene);
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public Stage getStage(){
        return stage;
    }
    
}
