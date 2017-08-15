package kon.shol;

import org.jsoup.Jsoup;

public class Fetcher {
    WebPage page = new WebPage();
    public boolean setHTML() {
        try {
            this.page.html = Jsoup.connect(page.link).get();
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
}