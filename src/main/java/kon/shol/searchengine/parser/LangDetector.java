package kon.shol.searchengine.parser;


import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.LanguageProfile;
import org.jsoup.nodes.Document;

import java.io.IOException;

class LangDetector {

    private final String ENGLISH_LANGUAGE = "en";

    private String detectLang(String text) throws IOException {

        LanguageProfile profile = new LanguageProfile(text);
        LanguageIdentifier identifier = new LanguageIdentifier(profile);
        return identifier.getLanguage();
    }

    private boolean checkMetaLanguage(Document document, String language) {

        return document.select("html").attr("lang").contains(ENGLISH_LANGUAGE) ||
                document.select("html").attr("lang").contains("mul") ||
                document.select("html")
                        .attr("lang").isEmpty();
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
                    return lang.equals(ENGLISH_LANGUAGE);
                }
            }
        }
    }
}
