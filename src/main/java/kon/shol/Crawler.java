package kon.shol;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class Crawler implements Runnable, Kafka {

    public void run() {
        while (true) {
            Fetcher fetcher = new Fetcher();
            do {
                fetcher.page.link = getLink();
            }
            while (!fetcher.getHtml());
            Elements links = Parser.extractLinks(fetcher.page.html);
            for (Element link: links) {
                sendLink(Parser.trimLink(link));
            }

        }
    }
}
