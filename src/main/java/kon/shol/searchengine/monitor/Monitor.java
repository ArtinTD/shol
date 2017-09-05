package kon.shol.searchengine.monitor;

import kon.shol.searchengine.crawler.Analysis;
import kon.shol.searchengine.crawler.Cache;
import kon.shol.searchengine.crawler.Crawler;
import kon.shol.searchengine.crawler.Queue;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
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
    private ArrayList<Analysis> analyses =new ArrayList<>();
    private final static Logger logger = Logger.getLogger("custom");


    public void addCrawler(Crawler crawler) {
        crawlers.add(crawler);
    }
    public void addAnalysis(Analysis analysis){ analyses.add(analysis);}

    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.fatal("Monitor thread interrupted while sleeping");
            }
            for (Analysis analysis : analyses) {
                speed += analysis.getNumCycle();
                analysis.resetNumCycle();
            }
            sum += speed;
            cycles += 1;
            System.out.println("");
            logger.info("Crawl Speed: " + speed);
            logger.info("Average Crawl Speed: " + sum/cycles);
            logger.info("Total Crawls: " + sum);
            speed = 0;
        /*    logger.info("Total Fetch Errors: " + fetchErrors);
            logger.info("Total Parse Errors: " + parseErrors);
            logger.info("Total Invalid Urls: " + invalidUrls);
            System.out.println("");

            speed = 0;
            parseErrors = 0;
            invalidUrls = 0;
            fetchErrors = 0;*/


        }
    }
}
