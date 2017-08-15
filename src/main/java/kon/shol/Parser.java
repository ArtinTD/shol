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
import java.util.List;

public class Parser {

    static Elements extractLinks(Document doc) {
        Elements links;
        links = doc.select("a");
        return links;
    }

    static String trimLink(Element link) {
        try {
            String temp = link.attr("abs:href");
            if (temp.charAt(temp.length() - 1) == '/') {
                StringBuilder sb = new StringBuilder(temp);
                sb.deleteCharAt(temp.length() - 1);
                temp = sb.toString();

                if (!temp.contains("http")) {
                    return null;
                }
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
}
