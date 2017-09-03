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

public class Crawler implements Runnable {

    private Queue queue;
    private Cache cache;
    private Fetcher fetcher;
    private Parser parser;
    private Storage storage;
    private int numCycle = 0;

    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.crawler.Crawler.class);

    public Crawler(Queue queue, Cache cache, Fetcher fetcher, Parser parser, Storage storage) {

        this.queue = queue;
        this.cache = cache;
        this.fetcher = fetcher;
        this.parser = parser;
        this.storage = storage;

    }

    public synchronized int getNumCycle(){
        return numCycle;
    }

    public synchronized void resetNumCycle(){
        numCycle = 0;
    }

    @Override
    public void run() {
        //TODO: Logger error
        while (!Thread.currentThread().isInterrupted()) {

            String url;
            try {
                url = queue.get();
                logger.error(url);
            } catch (InterruptedException interruptedException) {
/*                logger.error("Interruption while getting from CrawlerQueue:\n " +
                        interruptedException.getMessage());*/
                continue;
            }
            try {
                String domain = parser.getDomain(url);
                if (cache.exists(domain)) {
                    queue.send(url);
//                    logger.error("Already in cache: " + url);
                    continue;
                }
                if (storage.exists(url)) {
//                    logger.error("already exists in storage: " + url);
                    continue;
                }
                cache.insert(domain);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Document document;
            try {
                document = fetcher.fetch(url);
            } catch (IOException exception) {
//                logger.error("Error fetching " + url + ": " + exception.getMessage());
                continue;
            }
            try {
                try {
                    parser.parse(document);
                } catch (IOException exception) {
//                    logger.error("Error parsing " + url + ": " + exception.getMessage());
                }
            } catch (InvalidLanguageException | EmptyDocumentException parseException) {
//                logger.error(parseException.getMessage());
                continue;
            }
//           TODO: Let's Move the Parse section  to other threads. Cuz pageData is too heavy to go through Kafka, Should be Asked from the Mentors
            storage.sendToStorage(parser.getPageData());
            queue.send(parser.getPageData().getAnchors());
            numCycle++;
        }
    }
}

