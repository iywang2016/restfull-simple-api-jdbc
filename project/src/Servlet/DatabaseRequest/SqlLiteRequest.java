package Servlet.DatabaseRequest;

import sun.util.cldr.CLDRLocaleDataMetaInfo;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by STUDS8_2 on 12/19/2016.
 */
public class SqlLiteRequest {
    private static Connection conn;
    private static Statement stm;
    private static ResultSet rs;

    private static void getConnection() throws ClassNotFoundException, SQLException, NamingException{
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:D:\\testdb");
    }

    public static String authorization(String userName, String interPassword) throws SQLException, ClassNotFoundException, NamingException{
        String res = "ok";
        try {
            getConnection();

            stm = conn.createStatement();
            String sqlRequest = ("SELECT Password FROM Users WHERE Username ='"+userName+"';");
            rs = stm.executeQuery(sqlRequest);
            String rightPassword = rs.getString("Password");

            if (rightPassword.equals("") || rightPassword.equals(null)) {
                sqlRequest = "SELECT MAX(UserID) FROM Users";
                rs = stm.executeQuery(sqlRequest);
                int userID = rs.getInt("UserID");
                userID++;
                sqlRequest = "INSERT INTO Users (UserID,UserName,Password) VALUES ("+userID+",'"+userName+"','"+interPassword+"');";
                stm.execute(sqlRequest);
                 res = "reg";
            }
            else if(!rightPassword.equals(interPassword))
                res = "Retry";
        }catch (Exception e) {
            System.out.println(e.toString());}
        finally {
            closeAllConections();
        }
        return res;
    }

    public static ArrayList<String> top10Requsts() throws ClassNotFoundException, SQLException, NamingException{
            ArrayList<String> res = new ArrayList<>();
            getConnection();

            stm = conn.createStatement();
            rs = stm.executeQuery("SELECT Request, COUNT(*) AS times_requested FROM Requests GROUP BY Request ORDER BY times_requested DESC LIMIT 10");
            while (rs.next()){
                res.add(rs.getString("Request"));
            }
            closeAllConections();
            return res;
    }

    public static void deleteUser(String userName) throws ClassNotFoundException, SQLException, NamingException{
            getConnection();

            stm = conn.createStatement();
            String sqlRequest = "DELETE FROM Users WHERE Username ='" +userName+"';";
            stm.executeQuery(sqlRequest);

            closeAllConections();
    }

    public static ArrayList<String> getInfoAboutUser(String userName) throws ClassNotFoundException, SQLException, NamingException{
        ArrayList<String> res = new ArrayList<>();
        getConnection();

        stm = conn.createStatement();
        String sqlRequest = "SELECT Number,LastTime,AverageTime FROM Users WHERE Username ='" +userName+"';";
        res.add(stm.executeQuery(sqlRequest).getString("Number"));
        res.add(stm.executeQuery(sqlRequest).getString("LastTime"));
        res.add(stm.executeQuery(sqlRequest).getString("AverageTime"));

        closeAllConections();
        return res;
    }

    public static String[] detectLanguage(String text) throws ClassNotFoundException, SQLException, NamingException{
        String []res = new String[2];
        getConnection();
        double tmp;
        stm = conn.createStatement();
        String sqlRequest = "SELECT Probability,Language FROM Words WHERE Word ='"+text+"';";
        rs = stm.executeQuery(sqlRequest);
        res[0] = rs.getString("Language");
        res[1] = Double.toString(rs.getDouble("Probability"));
        closeAllConections();
        return res;
    }

    public static void rememberWord(String word, String language, String probab) throws ClassNotFoundException, SQLException, NamingException{
        getConnection();
        double probability = Double.parseDouble(probab);
        stm = conn.createStatement();
        String sqlRequest = "INSERT INTO Words (Word,Language,Probability) VALUES ("+word+",'"+language+"','"+probability+"');";
        stm.executeQuery(sqlRequest);

        closeAllConections();
    }

    public static void newRequest(String userName, String word) throws ClassNotFoundException, SQLException, NamingException{
        getConnection();

        stm = conn.createStatement();
        String sqlRequest = "INSERT INTO Requests (Username,Request) VALUES ("+userName+",'"+word+"');";
        stm.executeQuery(sqlRequest);

        closeAllConections();
    }

    private static void closeAllConections() throws ClassNotFoundException, SQLException, NamingException{
        rs.close();
        stm.close();
        conn.close();
    }
}
