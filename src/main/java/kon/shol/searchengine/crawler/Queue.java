package kon.shol.searchengine.crawler;

import org.jsoup.nodes.Document;

public interface Queue {

    Object get() throws InterruptedException;
    void send(Object messages);
}
