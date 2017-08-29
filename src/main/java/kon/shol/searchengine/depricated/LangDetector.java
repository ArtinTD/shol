package kon.shol;

import com.google.common.base.Optional;
//import com.optimaize.langdetect.LanguageDetector;
//import com.optimaize.langdetect.LanguageDetectorBuilder;
//import com.optimaize.langdetect.i18n.LdLocale;
//import com.optimaize.langdetect.ngram.NgramExtractors;
//import com.optimaize.langdetect.profiles.LanguageProfile;
//import com.optimaize.langdetect.profiles.LanguageProfileReader;
//import com.optimaize.langdetect.text.CommonTextObjectFactories;
//import com.optimaize.langdetect.text.TextObject;
//import com.optimaize.langdetect.text.TextObjectFactory;
import org.apache.tika.language.LanguageIdentifier;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;


public class LangDetector {

    static String detectLang(String text) throws IOException {
//        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
//        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
//                .withProfiles(languageProfiles)
//                .build();
//        TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
//        TextObject textObject = textObjectFactory.forText(text);
//        Optional<LdLocale> lang = languageDetector.detect(textObject);
//        return lang.get().getLanguage();
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
