package kon.shol.searchengine.crawler;

import java.util.concurrent.ExecutionException;

public interface Cache {
    void insert(Object element) throws ExecutionException;
    boolean exists(Object element);
    long size();
}
