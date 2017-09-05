package kon.shol.searchengine.crawler;

public interface Queue {

    Object get() throws InterruptedException;
    void send(Object messages);
}
