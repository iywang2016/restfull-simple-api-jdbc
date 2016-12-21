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

            if (rs.isClosed()) {
                sqlRequest = "INSERT INTO Users (UserName,Password) VALUES ('"+userName+"','"+interPassword+"');";
                stm.executeUpdate(sqlRequest);
                 res = "reg";
            }
            else if(!rs.getString("Password").equals(interPassword))
                res = "Retry";//date('now');
            sqlRequest = "UPDATE Users SET LastTime = date('now') WHERE Username ='"+userName+"';";
            stm.executeUpdate(sqlRequest);
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
            stm.executeUpdate(sqlRequest);

            closeAllConections();
    }

    public static ArrayList<String[]> getInfoAboutUsers(String userName) throws ClassNotFoundException, SQLException, NamingException{
        ArrayList<String[]> res = new ArrayList<>();
        getConnection();

        stm = conn.createStatement();
        String sqlRequest = "SELECT Username,Number,LastTime FROM Users;";
        rs = stm.executeQuery(sqlRequest);
        while (rs.next()) {
            String tmp[] = new String[4];
            tmp[0] = rs.getString("Username");
            tmp[1] = rs.getString("Number");
            tmp[2] = rs.getString("LastTime");
            res.add(tmp);
        }
        sqlRequest = "SELECT  julianday('now') - AverageTime as days FROM Users";
        rs = stm.executeQuery(sqlRequest);
        for (int i = 0; i < res.size();++i){
            int a = (rs.getInt("days")/Integer.parseInt(res.get(i)[2]));
            res.get(i)[3]=String.valueOf(a);
        }
        closeAllConections();
        return res;
    }

    public static String[] detectLanguage(String text) throws ClassNotFoundException, SQLException, NamingException {
        String[] res = new String[2];
        getConnection();
        double tmp;
        stm = conn.createStatement();
        String sqlRequest = "SELECT Probability,Language FROM Words WHERE Word ='" + text + "';";
        rs = stm.executeQuery(sqlRequest);
        if (!rs.isClosed()){
            res[0] = rs.getString("Language");
            res[1] = Double.toString(rs.getDouble("Probability"));
        }
        closeAllConections();
        return res;
    }

    public static void rememberWord(String word, String language, String probab) throws ClassNotFoundException, SQLException, NamingException{
        getConnection();
        double probability = Double.parseDouble(probab);
        stm = conn.createStatement();
        String sqlRequest = "INSERT INTO Words (Word,Language,Probability) VALUES ('"+word+"','"+language+"','"+probability+"');";
        stm.executeUpdate(sqlRequest);

        closeAllConections();
    }

    public static void newRequest(String userName, String word) throws ClassNotFoundException, SQLException, NamingException{
        getConnection();

        stm = conn.createStatement();
        String sqlRequest = "INSERT INTO Requests (Username,Request) VALUES ('"+userName+"','"+word+"');";
        stm.executeUpdate(sqlRequest);
        closeAllConections();
        updateUserInfo(userName);

    }

    private static void updateUserInfo(String userName)throws ClassNotFoundException, SQLException, NamingException {
        getConnection();

        stm = conn.createStatement();
        String sqlRequest = "SELECT Number FROM Users WHERE Username ='"+userName+"';";
        rs = stm.executeQuery(sqlRequest);
        int number = rs.isClosed() ? 1 : rs.getInt("Number");
        sqlRequest = "UPDATE Users SET Number = "+number+" WHERE Username = '"+userName+"';";
        stm.executeUpdate(sqlRequest);
        if (number == 1) {
            sqlRequest = "UPDATE Users SET AverageTime = julianday('now') WHERE Username = '"+userName+"';";
            stm.executeUpdate(sqlRequest);
        }
        closeAllConections();


    }

    private static void closeAllConections() throws ClassNotFoundException, SQLException, NamingException{
        rs.close();
        stm.close();
        conn.close();
    }
}
