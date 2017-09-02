package kon.shol.searchengine.monitor;

import kon.shol.searchengine.crawler.Crawler;

import java.util.ArrayList;

public class Monitor implements Runnable{

    int speed;
    private ArrayList<Crawler> crawlers;

    static int numberOfFetchedLinksFromQueueToCrawl;
    static int numberOfPoliteDomains;
    static int numberOfenglishLinks;
    static int numberOfCrawledLinks;
    static int numberOfActiveThreads;
    static int allLinkeCrawled;

    public void addCrawler(Crawler temp){
        crawlers.add(temp);
    }

    @Override
    public void run() {
        for (Crawler crawler: crawlers) {
            speed += crawler.getNumCycle();
            crawler.resetNumCycle();
        }
        System.out.println(speed);
        speed = 0;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
