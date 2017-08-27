package kon.shol.searchengine.parser;

import com.google.common.net.InternetDomainName;
import kon.shol.PageData;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {
    public ArrayList<String> extractLinks(Document doc) {
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

    public PageData parse(Document doc) throws IOException {
        kon.shol.PageData pageData = new PageData();
        pageData.setLinks(extractLinks(doc));
        pageData.setAnchors(extractAnchors(doc));
        pageData.setText(doc.text());
        pageData.setTitle(doc.title());
        pageData.setDescription(doc.select("meta[name=description]").attr("content"));
        pageData.setImagesAlt(String.join(" ", doc.select("img").eachAttr("alt")));
        pageData.setH1h3(doc.select("h1,h2,h3").text());
        pageData.setH4h6(doc.select("h4,h5,h6").text());
        return pageData;
    }

    private String trimLink(Element link) {
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

    public String getDomain(String link) {

        try {
            URL url = new URL(link);
            return InternetDomainName.from(url.getHost()).topPrivateDomain().toString();

        } catch (Exception ignore) {
            return null;
        }
    }

    private HashMap<String, String> extractAnchors(Document doc) throws IOException {
        HashMap<String, String> hashMap = new HashMap<>();
        Elements links;
        links = doc.select("a");
        for (Element link : links) {
            String linkRef = trimLink(link);
            String linkText = link.text();
            if (linkRef != null && linkText.length() > 0) {
                hashMap.put(linkRef, linkText);
            }
        }
        return hashMap;
    }
}
