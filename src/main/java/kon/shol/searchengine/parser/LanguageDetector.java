package kon.shol.searchengine.parser;

import kon.shol.searchengine.parser.exceptions.InvalidLanguageException;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.LanguageProfile;
import org.jsoup.nodes.Document;


import java.io.IOException;

class LanguageDetector {

    private final String ENGLISH_LANGUAGE = "en";


    public String detectLanguage(Document document) throws IOException {
        System.out.println(document.text());
        LanguageIdentifier identifier = new LanguageIdentifier(document.text());
        return identifier.getLanguage();
//        TODO: THIS IS NOT WORKING :|
    }

    private boolean checkMetaLanguage(Document document, String language) {
        if (!document.select("html").attr("lang").contains(language) && !document.select("html").attr("lang").isEmpty()) {
            return false;
        }
        return true;
    }

    boolean isEnglish(Document document) throws IOException {
        if (!checkMetaLanguage(document, ENGLISH_LANGUAGE)) {
            throw new InvalidLanguageException("Meta not English: " + document.location());
        } else if (!detectLanguage(document).equals(ENGLISH_LANGUAGE)) {
            throw new InvalidLanguageException("Text not English: " + document.location());
        }
        return true;
    }

    public static void main(String args[]){
        LanguageProfile profile = new LanguageProfile("hello world what apple bill ");
        LanguageIdentifier identifier = new LanguageIdentifier(profile);
        System.out.println(identifier.getLanguage());
    }

}
