package kon.shol;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static kon.shol.LRU.lruCache;
import static kon.shol.Parser.extractLinks;
import static kon.shol.Parser.getDomain;

public abstract class Crawler implements Runnable, Kafka {


    public void run() {
        while (true) {
            Fetcher fetcher = new Fetcher();
            do {
                fetcher.page.link = getLink();
                String link = fetcher.page.link;
                while (lruCache.getIfPresent(getDomain(link)) != null){
                    sendLink(link);
                    //System.err.println("Back" + ":" + link);
                    fetcher.page.link = getLink();
                    link = fetcher.page.link;
                }
                try {
                    lruCache.get(getDomain(link));

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            while (!fetcher.setHTML());
            System.out.println(fetcher.page.link);
            ArrayList<String> links = extractLinks(fetcher.page.html);
            for (String link: links) {
                    sendLink(link);
            }
        }
    }

}
