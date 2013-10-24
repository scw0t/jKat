package javafxapplication1.settingsHandlers;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafxapplication1.SqlWorker;

public class ArtistSettings {

    private SqlWorker sqlWorker;
    private ResultSet resultSet;
    private Button button;
    private Node node;
    private TextField nameTF;
    private TextField originTF;
    private ImageView imageView;
    private int id;

    public ArtistSettings(Node node, int id, SqlWorker sqlWorker) {
        this.node = node;
        this.id = id;
        this.sqlWorker = sqlWorker;
    }

    public void initStage() {
        Stage st = new Stage();
        st.initOwner(node.getScene().getWindow());
        Group root = new Group();
        Scene scene = new Scene(root, 600, 430);
        st.setScene(scene);
        //st.setResizable(false);
        root.getChildren().add(getMainPane());
        st.show();
        st.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                getButton().setDisable(false);
            }
        });
    }

    private TabPane getMainPane() {
        TabPane mainPane = new TabPane();
        mainPane.setPrefSize(600, 400);
        mainPane.setMinSize(600, 400);
        mainPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        mainPane.setPrefSize(TabPane.USE_COMPUTED_SIZE, TabPane.USE_COMPUTED_SIZE);
        mainPane.setStyle("-fx-border-color: black;");
        mainPane.getTabs().add(getMainTab());
        mainPane.getTabs().add(getBioTab());
        mainPane.getTabs().add(getMembersTab());
        mainPane.getTabs().add(getRelatedTab());
        fillContent();
        return mainPane;
    }

    private Tab getMainTab() {
        imageView = new ImageView();
        nameTF = new TextField();
        originTF = new TextField();
        Label nameLabel = new Label("Name");
        Label originLabel = new Label("Origin");

        GridPane gridPane = new GridPane();
        gridPane.setConstraints(nameLabel, 0, 0);
        gridPane.setConstraints(originLabel, 0, 1);
        gridPane.setConstraints(nameTF, 1, 0);
        gridPane.setConstraints(originTF, 1, 1);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.getChildren().addAll(nameLabel, originLabel, nameTF, originTF);
        //gridPane.setStyle("-fx-border-color: red;");

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10,10,0,10));
        vBox.setPrefSize(600, 400);
        vBox.setMinSize(600, 400);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.getChildren().addAll(imageView, gridPane);
        
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(20);
        buttonBox.setPadding(new Insets(10, 0, 10, 0)); 
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Отмена");
        okButton.setMaxWidth(Double.MAX_VALUE);
        cancelButton.setMaxWidth(Double.MAX_VALUE);
        Region region = new Region();
        region.setMinSize(400, 1);
        buttonBox.getChildren().addAll(region, okButton, cancelButton);
        buttonBox.setStyle("-fx-border-color: black;");
        
        vBox.getChildren().addAll(hBox, addTableView(), buttonBox);

        Tab t = new Tab("Main");
        vBox.setStyle("-fx-border-color: black;");
        t.setContent(vBox);
        return t;
    }

    private void fillContent() {
        try {
            String query = "SELECT * FROM Artists WHERE idartist = ?;";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            resultSet.next();
            nameTF.setText(resultSet.getString("name"));
            originTF.setText(resultSet.getString("origin"));
            File image = new File(resultSet.getString("img"));
            imageView.setImage(new Image(image.toURI().toString(), 150, 150, false, true));
        } catch (SQLException e) {
            System.out.println("##### Ошибка заполнения полей AstistSettings");
        }
    }

    private TableView addTableView() {

        ArrayList<TableItems> disqList = new ArrayList<>();

        try {
            String query = "SELECT Albums.name, Albums.aYear FROM Artists INNER JOIN"
                    + "(Albums INNER JOIN artist_has_albums"
                    + " ON Albums.idalbum = artist_has_albums.album_idalbum)"
                    + " ON Artists.idartist = artist_has_albums.artist_idartist"
                    + " WHERE (artist_has_albums.album_idalbum=Albums.idalbum"
                    + " And artist_has_albums.artist_idartist=Artists.idArtist"
                    + " And Artists.idartist = ?) ORDER BY aYear;";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                disqList.add(new TableItems(resultSet.getString("aYear"), resultSet.getString("name")));
            }

        } catch (SQLException e) {
            System.out.println("##### Ошибка заполнения полей AstistSettings");
        }

        TableColumn firstNameCol = new TableColumn();
        firstNameCol.setText("Year");
        firstNameCol.setEditable(true);
        firstNameCol.setCellValueFactory(new PropertyValueFactory("yearName"));

        TableColumn lastNameCol = new TableColumn();
        lastNameCol.setText("Album");
        lastNameCol.setEditable(true);
        lastNameCol.setCellValueFactory(new PropertyValueFactory("albName"));

        TableView tableView = new TableView();
        tableView.setEditable(true);
        tableView.setMinSize(TableView.USE_COMPUTED_SIZE, TableView.USE_COMPUTED_SIZE);
        tableView.setMaxSize(TableView.USE_COMPUTED_SIZE, TableView.USE_COMPUTED_SIZE);
        tableView.setPrefHeight(TableView.USE_COMPUTED_SIZE);

        ObservableList<TableItems> data = FXCollections.observableArrayList(disqList);
        tableView.setItems(data);
        tableView.getColumns().addAll(firstNameCol, lastNameCol);
        return tableView;
    }

    private Tab getBioTab() {
        TextArea textArea = new TextArea();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(textArea);
        Tab t = new Tab("Bio");
        t.setContent(vBox);

        return t;
    }

    private Tab getMembersTab() {
        TextArea textArea = new TextArea();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(textArea);
        Tab t = new Tab("Members");
        t.setContent(vBox);

        return t;
    }

    private Tab getRelatedTab() {
        TextArea textArea = new TextArea();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.getChildren().addAll(textArea);
        Tab t = new Tab("Related");
        t.setContent(vBox);
        
        return t;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public static class TableItems {

        private StringProperty albName;
        private StringProperty yearName;

        private TableItems(String year, String name) {
            this.yearName = new SimpleStringProperty(year);
            this.albName = new SimpleStringProperty(name);
        }

        public StringProperty yearNameProperty() {
            return yearName;
        }

        public StringProperty albNameProperty() {
            return albName;
        }
    }
    /*
     public class TableItems {

     public SimpleStringProperty yearProp = new SimpleStringProperty();
     public SimpleStringProperty albumProp = new SimpleStringProperty();

     public String getYearProp(){
     return yearProp.get();
     }
        
     public String getAlbumProp(){
     return albumProp.get();
     }

     }*/
}
