package kon.shol.searchengine.crawler;

public interface Queue {

    String getUrl();
    void send(Object element);

}
