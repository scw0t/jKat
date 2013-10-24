package javafxapplication1;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafxapplication1.sqlWrappers.Albums;
import javafxapplication1.sqlWrappers.Artists;
import javafxapplication1.sqlWrappers.Genres;
import javafxapplication1.sqlWrappers.Labels;
import javafxapplication1.sqlWrappers.Songs;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

public class Aggregator {
    
    private SqlWorker sqlWorker;
    private File[] fileArray;
    private File folder;
    private int typeFlag; // 1 - album; 2 - split; >2 - VA
    private HashSet<String> artistSet;
    private HashSet<String> albumSet;
    private HashSet<String> yearSet;
    private ArrayList<String> nameSet;
    private File defaultPersonPicture = new File("person.png");
    private Albums album;
    private String cd = "0";

    public Aggregator() {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
    }

    public Aggregator(SqlWorker sqlWorker, File[] fileArray, File folder) {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        this.fileArray = fileArray;
        this.folder = folder;
        this.sqlWorker = sqlWorker;
    }

    public Aggregator(SqlWorker sqlWorker, File[] fileArray) {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        this.fileArray = fileArray;
        this.sqlWorker = sqlWorker;
        this.folder = new File("temp.png");
    }

    public void process() throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException {
        artistSet = new HashSet<>();
        albumSet = new HashSet<>();
        yearSet = new HashSet<>();
        nameSet = new ArrayList<>();

        for (File file : fileArray) {
            MP3File mp3file = (MP3File) AudioFileIO.read(file);
            Tag tag = mp3file.getTag();

            if (!tag.getFirst(FieldKey.YEAR).isEmpty()) {
                yearSet.add(tag.getFirst(FieldKey.YEAR));
            }
            if (tag.getFirst(FieldKey.TITLE).isEmpty()) {
                nameSet.add(file.getName());
            } else {
                nameSet.add(tag.getFirst(FieldKey.TITLE));
            }
            if (tag.getFirst(FieldKey.ALBUM).isEmpty()) {
                albumSet.add(file.getParentFile().getName());
            } else {
                albumSet.add(tag.getFirst(FieldKey.ALBUM));
            }
            if (tag.getFirst(FieldKey.ARTIST).isEmpty()) {
                if (file.getParentFile().getParentFile().exists()) {
                    artistSet.add(file.getParentFile().getParentFile().getName());
                } else {
                    artistSet.add(file.getParentFile().getName());
                }
            } else {
                artistSet.add(tag.getFirst(FieldKey.ARTIST));
            }
            
            if (tag.getFirst(FieldKey.DISC_NO).isEmpty()) {
                if (file.getParentFile().getName().toLowerCase().contentEquals("cd")) {
                    
                }
            } else {
                cd = tag.getFirst(FieldKey.DISC_NO);
            }
            
        }

        if (artistSet.size() == 1) {
            setTypeFlag(1);
        } else if (artistSet.size() == 2) {
            setTypeFlag(2);
        } else if (artistSet.size() > 2) {
            setTypeFlag(3);
        }
        
        try {
            parse();
        } catch (SQLException ex) {
            Logger.getLogger(Aggregator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void parse() throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException, SQLException{
        int idLabel = 1;
        int idAlbum = 1;
        int idArtist = 1;
        
        ArrayList<Integer> arrGenres = null;
        
        for (File file : fileArray){
            MP3File mp3file = (MP3File) AudioFileIO.read(file);
            Tag tag = mp3file.getTag();
            
            Artists artist = new Artists(sqlWorker, tag);
            
            if (artistSet.size() == 2) {
                artist.setSplit(true);
                artist.setArtistSet(artistSet);
            } else if (artistSet.size() > 2){
                artist.setVA(true);
            } 
            
            artist.addFromTag();
            
            try {
                idArtist = artist.getId(tag.getFirst(FieldKey.ARTIST));
                artist.updateImagePath(defaultPersonPicture.getAbsolutePath());
            } catch (NullPointerException e) {
                System.out.println("Aggr !!!!! У исполнителя не заполнен тег");
            }
            
            Labels label = new Labels(sqlWorker);
            label.addFromTag(tag);
            idLabel = label.getId();
            
            if (!tag.getFirst(FieldKey.GENRE).isEmpty()) {
                Genres genre = new Genres(sqlWorker);
                genre.addFromTag(tag);
                arrGenres = genre.getGenres();
            }
            
            if (!tag.getFirst(FieldKey.ALBUM).isEmpty()) {
                setAlbum(new Albums(sqlWorker, idLabel, tag));
                if (artistSet.size() > 1) {
                    getAlbum().setVA(true);
                }
                
                getAlbum().addFromTag();
                idAlbum = getAlbum().getId(tag.getFirst(FieldKey.ALBUM));

                if (arrGenres != null) {
                    for (int j = 0; j < arrGenres.size(); j++) {
                        try {
                            String query = "INSERT INTO albums_has_genres (albums_idalbum, genres_idgenre) "
                                    + "VALUES (?, ?)";
                            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
                            stmt.setInt(1, idAlbum);
                            stmt.setInt(2, arrGenres.get(j));
                            stmt.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("##### Ошибка вставки в albums_has_genres. Значения уже существуют");
                        }
                    }
                }

                try {
                    String query = "INSERT INTO artist_has_albums (artist_idartist, album_idalbum) "
                            + "VALUES (?, ?)";
                    PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
                    if (artistSet.size() > 2) {
                        
                    }
                    stmt.setInt(1, idArtist);
                    stmt.setInt(2, idAlbum);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("##### Ошибка вставки в artist_has_albums. Значения уже существуют");
                }
                Songs song = new Songs(sqlWorker, mp3file, idAlbum);
                song.setCd(cd);
                song.addFromTag(tag);
            }
        }
    }

    public String getParent(File file) {
        while (file.getParentFile().exists()) {
            System.out.println(file.getParentFile().getName());
            try {
                getParent(file.getParentFile());
            } catch (NullPointerException e) {
                return "Root";
            }
        }
        return file.getParentFile().getName();
    }

    public HashSet<String> getArtistSet() {
        return artistSet;
    }

    public HashSet<String> getAlbumSet() {
        return albumSet;
    }

    public HashSet<String> getYearSet() {
        return yearSet;
    }

    public ArrayList<String> getNameSet() {
        return nameSet;
    }

    public int getTypeFlag() {
        return typeFlag;
    }

    public void setTypeFlag(int typeFlag) {
        this.typeFlag = typeFlag;
    }

    public Albums getAlbum() {
        return album;
    }

    public void setAlbum(Albums album) {
        this.album = album;
    }
}
