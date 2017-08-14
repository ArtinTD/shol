package kon.shol;

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
    boolean setHtml() {
        try {
            this.page.html=Jsoup.connect(page.link).get();
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
}