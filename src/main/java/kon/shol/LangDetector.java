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
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;


public class LangDetector {

    static String detectLang(String text) throws IOException {
        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();
        TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
        TextObject textObject = textObjectFactory.forText(text);
        Optional<LdLocale> lang = languageDetector.detect(textObject);
        return lang.get().getLanguage();
    }
    static boolean checkMetaLangEn(Document document) {
        if (!document.select("html").attr("lang").contains("en") && !document.select("html").attr("lang").isEmpty()) {
            return false;
        }
        return true;
    }
    public static void main(String[] args) throws IOException {
        System.out.println(detectLang("Art is a diverse range of human activities in creating visual, auditory or performing artifacts (artworks), expressing the author's imaginative or technical skill, intended to be appreciated for their beauty or emotional power.[1][2] In their most general form these activities include the production of works of art, the criticism of art, the study of the history of art, and the aesthetic dissemination of art."));
    }
}
