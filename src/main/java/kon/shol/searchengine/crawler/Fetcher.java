package kon.shol.searchengine.crawler;

import kon.shol.searchengine.crawler.exceptions.InvalidStatusCodeException;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Fetcher {

    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.crawler.Fetcher.class);

    public Document fetch(String url) throws IOException {
        /*Connection connection = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21");
//                .timeout(3000);
        Connection.Response response = connection.execute();
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            return connection.get();
        } else {
            throw new InvalidStatusCodeException(statusCode, url);
        }*/
        return Jsoup.connect(url)
                .timeout(3*1000)
                .followRedirects(true)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://google.com")
                .get();
    }
}
