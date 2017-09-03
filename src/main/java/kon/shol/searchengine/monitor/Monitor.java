package kon.shol.searchengine.monitor;

import kon.shol.searchengine.crawler.Crawler;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Monitor implements Runnable {

    int speed;
    private ArrayList<Crawler> crawlers = new ArrayList<>();
    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.monitor.Monitor.class);

    static int numberOfFetchedLinksFromQueueToCrawl;
    static int numberOfPoliteDomains;
    static int numberOfenglishLinks;
    static int numberOfCrawledLinks;
    static int numberOfActiveThreads;
    static int allLinkeCrawled;

    public void addCrawler(Crawler temp) {
        crawlers.add(temp);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Monitor thread interrupted while sleeping");
            }
            for (Crawler crawler : crawlers) {
                speed += crawler.getNumCycle();
                crawler.resetNumCycle();
            }
            System.out.println("Rate of crawlers : " + speed);
            speed = 0;
        }
    }
}
