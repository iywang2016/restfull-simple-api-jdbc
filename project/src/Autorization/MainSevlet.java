package Autorization;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Autorization.DatabaseRequest.*;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by STUDS8_2 on 12/19/2016.
 */
public class MainSevlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

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
            JSONObject jsonToReturn = new JSONObject();
            int command = jsonObject.getInt("command");
            switch (command){
                case 0:
                    String userName = jsonObject.getString("UserName");
                    String password = jsonObject.getString("password");
                    jsonToReturn.put("answer",SqlLiteRequest.authorization(userName,password));
                    break;
                case 1:
                    ArrayList<String> top10Requests = SqlLiteRequest.top10Requsts();
                    jsonToReturn.put("answer","top10requests");
                    jsonToReturn.put("top10Requests",top10Requests.toString());
                    break;
                case 2:
                    String userToDelete = jsonObject.getString("UserName");
                    SqlLiteRequest.deleteUser(userToDelete);
                    jsonToReturn.put("answer","userDeleted");
                    break;
                case 3:
                    String userRequest = jsonObject.getString("UserName");
                    ArrayList<String> userInformation = SqlLiteRequest.getInfoAboutUser(userRequest);
                    jsonToReturn.put("answer","userInformation");
                    jsonToReturn.put("timeRequested",userInformation.get(0));
                    jsonToReturn.put("date-time",userInformation.get(1));
                    jsonToReturn.put("averagetime",userInformation.get(2));
                    break;
                case 4:

                    break;
            }
            out.println(jsonToReturn.toString());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
