package kon.shol;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect("https://research.google.com/archive/bigtable-osdi06.pdf").get();

    }
}
