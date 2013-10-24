package javafxapplication1;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseEvent;
import javafxapplication1.gui.AlbumContentPane;
import javafxapplication1.gui.ArtistContentPane;

public class CustomHyperlink extends Hyperlink {

    private int id;
    private int flag;
    private String name;
    private Tab tab;
    private SqlWorker sqlWorker;
    private ResultSet resultSet;

    // 1 - исполнитель
    // 2 - альбом
    // 3 - год
    // 4 - жанр
    // 5 - издатель
    public CustomHyperlink(SqlWorker sqlWorker, String title, String name, int id, int flag, Tab tab) {
        super(title);
        setStyle("-fx-text-fill: #000000; -fx-border-width: 0;");
        this.flag = flag;
        this.id = id;
        this.name = name;
        this.sqlWorker = sqlWorker;
        this.tab = tab;
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                switch (CustomHyperlink.this.flag) {
                    case 1: {
                        fireArtistContentPane();
                    } break;
                    case 2: {
                        fireAlbumContentPane();
                    } break;
                }
            }
        });
    }

    private void fireArtistContentPane() {
        try {
            String query = "SELECT Artists.idartist, Artists.name FROM Albums INNER JOIN \n"
                    + "	(Artists INNER JOIN artist_has_albums \n"
                    + "	ON Artists.idartist = artist_has_albums.artist_idartist) \n"
                    + "	ON Albums.idalbum = artist_has_albums.album_idalbum \n"
                    + "	WHERE (artist_has_albums.album_idalbum = Albums.idalbum \n"
                    + "	AND artist_has_albums.artist_idartist = Artists.idArtist \n"
                    + "	AND Albums.idalbum = ?);";

            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            resultSet.next();

            ArtistContentPane artistContentPane =
                    new ArtistContentPane(resultSet.getString("Artists.name"), resultSet.getInt("Artists.idartist"), sqlWorker, tab);
            tab.setContent(artistContentPane);
            tab.setText(resultSet.getString("Artists.name"));
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void fireAlbumContentPane() {
        try {
            String query = "SELECT Albums.name, Albums.aYear, Albums.idalbum FROM Artists INNER JOIN"
                    + "(Albums INNER JOIN artist_has_albums"
                    + " ON Albums.idalbum = artist_has_albums.album_idalbum)"
                    + " ON Artists.idartist = artist_has_albums.artist_idartist"
                    + " WHERE (artist_has_albums.album_idalbum=Albums.idalbum"
                    + " AND artist_has_albums.artist_idartist=Artists.idArtist"
                    + " AND Artists.idartist = ? "
                    + " AND Albums.name = ?);";

            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, id);
            stmt.setString(2, name);
            resultSet = stmt.executeQuery();
            resultSet.next();

            AlbumContentPane albumContentPane =
                    new AlbumContentPane(resultSet.getString("Albums.name"), resultSet.getInt("Albums.idalbum"), sqlWorker, tab);
            tab.setContent(albumContentPane);
            tab.setText(resultSet.getString("Albums.name"));
        } catch (MalformedURLException ex) {
        } catch (IOException ex) {
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
