package Servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import LanguageDetection.*;
import Servlet.DatabaseRequest.*;

import com.cybozu.labs.langdetect.LangDetectException;
import org.json.JSONObject;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by STUDS8_2 on 12/19/2016.
 */
public class MainSevlet extends HttpServlet {

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
                    ArrayList<String[]> userInformation = SqlLiteRequest.getInfoAboutUsers(userRequest);
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
                    jsonToReturn.put("UserName",names);
                    jsonToReturn.put("timeRequested",numbers);
                    jsonToReturn.put("date-time",date);
                    jsonToReturn.put("averagetime",average);
                    break;
                case 4:
                    String word = jsonObject.getString("word");
                    String user = jsonObject.getString("UserName");
                    SqlLiteRequest.newRequest(user, word);

                    String tmp[] = SqlLiteRequest.detectLanguage(word);
                    String language = tmp[0];
                    String probability = tmp[1];
                    if (language==null || language.isEmpty()) {
                        String lanAndProb[] = LanguageDetection.Detectr.detecteLanguage(word);
                        SqlLiteRequest.rememberWord(word,lanAndProb[0],lanAndProb[1]);
                        language = lanAndProb[0];
                        probability = lanAndProb[1];
                    }
                    jsonToReturn.put("answer", "language");
                    jsonToReturn.put("language",language);
                    jsonToReturn.put("probability",probability);
                    break;
            }
            out.println(jsonToReturn.toString());
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }
}
