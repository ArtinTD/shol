package kon.shol;

import com.google.common.net.InternetDomainName;
import com.oracle.jrockit.jfr.Producer;
import kon.shol.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class Fetcher {
    WebPage page = new WebPage();
    HBase hbase = Main.hBase;

    boolean setHTML() {
        try {
            this.page.html = Jsoup.connect(this.page.link).get();
            if (!LangDetector.checkMetaLangEn(this.page.html)) {
                return false;
            } else if (!LangDetector.detectLang(this.page.html.text()).equals("en")) {
                return false;
            }
            PageData pageData = Parser.parse(this.page.html);
            System.out.println(pageData.toString());
            hbase.putPageData(this.page.link, pageData);
            System.out.println("Added " + this.page.link + " Data To Hbase");
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
}

