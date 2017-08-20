package kon.shol;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("http://www.wikipedia.org").get();
        PageData pageData = Parser.parse(document);
        System.out.println(pageData.title);

    }
}
