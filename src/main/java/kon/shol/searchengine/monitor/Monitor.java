package kon.shol.searchengine.monitor;

import kon.shol.searchengine.crawler.Crawler;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public class Monitor implements Runnable {

    private int speed = 0;
    private int sum = 0;
    private int cycles = 0;
    private ArrayList<Crawler> crawlers = new ArrayList<>();
    private final static Logger logger = Logger.getLogger("custom");

    static int numberOfFetchedLinksFromQueueToCrawl;
    static int numberOfPoliteDomains;
    static int numberOfenglishLinks;
    static int numberOfCrawledLinks;
    static int numberOfActiveThreads;
    static int allLinkeCrawled;

    public void addCrawler(Crawler crawler) {
        crawlers.add(crawler);
    }

    @Override
    public void run() {
        try {
            Thread.sleep(15000);
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
                crawler.resetNumCycle();
            }
            sum += speed;
            cycles += 1;
            System.out.println("");
            logger.info("Crawled: " + speed);
            logger.info("Average: " + sum/cycles);
            logger.info("Sum: " + sum);
            System.out.println("");
            speed = 0;
        }
    }
}
