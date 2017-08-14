package kon.shol;

import org.jsoup.Jsoup;

public class Fetcher {
    WebPage page = new WebPage();
    boolean setHTML() {
        try {
            this.page.html = Jsoup.connect(page.link).get();
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    public static void main(String[] args) {

        Fetcher temp = new Fetcher();
        temp.page.link = "http://asriran.com";
        temp.setHTML();
        System.out.println(temp.page.html.body().text());
    }
}