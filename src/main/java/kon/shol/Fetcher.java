package kon.shol;

import org.jsoup.Jsoup;

import static kon.shol.Main.hBase;
import static kon.shol.Main.logger;

public class Fetcher {

    WebPage page = new WebPage();

    boolean setHTML() {

        try {
            if (hBase.exists(this.page.link)) {
                throw new Exception("Already In Hbase : " + this.page.link);
            } else {
                this.page.html = Jsoup.connect(this.page.link).get();

                if (!this.page.link.contains("moz"))
                    if (!LangDetector.checkMetaLangEn(this.page.html)) {
                        throw new Exception("Meta not English: " + this.page.link);
                    } else if (!LangDetector.detectLang(this.page.html.text()).equals("en")) {
                        throw new Exception("Text not English: " + this.page.link);
                    }


                page.pageData = Parser.parse(this.page.html);

                hBase.putPageData(this.page.link, page.pageData);
                logger.error("Added " + this.page.link + " Data To HBase");
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}

