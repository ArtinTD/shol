package kon.shol.searchengine.crawler;

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

    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.crawler.Crawler.class);

    Crawler(Queue queue, Cache cache, Fetcher fetcher, Parser parser, Storage storage) {

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

            String url = queue.getUrl();
            try {
                String domain = parser.getDomain(url);
                if (cache.exists(domain)) {
                    queue.send(Collections.singletonList(url));
                    logger.error("Already in cache: " + url);
                    continue;
                }
                if (storage.exists(url)) {
                    logger.error("already exists in storage: " + url);
                    continue;
                }
                cache.insert(domain);
            } catch (Exception e) {
                logger.error("Bad link : " + url);
                continue;
            }
            Document document;
            try {
                document = fetcher.fetch(url);
            } catch (Exception e) {

                //TODO: Handle Exceptions
                continue;
            }
            try {
                try {
                    parser.parse(document);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (InvalidLanguageException | EmptyDocumentException parseException) {
                logger.error(parseException.getMessage());
                continue;
            }
            storage.sendToStorage(parser.getPageData());
            queue.send(parser.getPageData().getLinks());
        }
    }
}

