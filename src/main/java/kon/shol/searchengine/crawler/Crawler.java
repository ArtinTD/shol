package kon.shol.searchengine.crawler;

import kon.shol.searchengine.crawler.exceptions.InvalidStatusCodeException;
import kon.shol.searchengine.parser.Parser;
import kon.shol.searchengine.parser.exceptions.EmptyDocumentException;
import kon.shol.searchengine.parser.exceptions.InvalidLanguageException;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class Crawler implements Runnable {

    private Queue queue;
    private Cache cache;
    private Fetcher fetcher;
    private Parser parser;
    private Storage storage;
    private int numCycle = 0;
    private int invalidUrl = 0;



    private final static Logger logger = Logger.getLogger("custom");

    public Crawler(Queue queue, Cache cache, Fetcher fetcher, Parser parser, Storage storage) {

        this.queue = queue;
        this.cache = cache;
        this.fetcher = fetcher;
        this.parser = parser;
        this.storage = storage;

    }

    @Override
    public void run() {
        //TODO: Logger error
        while (!Thread.currentThread().isInterrupted()) {

            String url;
            try {
                url = queue.get();
            } catch (InterruptedException interruptedException) {
                logger.fatal("Interruption while  getting from queue");
                continue;
            }
            String domain = null;
            try {
                domain = parser.getDomain(url);
            } catch (MalformedURLException e) {
                logger.debug("Malformed: " + url);
                invalidUrl++;
                continue;
            } catch (IllegalArgumentException | IllegalStateException e) {
                logger.debug("Domain name not valid: " + url);
                invalidUrl++;
                continue;
            }
            try {
                if (storage.exists(url)) {
                    continue;
                }
            } catch (IOException e) {
                logger.fatal("Can't check existence from storage: " + url);
                e.printStackTrace();
            }
            if (cache.exists(domain)) {
                queue.send(url);
                continue;
            }
            try {
                cache.insert(domain);
            } catch (ExecutionException e) {
                logger.fatal("Can't insert to cache: " + domain);
            }
            Document document;
            try {
                document = fetcher.fetch(url);
            } catch (IOException exception) {
                logger.debug("Error fetching: " + url);
                continue;
            }
            try {
                parser.parse(document);
            } catch (IOException | EmptyDocumentException exception) {
                    logger.debug("Error parsing " + url + ": " + exception.getMessage());
            }
            storage.sendToStorage(parser.getPageData());
            queue.send(parser.getPageData().getAnchors());
            numCycle++;
        }
    }

    public synchronized int getNumCycle(){
        return numCycle;
    }

    public synchronized void resetNumCycle(){
        numCycle = 0;
    }
}

