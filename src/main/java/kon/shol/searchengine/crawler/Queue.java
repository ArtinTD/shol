package kon.shol.searchengine.crawler;

import java.io.IOException;
import java.util.List;

public interface Queue {

    Object get() throws InterruptedException;
    void send(String message);
    void send(Object[] messages) throws IOException;
}
