package javafxapplication1.gui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafxapplication1.CustomHyperlink;
import javafxapplication1.SqlWorker;
import javafxapplication1.settingsHandlers.ArtistSettings;

public class ArtistContentPane extends BorderPane {

    private SqlWorker sqlWorker;
    private ResultSet resultSet;
    private String name;
    private Label titleLabel;
    private Button settingsButton;
    private int id;
    private File imagePath;
    private final File settingsIconPath = new File("resources\\settings16.png");
    private Tab tab;

    public ArtistContentPane(String name, int id, SqlWorker sqlWorker, Tab tab) throws MalformedURLException, IOException {
        this.name = name;
        this.id = id;
        this.sqlWorker = sqlWorker;
        this.tab = tab;
        setTop(fillTop());
        setLeft(fillLeft());
        setCenter(fillCenter());
    }

    private String getAlbumsCount() throws SQLException {
        try {
            resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount FROM albums;");
            resultSet.next();
        } catch (SQLException ex) {
            System.out.println("##### Ошибка добавления статистики по исполнителю");
        }
        return resultSet.getString("aCount");
    }

    private HBox fillTop() throws MalformedURLException, IOException {
        ImageView settingsImageView = new ImageView(new Image(settingsIconPath.toURI().toString()));

        titleLabel = new Label(name);
        titleLabel.setFont(new Font("Verdana", 18));
        settingsButton = new Button("", settingsImageView);
        settingsButton.setMaxSize(16, 16);
        settingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                settingsButton.setDisable(true);
                ArtistSettings artistSettings = new ArtistSettings(settingsButton, id, sqlWorker);
                artistSettings.setButton(settingsButton);
                artistSettings.initStage();
            }
        });

        Region reg = new Region();
        reg.setPrefHeight(1);
        reg.setPrefWidth(50);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(3);
        hbox.getChildren().addAll(titleLabel, reg, settingsButton);
        hbox.setStyle("-fx-border-color: black;");
        hbox.setPadding(new Insets(5));

        hbox.setHgrow(reg, Priority.ALWAYS);
        return hbox;
    }

    private ScrollPane fillLeft() {
        VBox leftVBox = new VBox();
        VBox vbox1 = new VBox(5);
        vbox1.setAlignment(Pos.CENTER);
        ImageView imageView = null;

        //leftVBox.setMinWidth(200);
        //leftVBox.setStyle("-fx-border-color: black;");

        final File f1 = new File("dislike.png");
        final File f2 = new File("like.png");

        final ToggleButton toggle = new ToggleButton();
        toggle.setMinSize(35, 35);
        toggle.setMaxSize(35, 35);

        if (!isFavourite()) {
            toggle.setStyle("-fx-background-image: url('" + f1.toURI().toString() + "'); -fx-background-repeat: no-repeat;"
                    + " -fx-background-position: center; -fx-background-insets:35,35;");
        } else {
            toggle.setStyle("-fx-background-image: url('" + f2.toURI().toString()
                    + "'); -fx-background-repeat: no-repeat;"
                    + " -fx-background-position: center; -fx-background-insets:35,35;");
        }

        toggle.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (toggle.isSelected()) {
                    toggle.setStyle("-fx-background-image: url('" + f2.toURI().toString()
                            + "'); -fx-background-repeat: no-repeat;"
                            + " -fx-background-position: center; -fx-background-insets:35,35;");
                    updateFavoutite(1);
                } else {
                    toggle.setStyle("-fx-background-image: url('" + f1.toURI().toString()
                            + "'); -fx-background-repeat: no-repeat;"
                            + " -fx-background-position: center; -fx-background-insets:35,35;");
                    updateFavoutite(0);
                }
            }
        });

        try {
            PreparedStatement stmt = sqlWorker.getConnection().
                    prepareStatement("SELECT img FROM artists WHERE idartist = ?");
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            resultSet.next();
            imagePath = new File(resultSet.getString("img"));
            imageView = new ImageView(new Image(imagePath.toURI().toString(), 150, 150, false, true));
        } catch (SQLException ex) {
            System.out.println("##### Ошибка заполнения левой части ArtistContentPane");
        }

        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        Stage st = new Stage();
                        st.initOwner(settingsButton.getScene().getWindow());
                        st.initStyle(StageStyle.UTILITY);
                        st.setResizable(false);
                        st.setScene(new Scene(new Group(new ImageView(imagePath.toURI().toString()))));
                        st.show();
                    }
                }
            }
        });

        vbox1.getChildren().addAll(imageView);
        vbox1.setMargin(imageView, new Insets(10));

        VBox vbox2 = new VBox();
        vbox2.setPadding(new Insets(10));
        vbox2.setSpacing(5);
        addGenresToVBox(vbox2);
        //addLabelToVBox(vbox2);

        ScrollPane scrollPane = new ScrollPane();
        
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(ScrollPane.USE_COMPUTED_SIZE);
        scrollPane.setPrefWidth(200);
        scrollPane.setMinSize(ScrollPane.USE_COMPUTED_SIZE, ScrollPane.USE_COMPUTED_SIZE);
        scrollPane.setMaxSize(ScrollPane.USE_COMPUTED_SIZE, ScrollPane.USE_COMPUTED_SIZE);

        leftVBox.getChildren().addAll(vbox1, vbox2);
        scrollPane.setContent(leftVBox);
        return scrollPane;
    }

    private void updateFavoutite(int isLike) {
        try {
            String query = "UPDATE artists SET fav = ? WHERE idartist = ?";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, isLike);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("##### Ошибка Like Atrist");
        }
    }
    
    private void addYearsToVBox(VBox mainVBox) {
        Label yearsLabel = new Label("Годы деятельности");
        yearsLabel.setFont(Font.font(null, FontWeight.BOLD, 14));
        
        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(0, 0, 0, 15));
        
    }
    
    private void addCountryToVBox(VBox mainVBox){
        Label countryLabel = new Label("Страна");
        countryLabel.setFont(Font.font(null, FontWeight.BOLD, 14));
        
        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(0, 0, 0, 15));
    }

    private void addGenresToVBox(VBox mainVBox) {
        Label genreTitleLabel = new Label("Жанры");
        genreTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(0, 0, 0, 15));
        try {
            String query = "SELECT DISTINCT a.Genre FROM "
                    + "(SELECT Genres.name as Genre, Albums.name as Album "
                    + "FROM Albums INNER JOIN "
                    + "(Genres INNER JOIN albums_has_genres "
                    + "ON Genres.idgenre=albums_has_genres.genres_idgenre) "
                    + "ON Albums.idalbum=albums_has_genres.albums_idalbum "
                    + "WHERE (albums_has_genres.albums_idalbum=Albums.idalbum "
                    + "And albums_has_genres.genres_idgenre=Genres.idgenre)) AS a "
                    + "INNER JOIN "
                    + "(SELECT Artists.name as Artist, Albums.name as Album "
                    + "FROM Artists INNER JOIN "
                    + "(Albums INNER JOIN artist_has_albums "
                    + "ON Albums.idalbum=artist_has_albums.album_idalbum) "
                    + "ON Artists.idartist=artist_has_albums.artist_idartist "
                    + "WHERE (artist_has_albums.album_idalbum=Albums.idalbum "
                    + "And artist_has_albums.artist_idartist=Artists.idArtist "
                    + "And Artists.idartist = ?)) AS b "
                    + "ON (a.Album=b.Album) WHERE (a.Album=b.Album) ORDER BY a.Genre;";

            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                if (!resultSet.getString("a.Genre").equals("")) {
                    vbox.getChildren().add(new Label(resultSet.getString("a.Genre")));
                }
                
                if (resultSet.isLast()) {
                    mainVBox.getChildren().addAll(genreTitleLabel, vbox);
                }
            }
        } catch (SQLException ex) {
            System.out.println("##### Ошибка добавления жанров в Left");
        }

        
    }

    /*private void addLabelToVBox(VBox mainVBox) {
        Label typeTitleLabel = new Label("Издатель");
        typeTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(0, 0, 0, 15));
        vbox.getChildren().add(typeTitleLabel);

        try {
            String query = "SELECT Labels.name FROM Albums INNER JOIN "
                    + "Labels ON Albums.labels_idlabel=Labels.idlabel "
                    + "WHERE (Albums.labels_idlabel=Labels.idlabel AND Albums.idalbum = ?);";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                if (!resultSet.getString("Labels.name").equals("N/A")) {
                    vbox.getChildren().add(new Label(resultSet.getString("Labels.name")));
                    mainVBox.getChildren().addAll(typeTitleLabel, vbox);
                }
            }

        } catch (SQLException ex) {
            System.out.println("##### Ошибка добавления типа в Left");
        }
    }*/

    private boolean isFavourite() {
        boolean b = false;
        try {
            String query = "SELECT fav FROM artists WHERE idartist = ?";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            resultSet.next();
            if (resultSet.getInt("fav") == 0) {
                b = false;
            } else {
                b = true;
            }
        } catch (SQLException ex) {
            System.out.println("##### Ошибка определения isFavourite");
        }
        return b;
    }

    private ScrollPane fillCenter() {
        VBox vbox1 = new VBox();
        vbox1.setAlignment(Pos.TOP_CENTER);
        vbox1.setPadding(new Insets(10));
        //vbox1.setStyle("-fx-border-color: red;");

        final TextArea textArea = new TextArea();
        textArea.setPrefColumnCount(1);
        textArea.setPrefHeight(150);
        textArea.setWrapText(true);
        textArea.setStyle("-fx-background-color: transparent, -fx-text-box-border, -fx-control-inner-background; "
                + "-fx-background-insets: -1.4, 0, 1; -fx-background-radius: 1.4, 0, 0;"
                + "-fx-padding: 1;");

        Label bioTitleLabel = new Label("Biography");
        bioTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 18));

        Label discographyTitleLabel = new Label("Discography");
        discographyTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 18));

        vbox1.getChildren().addAll(bioTitleLabel, textArea,
                new Separator(Orientation.HORIZONTAL), discographyTitleLabel, addDiscographyBox());

        //------------------------

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vbox1);

        return scrollPane;
    }

    private VBox addDiscographyBox() {
        VBox vbox = new VBox(5);
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
                vbox.getChildren().add(new CustomHyperlink(sqlWorker, 
                        resultSet.getString("aYear") + " - " + resultSet.getString("name"),
                        resultSet.getString("name"), id, 2, tab));
            }
        } catch (SQLException ex) {
            System.out.println("##### Ошибка добавления дискографии в Center");
        }
        return vbox;
    }

    private String secondsToStringWithoutHour(int inSeconds) {
        int remainder = inSeconds % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;

        String disMinu = (minutes < 10 ? "0" : "") + minutes;
        String disSec = (seconds < 10 ? "0" : "") + seconds;

        return disMinu + ":" + disSec;
    }

    private String secondsToStringWithHour(int inSeconds) {
        int hours = inSeconds / 3600;
        int remainder = inSeconds % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;

        String disHour = (hours < 10 ? "0" : "") + hours;
        String disMinu = (minutes < 10 ? "0" : "") + minutes;
        String disSec = (seconds < 10 ? "0" : "") + seconds;

        return disHour + ":" + disMinu + ":" + disSec;
    }
}
