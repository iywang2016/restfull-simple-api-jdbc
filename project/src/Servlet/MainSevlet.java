package Servlet;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import LanguageDetection.Detection.DetectHelper;
import Servlet.DatabaseRequest.*;
import LanguageDetection.*;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;

/**
 * Created by STUDS8_2 on 12/19/2016.
 */
@WebServlet(name="MainSevlet",urlPatterns={"/hello"})
public class MainSevlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.html");
        if (dispatcher != null){
            dispatcher.forward(request, response);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        StringBuilder jb = new StringBuilder();
        String line = null;

        try{
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e){
            System.out.println(e.toString());
        }

        try {
            JSONObject jsonObject = new JSONObject(jb.toString());

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            int command = jsonObject.getInt("command");
            switch (command){
                case 0:
                    String userName = jsonObject.getString("UserName");
                    String password = jsonObject.getString("password");
                    JSONObject jsonToReturn = new JSONObject();
                    jsonToReturn.put("answer",SqlLiteRequest.authorization(userName,password));
                    out.println(jsonToReturn.toString());
                    break;
                case 1:
                    JSONObject jsonToReturn1 = new JSONObject();
                    ArrayList<String> top10Requests = SqlLiteRequest.top10Requsts();
                    jsonToReturn1.put("answer","top10requests");
                    jsonToReturn1.put("top10Requests",top10Requests.toString());
                    out.println(jsonToReturn1.toString());
                    break;
                case 2:
                    JSONObject jsonToReturn2 = new JSONObject();
                    String userToDelete = jsonObject.getString("UserName");
                    SqlLiteRequest.deleteUser(userToDelete);
                    jsonToReturn2.put("answer","userDeleted");
                    out.println(jsonToReturn2.toString());
                    break;
                case 3:
                    JSONObject jsonToReturn3 = new JSONObject();
                    String userRequest = jsonObject.getString("UserName");
                    ArrayList<String[]> userInformation = SqlLiteRequest.getInfoAboutUsers(userRequest);
                    jsonToReturn3.put("answer","usersInformation");
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> numbers = new ArrayList<>();
                    ArrayList<String> date = new ArrayList<>();
                    ArrayList<String> average = new ArrayList<>();
                    for (int i = 0; i < userInformation.size();++i){
                        names.add(userInformation.get(i)[0]);
                        numbers.add(userInformation.get(i)[1]);
                        date.add(userInformation.get(i)[2]);
                        average.add(userInformation.get(i)[3]);
                    }
                    jsonToReturn3.put("UserName",names);
                    jsonToReturn3.put("timeRequested",numbers);
                    jsonToReturn3.put("date-time",date);
                    jsonToReturn3.put("averagetime",average);
                    out.println(jsonToReturn3.toString());
                    break;
                case 4:
                    JSONObject jsonToReturn4 = new JSONObject();
                    String word = jsonObject.getString("word");
                    String user = jsonObject.getString("UserName");
                    String language="error comes";
                    String probability="";
                    try {
                    String tmp[] = DetectHelper.Helper(user,word);
                    language = tmp[0];
                    probability = tmp[1];
                    }
                    catch (Exception e){}
                    jsonToReturn4.put("answer", "language");
                    jsonToReturn4.put("language",language);
                    jsonToReturn4.put("probability",probability);
                    out.println(jsonToReturn4.toString());
                    break;
            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
