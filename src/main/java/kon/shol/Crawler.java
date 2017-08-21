package kon.shol;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static kon.shol.LRU.lruCache;
import static kon.shol.Parser.extractLinks;
import static kon.shol.Parser.getDomain;

public abstract class Crawler implements Runnable, Kafka {

    public int numCycle = 0;

    public void run() {
        while (true) {
            Fetcher fetcher = new Fetcher();
            do {
                fetcher.page.link = getLink();
                System.err.println("back: " + fetcher.page.link);
                String link = fetcher.page.link;
                try {
                    while (lruCache.getIfPresent(getDomain(link)) != null) {
                        sendLink(link);
                        fetcher.page.link = getLink();
                        link = fetcher.page.link;
                    }

                    try {
                        lruCache.get(getDomain(link));

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                } catch (Exception ignore) {
                }
            }
            while (!fetcher.setHTML());
            System.out.println(fetcher.page.link);
            ArrayList<String> links = extractLinks(fetcher.page.html);
            for (String link : links) {
                sendLink(link);
            }
            numCycle++;
            System.out.println(numCycle);
        }
    }
}
