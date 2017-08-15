package kon.shol;
;
import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.common.base.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    static ArrayList<String> extractLinks(Document doc) {
        Elements links;
        ArrayList<String> result = new ArrayList<>();
        links = doc.select("a");
        for (Element link : links) {
            result.add(trimLink(link));
        }
        return result;
    }

    static String trimLink(Element link) {
        try {
            String temp = link.attr("abs:href");
            if (temp.charAt(temp.length() - 1) == '/') {
                StringBuilder sb = new StringBuilder(temp);
                sb.deleteCharAt(temp.length() - 1);
                temp = sb.toString();
            }
            if (!temp.contains("http") || temp.contains("#")) {
                return null;
            }
            return temp;
        } catch (StringIndexOutOfBoundsException ignore) {
            return null;
        }
    }

    static String getDomain(String link){
        try {
            URL url = new URL(link);
            return url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String getLanguage(String link) {

        return null;
    }
}
