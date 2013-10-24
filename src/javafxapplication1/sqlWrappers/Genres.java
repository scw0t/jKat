package javafxapplication1.sqlWrappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafxapplication1.SqlWorker;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class Genres {

    private SqlWorker sqlWorker;
    private ResultSet resultSet;
    private int id;
    private String name;
    private String info;
    private String genres[];

    public Genres(SqlWorker sqlWorker) {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        this.sqlWorker = sqlWorker;
    }

    public void addFromTag(Tag tag) throws SQLException {
        if (tag.getFirst(FieldKey.GENRE).contains("; ")) {
            genres = tag.getFirst(FieldKey.GENRE).split("; ");
        } else {
            genres = tag.getFirst(FieldKey.GENRE).split(", ");
        } 

        for (int i = 0; i < genres.length; i++) {
            if (getCountByName(genres[i]).equals("0")) {
                id = findEmptyIndex();
                if (id == 0) {
                    id = 1;
                }
                try {
                    String query = "INSERT INTO genres (idgenre, name, info) "
                            + "VALUES (?, ?, ?)";
                    System.out.println(query);
                    PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
                    stmt.setInt(1, id);
                    stmt.setString(2, genres[i]);
                    stmt.setNull(3, java.sql.Types.NULL);
                    stmt.executeUpdate();
                    System.out.println("Genre %" + genres[i] + "% added");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("##### Ошибка вставки жанра!");
                }
            }
        }
    }

    public String getCountByName(String name) throws SQLException {
        PreparedStatement stmt = sqlWorker.getConnection().
                prepareStatement("SELECT COUNT(*) AS aCount FROM genres WHERE name = ?");
        stmt.setString(1, name);
        resultSet = stmt.executeQuery();
        resultSet.next();
        return resultSet.getString("aCount");
    }

    public String getMaxIndex() throws SQLException {
        resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount "
                + "FROM genres;");
        resultSet.next();
        return resultSet.getString("aCount");
    }

    public int getIdgenres() {
        return id;
    }

    public void setIdgenres(int id) {
        this.id = id;
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

    public ArrayList<Integer> getGenres() throws SQLException {
        ArrayList<Integer> arr = new ArrayList<>();
        for (int i = 0; i < genres.length; i++) {
            PreparedStatement stmt = sqlWorker.getConnection().
                    prepareStatement("SELECT idgenre FROM genres WHERE name = ?");
            stmt.setString(1, genres[i]);
            resultSet = stmt.executeQuery();
            resultSet.next();
            if (!resultSet.getString("idgenre").equals("")) {
                arr.add(Integer.parseInt(resultSet.getString("idgenre")));
            }
        }
        return arr;
    }

    private int findEmptyIndex() throws SQLException {
        int index = 0;
        int lastIndex = 0;
        
        resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount FROM genres;");
        resultSet.next();
        int cnt = Integer.parseInt(resultSet.getString("aCount"));
        resultSet = sqlWorker.getStat().executeQuery("SELECT idgenre FROM genres ORDER BY idgenre DESC LIMIT 1;");
        resultSet.next();

        if (cnt > 0) {
            lastIndex = Integer.parseInt(resultSet.getString("idgenre"));
        } else {
            lastIndex = 0;
        }

        if (cnt < lastIndex) {
            resultSet = sqlWorker.getStat().executeQuery("SELECT idgenre FROM genres ORDER BY idgenre;");
            int count = 0;

            while (resultSet.next()) {
                count++;
                System.out.println("id: " + resultSet.getInt("idgenre"));
                if (resultSet.getInt("idgenre") > count) {
                    index = count;
                    break;
                } else {
                    index = resultSet.getInt("idgenre") + 1;
                }
            }
        } else if (cnt == lastIndex) {
            index = lastIndex + 1;
        }
        return index;
    }
}
