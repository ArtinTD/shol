package kon.shol.searchengine.crawler;

public interface Cache {
    void insert(Object element);
    boolean exists(Object element);
}
