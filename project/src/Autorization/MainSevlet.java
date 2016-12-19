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

            int command = jsonObject.getInt("command");
            switch (command){
                case 0:
                    String userName = jsonObject.getString("UserName");
                    String password = jsonObject.getString("password");
                    JSONObject jsonToReturn = new JSONObject();
                    if (SqlLiteRequest.authorization(userName,password).equals("ok"))
                        jsonToReturn.put("anwser","ok");
                    else
                        jsonToReturn.put("anwser","reg");
                    break;
                case 1:

            }
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
