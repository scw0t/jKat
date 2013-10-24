package javafxapplication1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlWorker {

    private Statement stat = null;
    private ResultSet rs = null;
    private Connection conn;

    public SqlWorker() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {}
        
        connect();
    }

    private void connect() throws SQLException {
        try {
            conn =
                    DriverManager.getConnection("jdbc:mysql://localhost:3306/music?useUnicode=true&characterEncoding=utf-8", 
                    "root", "pass");
            setStat(conn.createStatement());
            
            System.out.println("Database connection successfull");
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } 
    }

    public Statement getStat() {
        return stat;
    }

    public void setStat(Statement stat) {
        this.stat = stat;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }
    
    public Connection getConnection(){
        return conn;
    }
}
