package javafxapplication1.settingsHandlers;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ReleaseSettings {

    private Button button;
    private Node node;
    
    public ReleaseSettings(Node node) {
        this.node = node;
        
    }
    
    public void initStage() {
        Stage st = new Stage();
        st.initOwner(node.getScene().getWindow());
        st.setScene(new Scene(new Group(new Text(100, 100, "Settings"))));
        st.show();
        
        st.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                getButton().setDisable(false);
            }
        });
    }
    
    public Button getButton() {
        return button;
    }
    
    public void setButton(Button button) {
        this.button = button;
    }
}
