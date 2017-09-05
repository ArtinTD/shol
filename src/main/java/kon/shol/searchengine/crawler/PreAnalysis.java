package kon.shol.searchengine.crawler;

import kon.shol.searchengine.parser.Parser;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;

public class PreAnalysis implements Runnable {

    private ArrayBlockingQueue documentsQueue;
    private Queue kafkaQueue;
    private Cache cache;
    private Fetcher fetcher;
    private Parser parser;
    private Storage storage;

    private int invalidUrls = 0;
    private int fetchErrors = 0;

    private final static Logger logger = Logger.getLogger("custom");

    public PreAnalysis(Queue kafkaQueue, ArrayBlockingQueue documentsQueue, Cache cache, Fetcher fetcher, Parser parser, Storage storage) {

        this.kafkaQueue = kafkaQueue;
        this.documentsQueue = documentsQueue;
        this.fetcher = fetcher;
        this.cache = cache;
        this.parser = parser;
        this.storage = storage;

    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String url;
            try {
                url = (String) kafkaQueue.get();
            } catch (InterruptedException interruptedException) {
                logger.fatal("Interruption while  getting from queue");
                continue;
            }

            String domain = null;
            try {
                domain = parser.getDomain(url);
            } catch (MalformedURLException e) {
                logger.debug("Malformed: " + url);
                invalidUrls++;
                continue;
            } catch (IllegalArgumentException | IllegalStateException e) {
                logger.debug("Domain name not valid: " + url);
                invalidUrls++;
                continue;
            }

            if (cache.exists(domain)) {
                kafkaQueue.send(url);
                continue;
            }

            try {
                if (storage.exists(parser.reverseDomain(url))) {
                    continue;
                }
            } catch (IOException e) {
                logger.fatal("Can't check existence from storage: " + url);
                continue;
            }

            try {
                cache.insert(domain);
            } catch (ExecutionException e) {
                logger.fatal("Can't insert to cache: " + domain);
                continue;
            }

            Document document = null;
            try {
                if (url != null)
                document = fetcher.fetch(url);
            } catch (IOException exception) {
                logger.debug("Error fetching: " + url);
                fetchErrors++;
                continue;
            }
            if (document != null)
                try {
                    documentsQueue.put(document);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


        }
    }

    public int getFetchErrors() {
        return fetchErrors;
    }


    public int getInvalidUrls() {
        return invalidUrls;
    }


}
