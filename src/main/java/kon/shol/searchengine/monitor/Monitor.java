package kon.shol.searchengine.monitor;

import kon.shol.searchengine.crawler.Cache;
import kon.shol.searchengine.crawler.Crawler;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Monitor implements Runnable {

    int speed;
    private Cache cache;
    private ArrayList<Crawler> crawlers = new ArrayList<>();
    private final static Logger logger = Logger.getLogger("custom");

    static int numberOfFetchedLinksFromQueueToCrawl;
    static int numberOfPoliteDomains;
    static int numberOfenglishLinks;
    static int numberOfCrawledLinks;
    static int numberOfActiveThreads;
    static int allLinkeCrawled;
    public Monitor(Cache cache){
        this.cache = cache;
    }

    public void addCrawler(Crawler temp) {
        crawlers.add(temp);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.fatal("Monitor thread interrupted while sleeping");
            }
            for (Crawler crawler : crawlers) {
                speed += crawler.getNumCycle();
                crawler.resetNumCycle();
            }

            System.out.println("Rate of crawlers : " + speed);
            System.out.println("Size of LRU : " + cache.size());
            speed = 0;
        }
    }
}
