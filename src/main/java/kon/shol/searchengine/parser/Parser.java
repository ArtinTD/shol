package kon.shol.searchengine.parser;

import com.google.common.net.InternetDomainName;
import com.google.gson.Gson;
import kon.shol.searchengine.parser.exceptions.EmptyDocumentException;
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
    private LanguageDetector languageDetector = new LanguageDetector();

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
        if (isValid(doc)) {
            pageData = new PageData();
            pageData.setLinks(extractLinks(doc));
            pageData.setAnchors(extractAnchors(doc));
            pageData.setText(doc.text());
            pageData.setTitle(doc.title());
            pageData.setUrl(doc.location());
            pageData.setDescription(doc.select("meta[name=description]")
                    .attr("content"));
            pageData.setImagesAlt(String.join(" ", doc.select("img")
                    .eachAttr("alt")));
            pageData.setH1h3(doc.select("h1,h2,h3").text());
            pageData.setH4h6(doc.select("h4,h5,h6").text());
        }
    }

    private String trimLink(Element link) {
        try {
            // TODO: Some Links do not Response whit / at the end! Think about it
            String absHref = link.attr("abs:href");
            if (absHref.charAt(absHref.length() - 1) != '/') {
                absHref += "/";
            }
            return absHref;

        } catch (StringIndexOutOfBoundsException ignore) {
            return null;
        }
    }

    public String getDomain(String link) throws MalformedURLException, IllegalArgumentException {
        URL url = new URL(link);
        return InternetDomainName.from(url.getHost()).topPrivateDomain().name();
    }

    private boolean isValid(Document document) throws IOException {
        if (!document.hasText())
            throw new EmptyDocumentException("Empty Document: " + document.location());
        else if (languageDetector.isEnglish(document)) {
            return true;
        }
        return false;
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
            List<String> domainArray = Arrays.asList(InternetDomainName.from(
                    new URL(url).getHost()).name().split("\\."));
            Collections.reverse(domainArray);
            return (String.join(".", domainArray)) + url.substring(
                    StringUtils.ordinalIndexOf(url, "/", 3));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PageData getPageData() {
        return pageData;
    }

    public <T> T deserialize(String payload, Class<T> tClass) {
        return new Gson().fromJson(payload, tClass);
    }

    public String serialize(Object payload) {
        return new Gson().toJson(payload);
    }

}
