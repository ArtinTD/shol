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
    boolean setHTML() {
        try {
            this.page.html=Jsoup.connect(page.link).get();
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }
    public static void main(String args[]){
        System.out.println(InternetDomainName.from("fa.google.com").topPrivateDomain().toString());
    }
}

