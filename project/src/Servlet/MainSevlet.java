package Servlet;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import LanguageDetection.Detection.*;
import Servlet.DatabaseRequest.*;
import LanguageDetection.*;
import servletComands.*;

import com.cybozu.labs.langdetect.LangDetectException;
import org.json.JSONObject;
import scala.collection.mutable.StringBuilder;

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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        RequestDispatcher dispatcher = request.getRequestDispatcher("index.html");
        if (dispatcher != null){
            dispatcher.forward(request, response);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            JSONObject jsonObject = getJSONObject(request);
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            String command = jsonObject.getString("command");
            out.println(makeCommand(command,jsonObject).toString());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private JSONObject getJSONObject(HttpServletRequest request){
        StringBuilder jb = new StringBuilder();
        String line = null;
        try{
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e){
            System.out.println(e.toString());
        }
        return new JSONObject(jb.toString());
    }

    private JSONObject makeCommand(String command,JSONObject jsonObject) {
        switch (command){
            case "logIn":
                return logIN(jsonObject);
            case "top10Request":
                return getTop10Request(jsonObject);
            case "deleteUser":
                return deleteUser(jsonObject);
            case "usersInformation":
                return getUsersInformation(jsonObject);
            case "detectLanguage":
                return detectLanguage(jsonObject);
            default:
                return new JSONObject().put("anwser","something went wrong");
        }
    }
    public JSONObject logIN(JSONObject jsonObject){
        String userName = jsonObject.getString("UserName");
        String password = jsonObject.getString("password");
        JSONObject jsonToReturn = new JSONObject();
        jsonToReturn.put("answer",SqlLiteRequest.autorize(userName,password));
        SqlLiteRequest.closeAllConections();
        return jsonToReturn;
    }

    public JSONObject getTop10Request(JSONObject jsonObject){
        JSONObject jsonToReturn = new JSONObject();
        ArrayList<String> top10Requests = SqlLiteRequest.top10Requsts();
        jsonToReturn.put("answer","top10requests");
        jsonToReturn.put("top10Requests",top10Requests.toString());
        SqlLiteRequest.closeAllConections();
        return jsonToReturn;
    }

    public JSONObject deleteUser(JSONObject jsonObject){
        JSONObject jsonToReturn = new JSONObject();
        String userToDelete = jsonObject.getString("UserName");
        SqlLiteRequest.deleteUser(userToDelete);
        jsonToReturn.put("answer","userDeleted");
        SqlLiteRequest.closeAllConections();
        return jsonToReturn;
    }

    public JSONObject getUsersInformation(JSONObject jsonObject){
        String userRequest = jsonObject.getString("UserName");
        ArrayList<String[]> userInformation = SqlLiteRequest.getInfoAboutUsers(userRequest);
        JSONObject jsonToReturn = convertUserInformation(userInformation);
        SqlLiteRequest.closeAllConections();
        return jsonToReturn;
    }

    public JSONObject detectLanguage(JSONObject jsonObject){
        JSONObject jsonToReturn = new JSONObject();
        String language="error comes";
        String probability="";
        try {
            String tmp[] = DetectHelper.Helper(jsonObject.getString("UserName"),jsonObject.getString("word"));
            language = tmp[0];
            probability = tmp[1];
        }
        catch (Exception e){
            System.out.println("error in DetectHelper");
        }
        jsonToReturn.put("answer", "language");
        jsonToReturn.put("language",language);
        jsonToReturn.put("probability",probability);
        SqlLiteRequest.closeAllConections();
        return jsonToReturn;
    }

    public JSONObject convertUserInformation(ArrayList<String[]> userInformation){
        JSONObject jsonToReturn = new JSONObject();
        jsonToReturn.put("answer","usersInformation");
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
        jsonToReturn.put("UserName",names.toString());
        jsonToReturn.put("timeRequested",numbers.toString());
        jsonToReturn.put("date-time",date.toString());
        jsonToReturn.put("averagetime",average.toString());
        return jsonToReturn;
    }

}


