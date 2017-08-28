package kon.shol.searchengine.parser;

import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Parser {
    private PageData pageData;

    private ArrayList<String> extractLinks(Document doc) {
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

    public void parse(Document doc) throws IOException {
        pageData = new PageData();

        //TODO: isValid Function and Throw Exceptions

        pageData.setLinks(extractLinks(doc));
        pageData.setAnchors(extractAnchors(doc));
        pageData.setText(doc.text());
        pageData.setTitle(doc.title());
        pageData.setDescription(doc.select("meta[name=description]").attr("content"));
        pageData.setImagesAlt(String.join(" ", doc.select("img").eachAttr("alt")));
        pageData.setH1h3(doc.select("h1,h2,h3").text());
        pageData.setH4h6(doc.select("h4,h5,h6").text());
    }

    private String trimLink(Element link) {
        try {
            String temp = link.attr("abs:href");
            if (temp.charAt(temp.length() - 1) != '/') {
                temp += "/";
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

    private boolean isValid(Document document){
        //TODO: Implement This Function
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

    public String reverseDomain(String url) {
        try {
            if (url.charAt(url.length() - 1) != '/') {
                url += "/";
            }
            List<String> domainArray = Arrays.asList(InternetDomainName.from(new URL(url).getHost()).name().split("\\."));
            Collections.reverse(domainArray);
            return (String.join(".", domainArray)) + url.substring(StringUtils.ordinalIndexOf(url, "/", 3));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PageData getPageData() {
        return pageData;
    }

}
