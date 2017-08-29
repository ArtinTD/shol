package kon.shol;

import org.apache.tika.language.LanguageIdentifier;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class LangDetector {

    static String detectLang(String text) throws IOException {
        LanguageIdentifier identifier = new LanguageIdentifier(text);
        return identifier.getLanguage();
    }
    static boolean checkMetaLangEn(Document document) {
        if (!document.select("html").attr("lang").contains("en") && !document.select("html").attr("lang").isEmpty()) {
            return false;
        }
        return true;
    }
}
