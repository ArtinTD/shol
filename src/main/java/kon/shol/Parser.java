package kon.shol;

import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Parser {

    static Elements extractLinks(Document doc){
        Elements links;
        links = doc.select("a");
        return links;
    }
    static String extractText(Document doc){

        return doc.body().text();
    }
    static String trimLink(Element link){
        return link.attr("abs:href");
    }
}
