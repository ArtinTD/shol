package kon.shol.searchengine.crawler;

public interface Cache {
    void put(Object element);
    boolean exists(Object element);
}
