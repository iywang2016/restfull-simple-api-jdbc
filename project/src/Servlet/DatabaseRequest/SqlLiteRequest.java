package Servlet.DatabaseRequest;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
//убрать static. разбить на разные функции изменить имена функций. изменить sql запросы. убрать абсолютный файловй путь.не делать глобальный переменный для подключений
//работь с обьектами а не строками ловить эксепшионы более узко
//изменить форматирование
/**
 * Created by STUDS8_2 on 12/19/2016.
 */
public class SqlLiteRequest {
    private  Connection conn;
    private  Statement stm;
    private  ResultSet rs;

    private  void createConnection() throws ClassNotFoundException, SQLException, NamingException{
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:Testbd.db");
        stm = conn.createStatement();
    }

    public  String autorize(String userName, String interPassword) throws SQLException, ClassNotFoundException, NamingException{
        String res = "";
        try {
            createConnection();
            String sqlRequest = ("SELECT Password,Username FROM Users WHERE Username ='"+userName+"';");
            rs = stm.executeQuery(sqlRequest);
            res = rs.isClosed() ? register(userName,interPassword) : checkPassword(interPassword);
        }catch (Exception e) {
            System.out.println(e.toString());
        }
        return res;
    }

    private String register(String userName, String interPassword)throws SQLException, ClassNotFoundException,NamingException{
        String sqlRequest = "INSERT INTO Users (UserName,Password,LastTime,Number,AverageTime) VALUES ('"+userName+"','"+interPassword+"',date('now'),0,julianday('now'));";
        stm.executeUpdate(sqlRequest);
        return "reg";
    }

    private String checkPassword(String interPassword) throws SQLException, ClassNotFoundException, NamingException{
        if (!rs.getString("Password").equals(interPassword))
            return "Retry";
        else {
            String userName = rs.getString("userName");
            String  sqlRequest = "UPDATE Users SET LastTime = date('now') WHERE Username ='"+userName+"';";
            stm.executeUpdate(sqlRequest);
            return "ok";
        }
    }

    public  ArrayList<String> top10Requsts() throws ClassNotFoundException, SQLException, NamingException{
            ArrayList<String> res = new ArrayList<>();
            createConnection();
            rs = stm.executeQuery("SELECT Request, COUNT(*) AS times_requested FROM Requests GROUP BY Request ORDER BY times_requested DESC LIMIT 10");
            while (rs.next()){
                res.add(rs.getString("Request"));
            }
            return res;
    }

    public  void deleteUser(String userName) throws ClassNotFoundException, SQLException, NamingException{
            createConnection();
            String sqlRequest = "DELETE FROM Users WHERE Username ='" +userName+"';";
            stm.executeUpdate(sqlRequest);
    }

    public  ArrayList<String[]> getInfoAboutUsers(String userName) throws ClassNotFoundException, SQLException, NamingException{
        ArrayList<String[]> res = new ArrayList<>();
        createConnection();
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

    public  String[] detectLanguage(String text) throws ClassNotFoundException, SQLException, NamingException {
        createConnection();
        double tmp;
        String sqlRequest = "SELECT Probability,Language FROM Words WHERE Word ='" + text + "';";
        rs = stm.executeQuery(sqlRequest);
        return rs.isClosed() ? new String[]{} : new String[]{rs.getString("Language"),rs.getString("Probability")};
    }

    public  void rememberWord(String word, String language, String probab) throws ClassNotFoundException, SQLException, NamingException{
        createConnection();
        double probability = Double.parseDouble(probab);
        String sqlRequest = "INSERT INTO Words (Word,Language,Probability) VALUES ("+"'"+word+"','"+language+"','"+probability+"');";
        stm.executeUpdate(sqlRequest);

    }

    public  void newRequest(String userName, String word) throws ClassNotFoundException, SQLException, NamingException{
        createConnection();
        String sqlRequest = "INSERT INTO Requests (Username,Request) VALUES ('"+userName+"','"+word+"');";
        stm.executeUpdate(sqlRequest);
        updateUserInfo(userName);
    }

    private  void updateUserInfo(String userName)throws ClassNotFoundException, SQLException, NamingException {
        createConnection();
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

    public  void closeAllConections() throws ClassNotFoundException, SQLException, NamingException{
        rs.close();
        stm.close();
        conn.close();
    }
}
