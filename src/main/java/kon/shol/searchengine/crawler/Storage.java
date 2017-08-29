package kon.shol.searchengine.crawler;

public interface Storage {

    void sendToStorage(Object element);
    boolean exists(String url);

}
