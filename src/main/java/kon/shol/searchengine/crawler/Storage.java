package kon.shol.searchengine.crawler;

import java.io.IOException;

public interface Storage {

    void sendToStorage(Object element);
    boolean exists(String url) throws IOException;

}
