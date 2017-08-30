package kon.shol.searchengine.hbase;

import kon.shol.searchengine.crawler.Storage;
import kon.shol.searchengine.kafka.HbaseQueue;

import java.io.IOException;

import static kon.shol.searchengine.hbase.Connector.connection;

public class HbaseDriver implements Storage {
    private Writer writer;
    private Reader reader;
    private HbaseQueue hBaseQueue;

    public HbaseDriver (String tableName) throws IOException {
        if (connection.isClosed()){
            new Connector();
        }
        writer = new Writer(tableName);
        reader = new Reader(tableName);
        hBaseQueue = new HbaseQueue();
    }
    @Override
    public void sendToStorage(Object element) {
        hBaseQueue.send(element);
    }

    @Override
    public boolean exists(String url) throws IOException {
        return reader.exists(url);
    }


}
