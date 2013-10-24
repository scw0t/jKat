package javafxapplication1.sqlWrappers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafxapplication1.SqlWorker;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class Songs {

    private SqlWorker sqlWorker;
    private ResultSet resultSet;
    private int idsong;
    private String num;
    private String name;
    private String sSize;
    private String sLangth;
    private String sPath;
    private String info;
    private int album_idalbum;
    private int id;
    private MP3AudioHeader audioHeader;
    private MP3File mp3F;
    private int idAlbum;
    private String cd = "";

    public Songs(SqlWorker sqlWorker, MP3File mp3F, int idAlbum) {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        this.sqlWorker = sqlWorker;
        this.idAlbum = idAlbum;
        this.mp3F = mp3F;
    }

    public void addFromTag(Tag tag) throws SQLException, IOException {
        name = tag.getFirst(FieldKey.TITLE);
        String namesCount = getCountByName(name);
        if (namesCount.equals("0") && !name.equals("")) {
            id = findEmptyIndex();
            if (id == 0) {
                id = 1;
            }
            try {
                String query = "INSERT INTO songs (idsong, num, name, sSize, "
                        + "sLength, sPath, info, cd, album_idalbum) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                System.out.println(query);
                PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
                stmt.setInt(1, id);
                stmt.setString(2, tag.getFirst(FieldKey.TRACK));
                stmt.setString(3, name);
                stmt.setInt(4, 0);
                stmt.setInt(5, mp3F.getMP3AudioHeader().getTrackLength());
                stmt.setString(6, mp3F.getFile().getCanonicalPath());
                stmt.setNull(7, java.sql.Types.NULL);
                stmt.setString(8, cd);
                stmt.setInt(9, idAlbum);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("##### Ошибка вставки трека!");
            }
            System.out.println("Song %" + tag.getFirst(FieldKey.TITLE) + "% added");
        } else {
        }
    }

    public String getCountByName(String name) throws SQLException {
        String query = "SELECT Artists.name FROM Albums INNER JOIN \n"
                + "	(Artists INNER JOIN artist_has_albums \n"
                + "	ON Artists.idartist = artist_has_albums.artist_idartist) \n"
                + "	ON Albums.idalbum = artist_has_albums.album_idalbum \n"
                + "	WHERE (artist_has_albums.album_idalbum = Albums.idalbum \n"
                + "	AND artist_has_albums.artist_idartist = Artists.idArtist \n"
                + "	AND Albums.idalbum = ?);";


        PreparedStatement stmt = sqlWorker.getConnection().
                prepareStatement("SELECT COUNT(*) AS aCount FROM songs WHERE name = ? "
                + "AND album_idalbum = ?");
        stmt.setString(1, name);
        stmt.setInt(2, idAlbum);
        resultSet = stmt.executeQuery();
        resultSet.next();
        return resultSet.getString("aCount");
    }

    public String getMaxIndex() throws SQLException {
        resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount "
                + "FROM songs;");
        resultSet.next();
        return resultSet.getString("aCount");
    }

    public int getId() throws SQLException {
        PreparedStatement stmt = sqlWorker.getConnection().
                prepareStatement("SELECT idsong FROM songs WHERE name = ?");
        stmt.setString(1, name);
        resultSet = stmt.executeQuery();
        resultSet.next();
        if (resultSet.getString("idsong").equals("")) {
            return 0;
        } else {
            return Integer.parseInt(resultSet.getString("idsong"));
        }
    }

    public int getIdsong() {
        return idsong;
    }

    public void setIdsong(int idsong) {
        this.idsong = idsong;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getsSize() {
        return sSize;
    }

    public void setsSize(String sSize) {
        this.sSize = sSize;
    }

    public String getsLangth() {
        return sLangth;
    }

    public void setsLangth(String sLangth) {
        this.sLangth = sLangth;
    }

    public String getsPath() {
        return sPath;
    }

    public void setsPath(String sPath) {
        this.sPath = sPath;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getAlbum_idalbum() {
        return album_idalbum;
    }

    public void setAlbum_idalbum(int album_idalbum) {
        this.album_idalbum = album_idalbum;
    }

    private int findEmptyIndex() throws SQLException {
        int index = 0;
        int lastIndex = 0;
        resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount FROM songs;");
        resultSet.next();
        int cnt = Integer.parseInt(resultSet.getString("aCount"));
        resultSet = sqlWorker.getStat().executeQuery("SELECT idsong FROM songs ORDER BY idsong DESC LIMIT 1;");
        resultSet.next();

        if (cnt > 0) {
            lastIndex = Integer.parseInt(resultSet.getString("idsong"));
        } else {
            lastIndex = 0;
        }

        if (cnt < lastIndex) {
            resultSet = sqlWorker.getStat().executeQuery("SELECT idsong FROM songs ORDER BY idsong;");
            int count = 0;

            while (resultSet.next()) {
                count++;
                if (resultSet.getInt("idsong") > count) {
                    index = count;
                    break;
                } else {
                    index = resultSet.getInt("idsong") + 1;
                }
            }
        } else if (cnt == lastIndex) {
            index = lastIndex + 1;
        }
        return index;
    }

    public MP3AudioHeader getAudioHeader() {
        return audioHeader;
    }

    public void setAudioHeader(MP3AudioHeader audioHeader) {
        this.audioHeader = audioHeader;
    }

    public MP3File getMp3F() {
        return mp3F;
    }

    public void setMp3F(MP3File mp3F) {
        this.mp3F = mp3F;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }
}
