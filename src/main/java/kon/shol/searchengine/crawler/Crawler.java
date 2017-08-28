package kon.shol.searchengine.crawler;

import kon.shol.searchengine.parser.Parser;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public abstract class Crawler implements Runnable{

    private Queue queue;
    private LruCache lruCache;
    private Fetcher fetcher;
    private Parser parser;
    private Storage storage;

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
        }
    }
}
