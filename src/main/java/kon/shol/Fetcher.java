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
            this.page.html = Jsoup.connect(page.link).get();
            PageData pageData = Parser.parse(this.page.html);
            hbase.putPageData(this.page.link, pageData);
            System.out.println("Added " + this.page.link + " Data To Hbase");
            return true;
        } catch (Exception ignore) {
//            ignore.printStackTrace();
            return false;
        }
    }
    public static void main(String args[]){
        System.out.println(InternetDomainName.from("fa.google.com").topPrivateDomain().toString());
    }
}

