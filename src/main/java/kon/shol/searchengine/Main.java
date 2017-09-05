package kon.shol.searchengine;

import kon.shol.searchengine.crawler.*;
import kon.shol.searchengine.hbase.Connector;
import kon.shol.searchengine.hbase.HbaseDriver;
import kon.shol.searchengine.hbase.Writer;
import kon.shol.searchengine.kafka.CrawlerQueue;
import kon.shol.searchengine.monitor.Monitor;
import kon.shol.searchengine.parser.Parser;
import org.apache.hadoop.hbase.client.Put;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private final static Logger logger = Logger.getLogger("custom");
    public static ArrayBlockingQueue<Document> documentsQueue = new ArrayBlockingQueue<Document>(10000);

    public static void main(String[] args) {

        Queue crawlerQueue;
        Queue analysisQueue;
        Cache lruCache;
        Fetcher fetcher;
        Parser parser;
        Storage hBase = null;
        Writer hBaseWriter = null;
        try {
            new Connector();
        } catch (IOException e) {
            logger.fatal("Couldn't Connect to Hbase, Fatal Error");
            System.exit(0);
        }
        ExecutorService executor = Executors.newFixedThreadPool(510);
        crawlerQueue = new CrawlerQueue();
        lruCache = new LruCache();

        Monitor monitor = new Monitor();
        for (int i = 0; i < 500; i++) {
            try {
                hBase = new HbaseDriver("amghezi");
            } catch (IOException e) {
                logger.fatal("Can't create HbaseDriver");
            }

            if (i % 3 != 0) {
                parser = new Parser();
                fetcher = new Fetcher();
                Crawler temp = new Crawler(crawlerQueue, documentsQueue, lruCache, fetcher, parser, hBase);
                monitor.addCrawler(temp);
                executor.execute(temp);
            }
            else{
                parser = new Parser();
                Analysis analysis =  new Analysis(crawlerQueue,documentsQueue, hBase, parser);
                monitor.addAnalysis(analysis);
                executor.execute(analysis);
            }
        }
        Thread t = new Thread(monitor);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            logger.fatal("Couldn't join thread");
        }
    }
}
