package LanguageDetection;
import java.util.ArrayList;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;

public class Detectr {
    static  private void init(String profileDirectory) throws LangDetectException {
        DetectorFactory.loadProfile(profileDirectory);
    }
    static private String detect(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
    }
    static private ArrayList detectLangs(String text) throws LangDetectException {
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.getProbabilities();
    }

   static public String[] detecteLanguage(String text) throws LangDetectException{
        init("D:\\project\\profiles.sm");
        String tmp = detectLangs(text).get(0).toString();
        String []res = tmp.split(":");
        return res;
    }

}