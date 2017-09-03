package kon.shol.searchengine.parser;


import kon.shol.searchengine.parser.exceptions.InvalidLanguageException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.LanguageProfile;
import org.apache.tika.language.ProfilingHandler;
import org.apache.tika.language.ProfilingWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.util.List;

class LangDetector {

    private final String ENGLISH_LANGUAGE = "en";

    private String detectLang(String text) throws IOException {

        LanguageProfile profile = new LanguageProfile(text);
        LanguageIdentifier identifier = new LanguageIdentifier(profile);
        return identifier.getLanguage();
    }

    private boolean checkMetaLanguage(Document document, String language) {

        if (!(document.select("html").attr("lang").contains(ENGLISH_LANGUAGE) ||
                document.select("html").attr("lang").contains("mul")) &&
                !document.select("html")
                .attr("lang").isEmpty()) {
            return false;
        }
        return true;
    }

    boolean isEnglish(Document document) throws IOException {
        String lang ;
        if(!checkMetaLanguage(document, ENGLISH_LANGUAGE)){
            return false;
        }
        else{
            lang = detectLang(document.select("meta[name=description]").attr("content"));
            if(lang.equals(ENGLISH_LANGUAGE)){
                return true;
            }
            else{
                lang = detectLang(document.title());
                if(lang.equals(ENGLISH_LANGUAGE)){
                    return true;
                }
                else {
                    lang = detectLang(document.text());
                    if(lang.equals(ENGLISH_LANGUAGE))
                        return true;
                    else
                        return false;
                }
            }
        }
    }

    public static void main(String args[]) throws IOException {

        LangDetector langDetector = new LangDetector();
        System.out.println(langDetector.isEnglish(Jsoup.connect("http://varzesh3.com").get()));

    }

}
