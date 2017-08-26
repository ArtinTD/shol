package kon.shol;

;
import com.google.common.net.InternetDomainName;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


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
        pageData.imagesAlt = String.join(" ", doc.select("img").eachAttr("alt"));
        pageData.h1h3 = doc.select("h1,h2,h3").text();
        pageData.h4h6 = doc.select("h4,h5,h6").text();
        return pageData;
    }

    private static String trimLink(Element link) {

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

        } catch (StringIndexOutOfBoundsException ignore) { return null; }
    }

    static String getDomain(String link) {

        try {
            URL url = new URL(link);
            return InternetDomainName.from(url.getHost()).topPrivateDomain().toString();

        } catch (Exception ignore) { return null; }
    }
    public static HashMap<String, String> extractAnchors (Document doc){
        HashMap<String, String> hashMap = new HashMap<>();
        Elements links;
        links = doc.select("a");
        for (Element link : links) {
            hashMap.put(trimLink(link), link.text());
            System.out.print(trimLink(link));
            System.out.println(" : " + link.text());
        }
        return hashMap;
    }

}
