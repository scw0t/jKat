package javafxapplication1.sqlWrappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafxapplication1.SqlWorker;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class Albums {

    private SqlWorker sqlWorker;
    private ResultSet resultSet;
    private boolean isAdded;
    private int id;
    private int idLabel;
    private String name;
    private String aYear;
    private String aType;
    private String aLength;
    private String info;
    private String rateLink;
    private String disqLink;
    private String lastLink;
    private String imgPath;
    private int labels_idlabels;
    private Tag tag;
    private boolean VA = false;
    private String cd;

    public Albums(SqlWorker sqlWorker, int idLabel, Tag tag) {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        this.sqlWorker = sqlWorker;
        this.idLabel = idLabel;
        this.tag = tag;
    }

    public void addFromTag() throws SQLException {
        String namesCount = getCountByName(tag.getFirst(FieldKey.ALBUM));
        if (namesCount.equals("0")) {
            id = findEmptyIndex();
            if (id == 0) {
                id = 1;
            }
            try {
                System.out.println(tag.getFirst(FieldKey.YEAR).toString());
                String query = "INSERT INTO albums (idalbum, name, aYear, aType, aLength, info, img, thumb, "
                        + "rateLink, disqLink, lastLink, fav, isVA, labels_idlabel) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                System.out.println(query);
                PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
                stmt.setInt(1, id);
                stmt.setString(2, tag.getFirst(FieldKey.ALBUM));
                stmt.setString(3, tag.getFirst(FieldKey.YEAR));
                stmt.setString(4, "Unknown");
                stmt.setString(5, "0");
                stmt.setNull(6, java.sql.Types.NULL);
                stmt.setNull(7, java.sql.Types.NULL);
                stmt.setNull(8, java.sql.Types.NULL);
                stmt.setNull(9, java.sql.Types.NULL);
                stmt.setNull(10, java.sql.Types.NULL);
                stmt.setNull(11, java.sql.Types.NULL);
                stmt.setInt(12, 0);
                if (isVA()) {
                    stmt.setInt(13, 1);
                } else {
                    stmt.setInt(13, 0);
                }
                stmt.setInt(14, idLabel);
                stmt.executeUpdate();
                isAdded = true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("##### Ошибка вставки альбома!");
            }
            System.out.println(tag.getFirst(FieldKey.ALBUM) + " added");
        } else {
            isAdded = false;
        }
    }

    public String getCountByName(String name) throws SQLException {
        try {
            PreparedStatement stmt = sqlWorker.getConnection().
                    prepareStatement("SELECT COUNT(*) AS aCount FROM albums WHERE name = ?");
            stmt.setString(1, name);
            resultSet = stmt.executeQuery();
            resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet.getString("aCount");
    }

    public String getMaxIndex() throws SQLException {
        resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount "
                + "FROM albums;");
        resultSet.next();
        return resultSet.getString("aCount");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getId(String name) throws SQLException {
        PreparedStatement stmt = sqlWorker.getConnection().
                prepareStatement("SELECT idalbum FROM albums WHERE name = ?");
        stmt.setString(1, name);
        resultSet = stmt.executeQuery();
        resultSet.next();
        if (resultSet.getString("idalbum").equals("")) {
            return 0;
        } else {
            return Integer.parseInt(resultSet.getString("idalbum"));
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getaYear() {
        return aYear;
    }

    public void setaYear(String aYear) {
        this.aYear = aYear;
    }

    public String getaType() {
        return aType;
    }

    public void setaType(String aType) {
        this.aType = aType;
    }

    public String getaLength() {
        return aLength;
    }

    public void setaLength(String aLength) {
        this.aLength = aLength;
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

    public int getLabels_idlabels() {
        return labels_idlabels;
    }

    public void setLabels_idlabels(int labels_idlabels) {
        this.labels_idlabels = labels_idlabels;
    }

    public boolean isAdded() {
        return isAdded;
    }

    private int findEmptyIndex() throws SQLException {
        int index = 0;
        int lastIndex = 0;
        resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount FROM albums;");
        resultSet.next();

        int cnt = Integer.parseInt(resultSet.getString("aCount"));

        resultSet = sqlWorker.getStat().executeQuery("SELECT idalbum FROM albums ORDER BY idalbum DESC LIMIT 1;");
        resultSet.next();

        if (cnt > 0) {
            lastIndex = Integer.parseInt(resultSet.getString("idalbum"));
        } else {
            lastIndex = 0;
        }

        if (cnt < lastIndex) {
            resultSet = sqlWorker.getStat().executeQuery("SELECT idalbum FROM albums ORDER BY idalbum;");
            int count = 0;

            while (resultSet.next()) {
                count++;
                System.out.println("id: " + resultSet.getInt("idalbum"));
                if (resultSet.getInt("idalbum") > count) {
                    index = count;
                    break;
                } else {
                    index = resultSet.getInt("idalbum") + 1;
                }
            }
        } else if (cnt == lastIndex) {
            index = lastIndex + 1;
        }

        return index;
    }

    public String getImagePath() {
        return imgPath;
    }

    public void updateImagePath(String imgPath) {
        try {
            String query = "UPDATE albums SET img = ? WHERE name = ? AND aYear = ?";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setString(1, imgPath);
            stmt.setString(2, tag.getFirst(FieldKey.ALBUM));
            stmt.setString(3, tag.getFirst(FieldKey.YEAR));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateThumbsPath(String thumbsPath) {
        try {
            String query = "UPDATE albums SET thumb = ? WHERE name = ? AND aYear = ?";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setString(1, thumbsPath);
            stmt.setString(2, tag.getFirst(FieldKey.ALBUM));
            stmt.setString(3, tag.getFirst(FieldKey.YEAR));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isVA() {
        return VA;
    }

    public void setVA(boolean VA) {
        this.VA = VA;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    
}
