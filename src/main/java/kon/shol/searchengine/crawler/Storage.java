package kon.shol.searchengine.crawler;

public interface Storage {

    void put(Object element);


    boolean exists(String url);
}
