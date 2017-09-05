package kon.shol.searchengine.monitor;

import kon.shol.searchengine.crawler.Cache;
import kon.shol.searchengine.crawler.Crawler;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Monitor implements Runnable {

    private int speed = 0;
    private int sum = 0;
    private int cycles = 0;
    private int fetchErrors = 0;
    private int parseErrors = 0;
    private int invalidUrls = 0;
    private ArrayList<Crawler> crawlers = new ArrayList<>();
    private final static Logger logger = Logger.getLogger("custom");
    private Cache cache;

    static int numberOfFetchedLinksFromQueueToCrawl;
    static int numberOfPoliteDomains;
    static int numberOfenglishLinks;
    static int numberOfCrawledLinks;
    static int numberOfActiveThreads;
    static int allLinkeCrawled;

    /*public Monitor(Cache cache) {

        this.cache = cache;
    }*/

    public void addCrawler(Crawler crawler) {
        crawlers.add(crawler);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            logger.fatal("Monitor thread interrupted while sleeping");
        }
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.fatal("Monitor thread interrupted while sleeping");
            }
            for (Crawler crawler : crawlers) {
                speed += crawler.getNumCycle();
                parseErrors += crawler.getParseErrors();
                fetchErrors += crawler.getFetchErrors();
                invalidUrls += crawler.getInvalidUrls();
                crawler.resetNumCycle();
            }
            sum += speed;
            cycles += 1;
            System.out.println("");
            logger.info("Crawl Speed: " + speed);
            logger.info("Average Crawl Speed: " + sum/cycles);
            logger.info("Total Crawls: " + sum);
            logger.info("Cache Size: " + cache.size());
            logger.info("Total Fetch Errors: " + fetchErrors);
            logger.info("Total Parse Errors: " + parseErrors);
            logger.info("Total Invalid Urls: " + invalidUrls);
            System.out.println("");
            speed = 0;
            parseErrors = 0;
            invalidUrls = 0;
            fetchErrors = 0;
        }
    }
}
