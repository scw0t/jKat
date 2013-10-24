package javafxapplication1.gui;

import java.awt.Desktop;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafxapplication1.CustomHyperlink;
import javafxapplication1.CustomImageView;
import javafxapplication1.SqlWorker;
import javafxapplication1.settingsHandlers.ReleaseSettings;

public class AlbumContentPane extends BorderPane {

    private SqlWorker sqlWorker;
    private ResultSet resultSet;
    private String name;
    private Label titleLabel;
    private Button settingsButton;
    private Button openFolderButton;
    private int id;
    private File imagePath;
    private final File settingsIconPath = new File("resources\\settings16.png");
    private final File folderIconPath = new File("resources\\folder16.png");
    private Tab tab;

    public AlbumContentPane(String name, int id, SqlWorker sqlWorker, Tab tab) throws MalformedURLException, IOException {
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
        ImageView folderImageView = new ImageView(new Image(folderIconPath.toURI().toString()));

        titleLabel = new Label(name);
        titleLabel.setFont(new Font("Verdana", 18));
        settingsButton = new Button("", settingsImageView);
        settingsButton.setMaxSize(16, 16);
        settingsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                settingsButton.setDisable(true);
                ReleaseSettings releaseSettings = new ReleaseSettings(settingsButton);
                releaseSettings.setButton(settingsButton);
                releaseSettings.initStage();
            }
        });

        openFolderButton = new Button();
        openFolderButton.setMaxSize(16, 16);
        openFolderButton = new Button("", folderImageView);
        openFolderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    PreparedStatement stmt = sqlWorker.getConnection().
                            prepareStatement("SELECT sPath FROM Songs WHERE album_idalbum = ? LIMIT 1;");
                    stmt.setInt(1, id);
                    resultSet = stmt.executeQuery();
                    if (!resultSet.wasNull()) {
                        resultSet.next();
                        File dir = new File(resultSet.getString("sPath")).getParentFile();
                        if (Desktop.isDesktopSupported()) {
                            if (dir.exists()) {
                                Desktop.getDesktop().open(dir);
                            } else {
                                System.out.println("Folder does not exists");
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        Region reg = new Region();
        reg.setPrefHeight(1);
        reg.setPrefWidth(50);

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(3);
        hbox.getChildren().addAll(titleLabel, reg, openFolderButton, settingsButton);
        hbox.setStyle("-fx-border-color: black;");
        hbox.setPadding(new Insets(5));

        hbox.setHgrow(reg, Priority.ALWAYS);
        return hbox;
    }

    private ScrollPane fillLeft() {
        VBox leftVBox = new VBox();
        VBox vbox1 = new VBox(5);
        vbox1.setAlignment(Pos.CENTER);
        CustomImageView imageView = new CustomImageView(id, 2, sqlWorker);

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
                    prepareStatement("SELECT img FROM albums WHERE idalbum = ?");
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();

            resultSet.next();
            String img = resultSet.getString("img");
            if (!resultSet.wasNull()) {
                imagePath = new File(img);
                imageView.setImage(new Image(imagePath.toURI().toString(), 150, 150, false, true));
            }

        } catch (SQLException ex) {
            System.out.println("##### Ошибка заполнения левой части AlbumTab");
        }

        /*imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
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
         });*/

        vbox1.getChildren().addAll(imageView);
        vbox1.setMargin(imageView, new Insets(10));

        VBox vbox2 = new VBox();
        vbox2.setPadding(new Insets(10));
        vbox2.setSpacing(5);
        addArtistToVBox(vbox2);
        addYearToVBox(vbox2);
        addTypeToVBox(vbox2);
        addGenresToVBox(vbox2);
        addLabelToVBox(vbox2);
        addLengthToVBox(vbox2);

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
            String query = "UPDATE albums SET fav = ? WHERE idalbum = ?";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, isLike);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("##### Ошибка Like Album");
        }
    }

    private void addArtistToVBox(VBox mainVBox) {
        Label artistTitleLabel = new Label("Исполнитель");
        artistTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(0, 0, 0, 15));
        try {
            String query = "SELECT Artists.name FROM Albums INNER JOIN \n"
                    + "	(Artists INNER JOIN artist_has_albums \n"
                    + "	ON Artists.idartist = artist_has_albums.artist_idartist) \n"
                    + "	ON Albums.idalbum = artist_has_albums.album_idalbum \n"
                    + "	WHERE (artist_has_albums.album_idalbum = Albums.idalbum \n"
                    + "	AND artist_has_albums.artist_idartist = Artists.idArtist \n"
                    + "	AND Albums.idalbum = ?);";

            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                if (!resultSet.getString("Artists.name").equals("")) {
                    vbox.getChildren().add(new CustomHyperlink(sqlWorker,
                            resultSet.getString("Artists.name"),
                            resultSet.getString("Artists.name"), id, 1, tab));
                    mainVBox.getChildren().addAll(artistTitleLabel, vbox);
                }
            }
        } catch (SQLException ex) {
            System.out.println("##### Ошибка добавления исполнителя в Left");
        }
    }

    private void addYearToVBox(VBox mainVBox) {
        Label yearTitleLabel = new Label("Год");
        yearTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(0, 0, 0, 15));
        try {
            String query = "SELECT aYear FROM Albums WHERE Albums.idalbum = ?;";

            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            if (!resultSet.wasNull()) {
                while (resultSet.next()) {
                    if (!resultSet.getString("aYear").equals("")) {
                        vbox.getChildren().add(new Label(resultSet.getString("aYear")));
                        mainVBox.getChildren().addAll(yearTitleLabel, vbox);
                    }
                }
            }


        } catch (SQLException ex) {
            System.out.println("##### Ошибка добавления года в Left");
        }
    }

    private void addLengthToVBox(VBox mainVBox) {
        Label lengthTitleLabel = new Label("Продолжительность");
        lengthTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(0, 0, 0, 15));
        try {
            String query = "SELECT SUM(sLength) as sum FROM Songs WHERE album_idalbum = "
                    + "(SELECT Albums.idalbum FROM Albums WHERE idalbum = ?);";

            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();

            if (!resultSet.wasNull()) {
                while (resultSet.next()) {
                    vbox.getChildren().add(new Label(secondsToStringWithHour(resultSet.getInt("sum"))));
                    mainVBox.getChildren().addAll(lengthTitleLabel, vbox);
                }
            }


        } catch (SQLException ex) {
            System.out.println("##### Ошибка вычисления длительности альбома");
        }
    }

    private void addGenresToVBox(VBox mainVBox) {
        Label genreTitleLabel = new Label("Жанры");
        genreTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(0, 0, 0, 15));
        try {
            String query = "SELECT Genres.name "
                    + "FROM Albums INNER JOIN "
                    + "(Genres INNER JOIN albums_has_genres "
                    + "ON Genres.idgenre=albums_has_genres.genres_idgenre) "
                    + "ON Albums.idalbum=albums_has_genres.albums_idalbum "
                    + "WHERE (albums_has_genres.albums_idalbum=Albums.idalbum "
                    + "And albums_has_genres.genres_idgenre=Genres.idgenre "
                    + "And Albums.idalbum = ?) ORDER BY Genres.name;";

            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                if (!resultSet.getString("Genres.name").equals("")) {
                    vbox.getChildren().add(new Label(resultSet.getString("Genres.name")));
                }

                if (resultSet.isLast()) {
                    mainVBox.getChildren().addAll(genreTitleLabel, vbox);
                }
            }


        } catch (SQLException ex) {
            System.out.println("##### Ошибка добавления жанров в Left");
        }
    }

    private void addTypeToVBox(VBox mainVBox) {
        Label typeTitleLabel = new Label("Тип релиза");
        typeTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 14));

        VBox vbox = new VBox(5);
        vbox.setPadding(new Insets(0, 0, 0, 15));
        vbox.getChildren().add(typeTitleLabel);

        try {
            String query = "SELECT aType FROM Albums WHERE idalbum = ?;";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();

            if (!resultSet.wasNull()) {
                while (resultSet.next()) {
                    if (!resultSet.getString("aType").equals("Unknown")) {
                        vbox.getChildren().add(new Label(resultSet.getString("aType")));
                        mainVBox.getChildren().addAll(typeTitleLabel, vbox);
                    }
                }
            }

        } catch (SQLException ex) {
            System.out.println("##### Ошибка добавления типа в Left");
        }
    }

    private void addLabelToVBox(VBox mainVBox) {
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

            if (!resultSet.wasNull()) {
                while (resultSet.next()) {
                    if (!resultSet.getString("Labels.name").equals("N/A")) {
                        vbox.getChildren().add(new Label(resultSet.getString("Labels.name")));
                        mainVBox.getChildren().addAll(typeTitleLabel, vbox);
                    }
                }
            }

        } catch (SQLException ex) {
            System.out.println("##### Ошибка добавления типа в Left");
        }
    }

    private boolean isFavourite() {
        boolean b = false;
        try {
            String query = "SELECT fav FROM albums WHERE idalbum = ?";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            if (!resultSet.wasNull()) {
                resultSet.next();
                if (resultSet.getInt("fav") == 0) {
                    b = false;
                } else {
                    b = true;
                }
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

        /*final TextArea textArea = new TextArea();
         textArea.setPrefColumnCount(1);
         textArea.setPrefHeight(150);
         textArea.setWrapText(true);
         textArea.setStyle("-fx-background-color: transparent, -fx-text-box-border, -fx-control-inner-background; "
         + "-fx-background-insets: -1.4, 0, 1; -fx-background-radius: 1.4, 0, 0;"
         + "-fx-padding: 1;");
         */

        Label infoTitleLabel = new Label("Information");
        infoTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 18));

        Label discographyTitleLabel = new Label("Tracklist");
        discographyTitleLabel.setFont(Font.font(null, FontWeight.BOLD, 18));

        VBox infoVBox = new VBox(5);
        infoVBox.setPadding(new Insets(10));
        Label totalDurationLabel1 = null;

        try {
            String query = "SELECT SUM(sLength) as sum FROM Songs WHERE album_idalbum = "
                    + "(SELECT Albums.idalbum FROM Albums WHERE idalbum = ?);";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            if (!resultSet.wasNull()) {
                resultSet.next();
                totalDurationLabel1 = new Label("Общее время звучания: "
                        + secondsToStringWithHour(resultSet.getInt("sum")));
            }
        } catch (SQLException ex) {
            System.out.println("##### Ошибка вычисления длительности альбома");
        }

        infoVBox.getChildren().addAll(totalDurationLabel1);

        vbox1.getChildren().addAll(infoTitleLabel, infoVBox,
                new Separator(Orientation.HORIZONTAL), discographyTitleLabel, addTracklist());
        //------------------------

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vbox1);

        return scrollPane;
    }

    private VBox addTracklist() {
        VBox vbox = new VBox(5);

        try {
            String query = "SELECT cd FROM Albums WHERE idalbum = ?;";

            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            if (!resultSet.wasNull()) {
                resultSet.next();
                vbox.getChildren().add(new Label("CD " + resultSet.getString("cd")));
            }
        } catch (SQLException e) {
            System.out.println("##### Ошибка добавления номера cd");
        }

        try {
            String query = "SELECT num, name, sLength FROM Songs WHERE album_idalbum = "
                    + "(SELECT Albums.idalbum FROM Albums WHERE idalbum = ?);";

            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            if (!resultSet.wasNull()) {
                while (resultSet.next()) {
                    vbox.getChildren().add(new Label(resultSet.getInt("num") + ". "
                            + resultSet.getString("name")
                            + " (" + secondsToStringWithoutHour(resultSet.getInt("sLength")) + ")"));
                }
            }
        } catch (SQLException ex) {
            System.out.println("##### Ошибка добавления треклиста в Center");
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
