package kon.shol;

import org.jsoup.Jsoup;
import static kon.shol.Main.hBase;

public class Fetcher {

    WebPage page = new WebPage();

    boolean setHTML() {

        try {
            this.page.html = Jsoup.connect(this.page.link).get();

            if (!LangDetector.checkMetaLangEn(this.page.html)) {
                throw new Exception("Meta not English: " + this.page.link);
            } else if (!LangDetector.detectLang(this.page.html.text()).equals("en")) {
                throw new Exception("Text not English: " + this.page.link);
            }

            page.pageData = Parser.parse(this.page.html);

            hBase.putPageData(this.page.link, page.pageData);
            System.out.println("Added " + this.page.link + " Data To HBase");
            return true;

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}

