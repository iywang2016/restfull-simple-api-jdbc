package Autorization.DatabaseRequest;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;

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
        String res = "ok";
        try {
            getConnection();

            stm = conn.createStatement();
            String sql = ("SELECT Password FROM Users WHERE Username ='"+userName+"';");
            rs = stm.executeQuery(sql);
            String rightPassword = rs.getString("Password");

            if (!rightPassword.equals(interPassword)) {
                sql = "SELECT MAX(UserID) FROM Users";
                rs = stm.executeQuery(sql);
                int userID = rs.getInt("UserID");
                userID++;
                sql = ("INSERT INTO Users (UserID,UserName,Password) VALUES ("+userID+",'"+userName+"','"+interPassword+"');");
                stm.execute(sql);
                 res = "reg";
            }
        }catch (Exception e) {
            System.out.println(e.toString());}
        finally {
            conn.close();
        }
        return res;
    }
    public static ArrayList<String> top10Requsters() throws ClassNotFoundException, SQLException, NamingException{
            ArrayList<String> res = new ArrayList<>();
            getConnection();

            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT Request, COUNT(*) AS times_requested FROM Requests GROUP BY Request ORDER BY times_requested DESC LIMIT 10");
            while (rs.next()){
                res.add(rs.getString("Request"));
            }
            rs.close();
            stm.close();
            conn.close();

            return res;
    }
}
