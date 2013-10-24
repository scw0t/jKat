package javafxapplication1;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafxapplication1.gui.AlbumContentPane;
import javafxapplication1.gui.ArtistContentPane;

public class ListProducer {

    private SqlWorker sqlWorker;
    private TabPane tabPane;
    private ObservableList<GroupBox> albumObserverList;
    private ObservableList<GroupBox> artistObserverList;
    private ListView<GroupBox> artistListView;
    private ListView<GroupBox> albumsListView;
    private AlbumContentPane albumContentPane;
    private ArtistContentPane artistContentPane;
    private Tab tab = null;

    public ListProducer(TabPane tabPane, SqlWorker sqlWorker) {
        this.sqlWorker = sqlWorker;
        this.tabPane = tabPane;
        this.tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
    }

    private void fillArtistList() {
        ArrayList<GroupBox> arrayCollection = new ArrayList();
        try {
            ResultSet resultSet = sqlWorker.getStat().executeQuery("SELECT Artists.img, Artists.name, Artists.origin, "
                    + "Artists.idartist "
                    + "FROM artists ORDER BY name;");
            while (resultSet.next()) {
                File fileImg = new File(resultSet.getString("Artists.img"));
                Image img = new Image(fileImg.toURI().toString(), 50, 50, false, true);
                GroupBox groupBox = new GroupBox();
                groupBox.initArtistBox(img, resultSet.getString("Artists.name"),
                        resultSet.getString("Artists.origin"), resultSet.getInt("idartist"));
                groupBox.setFlag(1);
                arrayCollection.add(groupBox);
            }
            resultSet.close();
        } catch (SQLException ex) {
            System.out.println("SQL-ошибка заполнения artistListView");
        } finally {
            artistObserverList = FXCollections.observableArrayList(arrayCollection);
            artistListView.setItems(artistObserverList);
        }
    }

