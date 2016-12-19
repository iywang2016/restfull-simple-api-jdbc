package Autorization.DatabaseRequest;

import javax.naming.NamingException;
import java.sql.*;

/**
 * Created by STUDS8_2 on 12/19/2016.
 */
public class SqlLiteRequest {
    public static Connection conn;
    public static Statement stm;
    public static ResultSet rs;

    public static void getConnection() throws ClassNotFoundException, SQLException, NamingException{
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:D:\\testdb");
    }

    public static String authorization(String userName, String interPassword) throws SQLException, ClassNotFoundException{
        stm = conn.createStatement();
        String sql = ("SELECT Password FROM Users WHERE Username ='"+userName+"';");
        rs = stm.executeQuery(sql);
        String rightPassword = rs.getString("Password");
        if (rightPassword.equals(interPassword))
            return "ok";
        else
            return "reg";
    }
}
