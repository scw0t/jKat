package javafxapplication1.sqlWrappers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafxapplication1.SqlWorker;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class Labels {

    private SqlWorker sqlWorker;
    private ResultSet resultSet;
    private boolean isAdded;
    private int id;
    private String name;
    private String description;
    private String label;

    public Labels(SqlWorker sqlWorker) throws SQLException {
        Logger.getLogger("org.jaudiotagger").setLevel(Level.OFF);
        this.sqlWorker = sqlWorker;
        String query = "SELECT * FROM Labels";
        PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
        resultSet = stmt.executeQuery();
        if (!resultSet.isBeforeFirst()) {
            query = "INSERT INTO labels (idlabel, name, description, img) "
                    + "VALUES (?, ?, ?, ?)";
            stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setInt(1, 1);
            stmt.setString(2, "N/A");
            stmt.setNull(3, java.sql.Types.NULL);
            stmt.setNull(4, java.sql.Types.NULL);
            stmt.executeUpdate();
        }
    }

    public void addFromTag(Tag tag) throws SQLException {
        try {
            String index = "";
            label = "";
            if (!exists(tag.getFirst(FieldKey.RECORD_LABEL)) && !tag.getFirst(FieldKey.RECORD_LABEL).isEmpty()) {
                index = getMaxIndex();
                if ("0".equals(index)) {
                    id = 1;
                    label = "N/A";
                } else {
                    id = findEmptyIndex();
                    if (id == 0) {
                        id = 1;
                    }
                    label = tag.getFirst(FieldKey.RECORD_LABEL);
                }
                try {
                    String query = "INSERT INTO labels (idlabel, name, description, img) "
                            + "VALUES (?, ?, ?, ?)";

                    PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
                    stmt.setInt(1, id);
                    stmt.setString(2, label);
                    stmt.setNull(3, java.sql.Types.NULL);
                    stmt.setNull(4, java.sql.Types.NULL);
                    stmt.executeUpdate();
                    isAdded = true;
                } catch (SQLException e) {
                    System.out.println("##### Ошибка вставки лейбла!");
                }
                System.out.println("Label %" + label + "% added");
            } else if (exists(tag.getFirst(FieldKey.RECORD_LABEL)) && !tag.getFirst(FieldKey.RECORD_LABEL).isEmpty()) {
                try {
                    String query = "SELECT name FROM Labels WHERE name = ?";
                    PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
                    stmt.setString(1, tag.getFirst(FieldKey.RECORD_LABEL));
                    resultSet = stmt.executeQuery();
                    resultSet.next();
                    label = tag.getFirst(FieldKey.RECORD_LABEL);
                } catch (SQLException e) {
                }
            }
            if (label.equals("")) {
                label = "N/A";
            }
        } catch (Exception e) {
        }

    }

    public boolean exists(String name) throws SQLException {
        PreparedStatement stmt = sqlWorker.getConnection().
                prepareStatement("SELECT COUNT(*) AS aCount FROM labels WHERE name = ?");
        stmt.setString(1, name);
        resultSet = stmt.executeQuery();
        resultSet.next();
        if (!resultSet.getString("aCount").equals("0")) {
            return true;
        } else {
            return false;
        }
    }

    public String getMaxIndex() throws SQLException {
        resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount "
                + "FROM labels;");
        resultSet.next();
        return resultSet.getString("aCount");
    }

    public boolean isAdded() {
        return isAdded;
    }

    public int getId() throws SQLException {
        PreparedStatement stmt = sqlWorker.getConnection().
                prepareStatement("SELECT idlabel FROM labels WHERE name = ?");
        stmt.setString(1, label);
        resultSet = stmt.executeQuery();
        resultSet.next();
        if (resultSet.getString("idlabel").isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(resultSet.getString("idlabel"));
        }
    }

    private int findEmptyIndex() throws SQLException {
        int index = 0;
        int lastIndex = 0;

        resultSet = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS aCount FROM labels;");
        resultSet.next();
        int cnt = Integer.parseInt(resultSet.getString("aCount"));
        resultSet = sqlWorker.getStat().executeQuery("SELECT idlabel FROM labels ORDER BY idlabel DESC LIMIT 1;");
        resultSet.next();

        if (cnt > 0) {
            lastIndex = Integer.parseInt(resultSet.getString("idlabel"));
        } else {
            lastIndex = 0;
        }

        if (cnt < lastIndex) {
            resultSet = sqlWorker.getStat().executeQuery("SELECT idlabel FROM labels ORDER BY idlabel;");
            int count = 0;

            while (resultSet.next()) {
                count++;
                System.out.println("id: " + resultSet.getInt("idlabel"));
                if (resultSet.getInt("idlabel") > count) {
                    index = count;
                    break;
                } else {
                    index = resultSet.getInt("idlabel") + 1;
                }
            }
        } else if (cnt == lastIndex) {
            index = lastIndex + 1;
        }
        return index;
    }
}
