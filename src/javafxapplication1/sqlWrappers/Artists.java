package javafxapplication1.sqlWrappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafxapplication1.SqlWorker;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class Artists {

    private boolean isAdded;
    private SqlWorker sqlWorker;
    private ResultSet resultSet;
    private int id;
    private String bio;
    private Image img;
    private String rateLink;
    private String disqLink;
    private String lastLink;
    private Tag tag;
    private boolean VA = false;
    private boolean split = false;
    private HashSet<String> artistSet;

    public Artists(SqlWorker sqlWorker, Tag tag) {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        this.sqlWorker = sqlWorker;
        this.tag = tag;
    }

    public void addFromTag() throws SQLException {
        try {
            String namesCount = getCountByName(tag.getFirst(FieldKey.ARTIST));
            if (namesCount.equals("0")) {
                id = findEmptyIndex();
                if (id == 0) {
                    id = 1;
                }
                try {
                    String query = "INSERT INTO artists (idartist, name, origin, bio, img, thumb, "
                            + "rateLink, disqLink, lastLink, fav, from ,to, aka) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    System.out.println(query);
                    PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
                    stmt.setInt(1, id);
                    if (isVA()) {
                        stmt.setString(2, "Various Artists");
                    } else if (split) {
                        String artistString = "";
                        int i = 0;
                        for (String artist : artistSet){
                            i++;
                            if (i%2 == 0) {
                                artistString += " with ";
                            }
                            artistString += artist;
                        }
                        stmt.setString(2, artistString);
                    } else {
                        stmt.setString(2, tag.getFirst(FieldKey.ARTIST));
                    }
                    stmt.setString(3, "N/A");
                    stmt.setNull(4, java.sql.Types.NULL);
                    stmt.setNull(5, java.sql.Types.NULL);
                    stmt.setNull(6, java.sql.Types.NULL);
                    stmt.setNull(7, java.sql.Types.NULL);
                    stmt.setNull(8, java.sql.Types.NULL);
                    stmt.setNull(9, java.sql.Types.NULL);
                    stmt.setInt(10, 0);
                    stmt.setInt(11, java.sql.Types.NULL);
                    stmt.setInt(12, java.sql.Types.NULL);
                    stmt.setInt(13, java.sql.Types.NULL);
                    stmt.executeUpdate();
                    isAdded = true;
                } catch (SQLException e) {
                    System.out.println("##### Ошибка вставки исполнителя!");
                }
                System.out.println("Artist %" + tag.getFirst(FieldKey.ARTIST) + "% added");
            } else {
                isAdded = false;
            }
        } catch (NullPointerException e) {
            System.out.println("!!!!! У исполнителя не заполнен тег");
        }
    }

    public String getCountByName(String name) throws SQLException {
        PreparedStatement stmt = sqlWorker.getConnection().
                prepareStatement("SELECT COUNT(*) AS aCount FROM artists WHERE name = ?");
        stmt.setString(1, name);
        resultSet = stmt.executeQuery();
        resultSet.next();
        return resultSet.getString("aCount");
    }

    public String getMaxIndex() throws SQLException {
        resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount "
                + "FROM artists;");
        resultSet.next();
        return resultSet.getString("aCount");
    }

    public String getName(String name) throws SQLException {
        PreparedStatement stmt = sqlWorker.getConnection().
                prepareStatement("SELECT DISTINCT name FROM artists WHERE name = ?");
        stmt.setString(1, name);
        resultSet = stmt.executeQuery();
        resultSet.next();
        return resultSet.getString("name");
    }

    public String getOrigin(String name) throws SQLException {
        PreparedStatement stmt = sqlWorker.getConnection().
                prepareStatement("SELECT origin FROM artists WHERE name = ?");
        stmt.setString(1, name);
        resultSet = stmt.executeQuery();
        resultSet.next();
        return resultSet.getString("origin");
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Image getImage() {
        return img;
    }

    public void setImage(Image img) {
        this.img = img;
    }

    public String getRateLink() {
        return rateLink;
    }

    public void setRateLink(String rateLink) {
        this.rateLink = rateLink;
    }

    public String getDisqLink() {
        return disqLink;
    }

    public void setDisqLink(String disqLink) {
        this.disqLink = disqLink;
    }

    public String getLastLink() {
        return lastLink;
    }

    public void setLastLink(String lastLink) {
        this.lastLink = lastLink;
    }

    public int getId(String name) throws SQLException {
        PreparedStatement stmt = sqlWorker.getConnection().
                prepareStatement("SELECT idartist FROM artists WHERE name = ?");
        stmt.setString(1, name);
        resultSet = stmt.executeQuery();
        resultSet.next();
        if (resultSet.getString("idartist").equals("")) {
            return 0;
        } else {
            return Integer.parseInt(resultSet.getString("idartist"));
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAdded() {
        return isAdded;
    }

    private int findEmptyIndex() throws SQLException {
        int index = 0;
        int lastIndex = 0;
        resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount FROM artists;");
        resultSet.next();

        int cnt = Integer.parseInt(resultSet.getString("aCount"));

        resultSet = sqlWorker.getStat().executeQuery("SELECT idartist FROM artists ORDER BY idartist DESC LIMIT 1;");
        resultSet.next();

        if (cnt > 0) {
            lastIndex = Integer.parseInt(resultSet.getString("idartist"));
        } else {
            lastIndex = 0;
        }

        if (cnt < lastIndex) {
            resultSet = sqlWorker.getStat().executeQuery("SELECT idartist FROM artists ORDER BY idartist;");
            int count = 0;

            while (resultSet.next()) {
                count++;
                System.out.println("id: " + resultSet.getInt("idartist"));
                if (resultSet.getInt("idartist") > count) {
                    index = count;
                    break;
                } else {
                    index = resultSet.getInt("idartist") + 1;
                }
            }
        } else if (cnt == lastIndex) {
            index = lastIndex + 1;
        }
        return index;
    }

    public void updateImagePath(String imgPath) throws SQLException {
        String query = "UPDATE artists SET img = ? WHERE idartist = ?";
        PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
        stmt.setString(1, imgPath);
        stmt.setInt(2, getId(tag.getFirst(FieldKey.ARTIST)));
        stmt.executeUpdate();
    }

    public boolean isVA() {
        return VA;
    }

    public void setVA(boolean VA) {
        this.VA = VA;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public HashSet<String> getArtistSet() {
        return artistSet;
    }

    public void setArtistSet(HashSet<String> artistSet) {
        this.artistSet = artistSet;
    }
}
