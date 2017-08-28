package kon.shol.searchengine.crawler;

import kon.shol.searchengine.parser.Parser;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import sun.plugin.dom.exception.InvalidStateException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public abstract class Crawler implements Runnable{

    private Queue queue;
    private LruCache lruCache;
    private Fetcher fetcher;
    private Parser parser;
    private Storage storage;
    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.crawler.Crawler.class);

    public void crawler(Queue queue, LruCache lruCache, Fetcher fetcher, Parser parser, Storage storage) {

        this.queue = queue;
        this.lruCache = lruCache;
        this.fetcher = fetcher;
        this.parser = parser;
        this.storage = storage;
    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) {

            String url = queue.getUrl();
            if (lruCache.getIfPresent(url)) {
                queue.send(Collections.singletonList(url));
                logger.error("Already in LruCache: " + url);
                continue;
            }
            if (storage.exists(url)) {
                logger.error("already exists in storage: " + url);
                continue;
            }
            lruCache.get(Url);
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
            storage.store(parser.getPageData());
            queue.send(parser.getPageData().getLinks());
        }
    }
}
