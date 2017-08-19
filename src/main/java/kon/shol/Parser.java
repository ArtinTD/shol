package kon.shol;

;
import com.google.common.base.Optional;
import com.google.common.net.InternetDomainName;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.common.base.*;
import rankpage.Page;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class Parser {

    static ArrayList<String> extractLinks(Document doc) {
        Elements links;
        ArrayList<String> result = new ArrayList<>();
        links = doc.select("a");
        for (Element link : links) {
            String temp = trimLink(link);
            if (temp != null)
                result.add(trimLink(link));
        }
        return result;
    }

    static PageData parse(Document doc) {
        PageData pageData = new PageData();
        pageData.links = extractLinks(doc);
        pageData.text = doc.text();
        pageData.title = doc.title();
        pageData.description = doc.select("meta[name=description]").attr("content");
        if (pageData.description == null) {
            System.out.println("No Description");
        }
        pageData.h1h3 = (ArrayList<String>) doc.select("h1,h2,h3").eachText();
        pageData.h4h6 = (ArrayList<String>) doc.select("h4,h5,h6").eachText();
        return pageData;
    }

    static String trimLink(Element link) {
        try {
            String temp = link.attr("abs:href");
            if (temp.charAt(temp.length() - 1) == '/') {
                StringBuilder sb = new StringBuilder(temp);
                sb.deleteCharAt(temp.length() - 1);
                temp = sb.toString();
            }
            if (!temp.contains("http") || temp.contains("#")) {
                return null;
            }
            return temp;
        } catch (StringIndexOutOfBoundsException ignore) {
            return null;
        }
    }

    static String getDomain(String link) {
        try {
            URL url = new URL(link);
            return InternetDomainName.from(url.getHost()).topPrivateDomain().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static boolean isEnglish(Document document) {

        if (!document.select("html").attr("lang").contains("en") && !document.select("html").attr("lang").isEmpty()) {
            System.out.println("Not English");
            return false;
        }
        System.out.println("don't know");
        return false;
    }
}
