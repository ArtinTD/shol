package kon.shol.searchengine.crawler;

import kon.shol.searchengine.parser.Parser;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.util.Collections;

public abstract class Crawler implements Runnable {

    private Queue queue;
    private Cache cache;
    private Fetcher fetcher;
    private Parser parser;
    private Storage storage;

    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.crawler.Crawler.class);

    public void crawler(Queue queue, Cache cache, Fetcher fetcher, Parser parser, Storage storage) {

        this.queue = queue;
        this.cache = cache;
        this.fetcher = fetcher;
        this.parser = parser;
        this.storage = storage;

    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {
            String url = queue.getUrl();
            //TODO: get domain
            if (cache.exists(url)) {
                queue.send(Collections.singletonList(url));
                logger.error("Already in cache: " + url);
                continue;
            }
            if (storage.exists(url)) {
                logger.error("already exists in storage: " + url);
                continue;
            }
            cache.put(url);
            Document document;
            try {
                document = fetcher.fetch(url);
            } catch (Exception e) {
                //TODO: Handle Exceptions
                continue;
            }
            try {
                parser.parse(document);
            } catch (Exception e) {
                //TODO: Handle Exceptions
                continue;
            }
            storage.put(parser.getPageData());
            queue.send(parser.getPageData().getLinks());
        }
    }
}
