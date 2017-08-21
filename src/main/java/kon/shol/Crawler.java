package kon.shol;

import java.util.ArrayList;
import static kon.shol.LRU.lruCache;
import static kon.shol.Parser.extractLinks;
import static kon.shol.Parser.getDomain;

public abstract class Crawler implements Runnable, Queue{

    public void run() {

        while (true) {

            Fetcher fetcher = new Fetcher();

            do {

                fetcher.page.link = getLink();
                String link = fetcher.page.link;

                try {
                    while (lruCache.getIfPresent(getDomain(link)) != null) {
                        System.err.println("Already in cache: " + fetcher.page.link);
                        sendLink(link);
                        fetcher.page.link = getLink();
                        link = fetcher.page.link;
                    }
                    lruCache.get(getDomain(link));

                } catch (Exception ignore) { }

            }

            while (!fetcher.setHTML());

            for (String link : fetcher.page.pageData.links) { sendLink(link); }
        }
    }
}
