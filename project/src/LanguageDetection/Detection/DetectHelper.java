package LanguageDetection.Detection;

import com.cybozu.labs.langdetect.LangDetectException;
import org.sqlite.SQLiteException;

import javax.naming.NamingException;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;

import Servlet.*;
import sun.dc.path.PathException;

/**
 * Created by STUDS8_2 on 12/22/2016.
 */
public class DetectHelper {
    public static String[] Helper(String user,String word)throws LangDetectException, ClassNotFoundException,SQLException, NamingException, PathException{
        Servlet.DatabaseRequest.SqlLiteRequest.newRequest(user,word);

        String tmp[] = Servlet.DatabaseRequest.SqlLiteRequest.detectLanguage(word);
        if (tmp[0]==null || tmp[0].isEmpty()) {
            String lanAndProb[] = LanguageDetection.Detectr.detecteLanguage(word);
            Servlet.DatabaseRequest.SqlLiteRequest.rememberWord(word,lanAndProb[0],lanAndProb[1]);
            tmp[0] = lanAndProb[0];
            tmp[1] = lanAndProb[1];
        }
        return tmp;
    }
}
