package kon.shol.searchengine.parser;

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
import kon.shol.searchengine.crawler.Fetcher;
import kon.shol.searchengine.parser.exceptions.InvalidLanguageException;
import org.apache.tika.language.LanguageIdentifier;

import org.jsoup.nodes.Document;


import java.io.IOException;
import java.util.List;

class LangDetector {

    private final String ENGLISH_LANGUAGE = "en";


    private  String detectLang(Document document) throws IOException {
        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();

        TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

        TextObject textObject = textObjectFactory.forText(document.text());
        Optional<LdLocale> lang = languageDetector.detect(textObject);
        return lang.get().getLanguage();
    }

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
        } else if (!detectLang(document).equals(ENGLISH_LANGUAGE)) {
            throw new InvalidLanguageException("Text not English: " + document.location());
        }
        return true;

    }

    public static void main(String args[]) throws IOException {
       /* Fetcher fetcher = new Fetcher();
        Document document = fetcher.fetch("http://moz.com/top500");
        System.out.println(detectLang(document));*/
    }

}
