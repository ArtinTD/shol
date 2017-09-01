package kon.shol.searchengine;

import kon.shol.searchengine.crawler.*;
import kon.shol.searchengine.hbase.Connector;
import kon.shol.searchengine.hbase.HbaseDriver;
import kon.shol.searchengine.hbase.Writer;
import kon.shol.searchengine.kafka.CrawlerQueue;
import kon.shol.searchengine.parser.Parser;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
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
        }

        ExecutorService executor = Executors.newFixedThreadPool(110);

        try {
            hBase = new HbaseDriver("testdb");
        } catch (IOException e) {
            logger.error("are dige...");
        }
        crawlerQueue = new CrawlerQueue();
        lruCache = new LruCache();
        for (int i = 0; i < 100; i++) {
            fetcher = new Fetcher();
            parser = new Parser();
            executor.execute(new Crawler(crawlerQueue, lruCache, fetcher, parser, hBase));
        }
        for (int i = 0; i < 10; i++) {
            try {
                hBaseWriter = new Writer("testdb");
            } catch (IOException e) {
                logger.error("chera aziat mikoni...");
            }
            executor.execute(hBaseWriter);
        }
    }
}
