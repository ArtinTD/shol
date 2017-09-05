package kon.shol.searchengine;

import kon.shol.searchengine.crawler.*;
import kon.shol.searchengine.hbase.Connector;
import kon.shol.searchengine.hbase.HbaseDriver;
import kon.shol.searchengine.kafka.PreAnalysisQueue;
import kon.shol.searchengine.monitor.Monitor;
import kon.shol.searchengine.parser.Parser;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private final static Logger logger = Logger.getLogger("custom");
    public static ArrayBlockingQueue<Document> documentsQueue = new ArrayBlockingQueue<Document>(10000);

    private static final int priority = 3;
    private static final int numThreads = 500;

    public static void main(String[] args) {

        Queue preAnalysisQueue;
        Cache lruCache;
        Fetcher fetcher;
        Parser parser;
        Storage hBase = null;
        try {
            new Connector();
        } catch (IOException e) {
            logger.fatal("Couldn't Connect to Hbase, Fatal Error");
            System.exit(0);
        }
        ExecutorService executor = Executors.newFixedThreadPool(510);
        preAnalysisQueue = new PreAnalysisQueue();
        lruCache = new LruCache();

        Monitor monitor = new Monitor();
        for (int i = 0; i < numThreads; i++) {
            try {
                hBase = new HbaseDriver("amghezi");
            } catch (IOException e) {
                logger.fatal("Can't create HbaseDriver");
            }

            if (i % priority != 0) {
                parser = new Parser();
                fetcher = new Fetcher();
                PreAnalysis preAnalysis = new PreAnalysis(preAnalysisQueue, documentsQueue, lruCache, fetcher, parser, hBase);
                monitor.addPreAnalysis(preAnalysis);
                executor.execute(preAnalysis);
            }
            else{
                parser = new Parser();
                Analysis analysis =  new Analysis(preAnalysisQueue, documentsQueue, hBase, parser);
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
