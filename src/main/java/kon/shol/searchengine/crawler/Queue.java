package kon.shol.searchengine.crawler;

public interface Queue {

    String get() throws InterruptedException;
    void send(Object element);

}
