package javafxapplication1;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Progress implements Runnable {

    private Stage progressStage;
    private Thread th;
    private Node node;
    private ArrayList<File> str;
    private int count;
    private Label label;
    private Task taskWorker;
    private StackPane layout;
    private Scene scene;
    private ProgressBar progressBar;

    public Progress(Node node) {
        this.node = node;
        label = new Label();
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        VBox vBox = new VBox(5);
        vBox.setStyle("-fx-border-color: black; -fx-padding: 10;");
        Button button = new Button("pick");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                getProgressStage().close();
            }
        });
        vBox.getChildren().addAll(progressBar, label, button);
        layout = new StackPane();
        layout.setStyle("-fx-background-color: white;");
        layout.getChildren().add(vBox);

        taskWorker = createWorker();
        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(taskWorker.progressProperty());

        taskWorker.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(newValue);
            }
        });
        scene = new Scene(layout);
    }

    public void closeStage() {
        getProgressStage().hide();
        th.stop();
    }

    @Override
    public void run() {
        Thread th = new Thread(taskWorker);
        th.setDaemon(true);
        th.start();
        if (progressStage != null) {
            if (progressStage.isShowing()) {
                progressStage.close();
                taskWorker = createWorker();
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(taskWorker.progressProperty());
            }
        }

        progressStage = new Stage();

        getProgressStage().setWidth(300);
        getProgressStage().initOwner(node.getScene().getWindow());
        getProgressStage().initStyle(StageStyle.UNDECORATED);
        getProgressStage().setScene(scene);
        getProgressStage().show();
    }

    public void setStringArray(Collection<File> str) {
        ArrayList<File> list = new ArrayList<File>(str);
        this.str = list;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Task createWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < count; i++) {
                    Thread.sleep(50);
                    updateProgress(i + 1, count);
                    updateLabelLater(label, str.get(i).getName());
                }
                return null;
            }

            @Override
            protected void succeeded() {
                //updateProgress(0, count);
                progressStage.close();
                super.succeeded(); //To change body of generated methods, choose Tools | Templates.
            }

            void updateLabelLater(final Label label, final String text) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        label.setGraphic(null);
                        label.setText(text);
                    }
                });
            }
        };
    }

    public Stage getProgressStage() {
        return progressStage;
    }
}
