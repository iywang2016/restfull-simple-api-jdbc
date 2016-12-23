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
        conn = DriverManager.getConnection("jdbc:sqlite:D:\\Testbd.db");
    }

    public static String authorization(String userName, String interPassword) throws SQLException, ClassNotFoundException, NamingException{
        String res = "ok";
        try {
            getConnection();

            stm = conn.createStatement();
            String sqlRequest = ("SELECT Password FROM Users WHERE Username ='"+userName+"';");
            rs = stm.executeQuery(sqlRequest);

            if (rs.isClosed()) {
                sqlRequest = "INSERT INTO Users (UserName,Password,LastTime,Number,AverageTime) VALUES ('"+userName+"','"+interPassword+"',date('now'),0,julianday('now'));";
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
            return res;
    }

    public static void deleteUser(String userName) throws ClassNotFoundException, SQLException, NamingException{
            getConnection();

            stm = conn.createStatement();
            String sqlRequest = "DELETE FROM Users WHERE Username ='" +userName+"';";
            stm.executeUpdate(sqlRequest);

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
            double a = ((double)rs.getInt("days")/(Integer.parseInt(res.get(i)[1])+2));
            res.get(i)[3]=String.valueOf(a);
        }
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
        return res;
    }

    public static void rememberWord(String word, String language, String probab) throws ClassNotFoundException, SQLException, NamingException{
        getConnection();
        double probability = Double.parseDouble(probab);
        stm = conn.createStatement();
        String sqlRequest = "INSERT INTO Words (Word,Language,Probability) VALUES ("+"'"+word+"','"+language+"','"+probability+"');";
        stm.executeUpdate(sqlRequest);

    }

    public static void newRequest(String userName, String word) throws ClassNotFoundException, SQLException, NamingException{
        getConnection();

        stm = conn.createStatement();
        String sqlRequest = "INSERT INTO Requests (Username,Request) VALUES ('"+userName+"','"+word+"');";
        stm.executeUpdate(sqlRequest);
        updateUserInfo(userName);

    }

    private static void updateUserInfo(String userName)throws ClassNotFoundException, SQLException, NamingException {
        getConnection();

        stm = conn.createStatement();
        String sqlRequest = "SELECT Number FROM Users WHERE Username ='"+userName+"';";
        rs = stm.executeQuery(sqlRequest);
        int number = rs.isClosed() ? 0 : rs.getInt("Number");
        number++;
        sqlRequest = "UPDATE Users SET Number = "+number+" WHERE Username = '"+userName+"';";
        stm.executeUpdate(sqlRequest);
        if (number == 1 || number == 0) {
            sqlRequest = "UPDATE Users SET AverageTime = julianday('now') WHERE Username = '"+userName+"';";
            stm.executeUpdate(sqlRequest);
        }


    }

    public static void closeAllConections() throws ClassNotFoundException, SQLException, NamingException{
        rs.close();
        stm.close();
        conn.close();
    }
}
