package LanguageDetection;
import java.util.ArrayList;
import com.cybozu.labs.langdetect.*;

public class Detectr {
    static  private void init(String profileDirectory) throws LangDetectException,ClassNotFoundException {
        try {
            DetectorFactory.loadProfile(profileDirectory);
        }
        catch (Exception e){

        }
    }
    static private String detect(String text) throws LangDetectException,ClassNotFoundException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }
    static private ArrayList detectLangs(String text) throws LangDetectException ,ClassNotFoundException{
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.getProbabilities();
    }

    static public String[] detecteLanguage(String text) throws LangDetectException,ClassNotFoundException{
        init("D:\\project\\profiles.sm");
        String tmp = detectLangs(text).get(0).toString();
        String []res = tmp.split(":");
        return res;
    }

}