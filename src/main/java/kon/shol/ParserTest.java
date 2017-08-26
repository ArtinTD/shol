package kon.shol;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("http://digikala.com").get();
        Parser.extractAnchors(document);

    }
}
