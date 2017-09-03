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
    private LangDetector languageDetector = new LangDetector();

    public void parse(Document doc) throws IOException {
        if (isValid(doc)) {
            pageData = new PageData();
            pageData.setAnchors(extractAnchors(doc));
            pageData.setUrl(doc.location());
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
            if (absHref.contains("http"))
                return absHref;
            else{
                return null;
            }

        } catch (StringIndexOutOfBoundsException ignore) {
            return null;
        }
    }

    public String getDomain(String inputUrl) throws MalformedURLException, IllegalArgumentException {
        URL url = new URL(inputUrl);
        return InternetDomainName.from(url.getHost()).topPrivateDomain().toString();
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
        HashMap<String, String> anchors = new HashMap<>();
        Elements urls;
        urls = doc.select("a");
        for (Element url : urls) {
            String urlRef = trimLink(url);
            if (urlRef == null) {
                continue;
            }
            String urlText = url.text()
                    .replaceAll("here", "")
                    .replaceAll("link", "")
                    .replaceAll("click", "")
                    .replaceAll("next", "")
                    .replaceAll("more", "")
                    .replaceAll("show", "")
                    .replaceAll("visit", "")
                    .replaceAll("website", "")
                    .replaceAll("[0-9]", "");
            anchors.put(urlRef, urlText);
        }
        return anchors;
    }

    public String reverseDomain(String url) {
        try {
            if (url.charAt(url.length() - 1) != '/') {
                url += "/";
            }
            List<String> domainArray = new LinkedList<>(Arrays.asList(InternetDomainName.from(
                    new URL(url).getHost()).toString().split("\\.")));
            if (domainArray.get(0).equals("www")) {
                domainArray.remove(0);
            }
            Collections.reverse(domainArray);
            return (String.join(".", domainArray)) + url.substring(
                    StringUtils.ordinalIndexOf(url, "/", 3));
        } catch (MalformedURLException | IllegalArgumentException e) {
            if (e instanceof IllegalArgumentException) {
                return url;
            }
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
