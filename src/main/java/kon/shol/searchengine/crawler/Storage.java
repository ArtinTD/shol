package kon.shol.searchengine.crawler;

import java.io.IOException;
import java.util.List;

public interface Storage {

    void sendToStorage(Object element);
    boolean exists(String url) throws IOException;
    List<String> removeExisting(Object[] messages) throws IOException;
}
