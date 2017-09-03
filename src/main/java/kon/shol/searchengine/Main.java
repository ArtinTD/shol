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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static List<Put> putList = new CopyOnWriteArrayList<>();
    private final static Logger logger =
            Logger.getLogger(kon.shol.searchengine.Main.class);

    public static void main(String[] args) {
        Queue crawlerQueue;
        Cache lruCache;
        Fetcher fetcher;
        Parser parser;
        Storage hBase = null;
        Writer hBaseWriter = null;
        try {
            new Connector();
        } catch (IOException e) {
            logger.error("Couldn't Connect to Hbase, Fatal Error");
            System.exit(0);
        } catch (Exception e){

        }

        ExecutorService executor = Executors.newFixedThreadPool(510);
        Monitor monitor = new Monitor();
        crawlerQueue = new CrawlerQueue();
        lruCache = new LruCache();
        for (int i = 0; i < 500; i++) {
            try {
                hBase = new HbaseDriver("webpages");
            } catch (IOException e) {
                logger.error("are dige...");
            }
            fetcher = new Fetcher();
            parser = new Parser();
            Crawler temp = new Crawler(crawlerQueue, lruCache, fetcher, parser, hBase);
            monitor.addCrawler(temp);
            executor.execute(temp);
        }
        Thread t = new Thread(monitor);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       /* //TODO: Check Hbase Threads Behaviors
        for (int i = 0; i < 50; i++) {
            try {
                hBaseWriter = new Writer("testdb");
            } catch (IOException e) {
                logger.error("chera aziat mikoni...");
            }
            executor.execute(hBaseWriter);
        }*/
    }
}
