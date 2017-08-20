package kon.shol;

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

import java.io.IOException;
import java.util.List;


public class LanguageDetectorTest {
    public static void main(String[] args) throws IOException {
        //load all languages:
        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

//build language detector:
        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();

//create a text object factory
        TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

//query:
        TextObject textObject = textObjectFactory.forText("سلام به شما دوست عزیز");
        TextObject textObject2 = textObjectFactory.forText("hello my dear friends");
        Optional<LdLocale> lang = languageDetector.detect(textObject);
        Optional<LdLocale> lang2 = languageDetector.detect(textObject2);
        System.out.println(lang.get().getLanguage());
        System.out.println(lang2.get().getLanguage());
    }
}