    private void fillAlbumList() {
        ArrayList<GroupBox> arrayCollection = new ArrayList();
        String query = "SELECT Artists.name as artistName, Albums.name as albumName, Albums.aYear as Year, "
                + "Albums.aType as Type, Albums.thumb as Thumb, Albums.idalbum"
                + " FROM Albums INNER JOIN"
                + " (Artists INNER JOIN artist_has_albums"
                + " ON Artists.idartist=artist_has_albums.artist_idartist)"
                + " ON Albums.idalbum=artist_has_albums.album_idalbum"
                + " WHERE (artist_has_albums.album_idalbum=Albums.idalbum"
                + " And artist_has_albums.artist_idartist=Artists.idartist) ORDER BY albumName;";
        try {
            ResultSet resultSet = sqlWorker.getStat().executeQuery(query);
            while (resultSet.next()) {
                File fileImg = null;
                Image img = null;
                if (resultSet.getString("Thumb") != null) {
                    fileImg = new File(resultSet.getString("Thumb"));
                    img = new Image(fileImg.toURI().toString());
                } else {
                    fileImg = new File("temp.png");
                    img = new Image(fileImg.toURI().toString(), 50, 50, false, true);
                }

                GroupBox groupBox = new GroupBox();
                groupBox.initAlbumBox(img, resultSet.getString("albumName"), resultSet.getString("Year"),
                        resultSet.getString("Type"), resultSet.getString("artistName"), resultSet.getInt("idalbum"));
                groupBox.setFlag(2);
                arrayCollection.add(groupBox);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("SQL-ошибка заполнения albumListView");
        } finally {
            albumObserverList = FXCollections.observableArrayList(arrayCollection);
            albumsListView.setItems(albumObserverList);
        }
    }

    public void init() {
        fillArtistList();
        fillAlbumList();
    }

    public ListView<GroupBox> getArtistListView() {
        return artistListView;
    }

    public void setArtistListView(ListView<GroupBox> artistListView) {
        this.artistListView = artistListView;
        this.artistListView.setCellFactory(new Callback<ListView<GroupBox>, ListCell<GroupBox>>() {
            @Override
            public ListCell<GroupBox> call(ListView<GroupBox> p) {
                return new CustomCellFactory();
            }
        });
    }

    public ListView<GroupBox> getAlbumsListView() {
        return albumsListView;
    }

    public void setAlbumsListView(ListView<GroupBox> albumsListView) {
        this.albumsListView = albumsListView;
        this.albumsListView.setCellFactory(new Callback<ListView<GroupBox>, ListCell<GroupBox>>() {
            @Override
            public ListCell<GroupBox> call(ListView<GroupBox> p) {
                return new CustomCellFactory();
            }
        });
    }

    public class CustomCellFactory extends ListCell<GroupBox> {

        private GroupBox groupBox;

        public CustomCellFactory() {
            this.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        if (event.getClickCount() == 2) {
                            try {
                                if (groupBox.getFlag() == 1) {
                                    if (tab == null) {
                                        tab = new Tab();
                                        tab.setOnClosed(new EventHandler<Event>() {
                                            @Override
                                            public void handle(Event t) {
                                                tab = null;
                                            }
                                        });
                                        artistContentPane = new ArtistContentPane(groupBox.getAlbumNameLabel(),
                                                groupBox.getMyId(), sqlWorker, tab);
                                        tab.setContent(artistContentPane);
                                        tab.setText(groupBox.getArtistNameLabel());
                                        tabPane.getTabs().add(tab);
                                    } else {
                                        artistContentPane = new ArtistContentPane(groupBox.getAlbumNameLabel(),
                                                groupBox.getMyId(), sqlWorker, tab);
                                        tab.setContent(artistContentPane);
                                        tab.setText(groupBox.getArtistNameLabel());
                                    }

                                    tabPane.getSelectionModel().select(tab);
                                } else if (groupBox.getFlag() == 2) { // альбом
                                    if (tab == null) {
                                        tab = new Tab();
                                        tab.setOnClosed(new EventHandler<Event>() {
                                            @Override
                                            public void handle(Event t) {
                                                tab = null;
                                            }
                                        });
                                        albumContentPane = new AlbumContentPane(groupBox.getAlbumNameLabel(),
                                                groupBox.getMyId(), sqlWorker, tab);
                                        tab.setContent(albumContentPane);
                                        tab.setText(groupBox.getAlbumNameLabel());
                                        tabPane.getTabs().add(tab);
                                    } else {
                                        albumContentPane = new AlbumContentPane(groupBox.getAlbumNameLabel(),
                                                groupBox.getMyId(), sqlWorker, tab);
                                        tab.setContent(albumContentPane);
                                        tab.setText(groupBox.getAlbumNameLabel());
                                    }
                                    tabPane.getSelectionModel().select(tab);
                                }
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (MalformedURLException ex) {
                                Logger.getLogger(ListProducer.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(ListProducer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                    if (event.getButton().equals(MouseButton.SECONDARY)) {
                        try {
                            if (!groupBox.equals(null)) {
                                MenuItem menuItem = new MenuItem("Delete");
                                menuItem.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent t) {
                                        if (groupBox.getFlag() == 1) {
                                            artistObserverList.remove(CustomCellFactory.this.getIndex());
                                            // TODO: Add artist delete
                                        } else if (groupBox.getFlag() == 2) {
                                            deleteAlbum(groupBox);
                                            albumObserverList.remove(CustomCellFactory.this.getIndex());
                                        }
                                    }
                                });
                                ContextMenu contextMenu = new ContextMenu();
                                contextMenu.getItems().add(menuItem);
                                contextMenu.show(groupBox, event.getScreenX(), event.getScreenY());
                            }
                        } catch (NullPointerException e) {
                        }
                    }
                }
            });
        }
 
        @Override
        protected void updateItem(GroupBox gb, boolean b) {
            super.updateItem(gb, b);
            setGraphic(gb);
            groupBox = gb;
        }
    }

    private void deleteAlbum(GroupBox groupBox) {
        String query = "DELETE FROM Albums WHERE idalbum = ?;";
        try {
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, groupBox.getMyId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("##### Не удалось удалить альбом");
        }
    }
}
