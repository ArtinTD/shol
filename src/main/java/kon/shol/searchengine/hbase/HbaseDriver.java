package kon.shol.searchengine.hbase;

import kon.shol.searchengine.crawler.Storage;
import kon.shol.searchengine.parser.PageData;

import java.io.IOException;
import java.util.List;

import static kon.shol.searchengine.hbase.Connector.connection;

public class HbaseDriver implements Storage {
    private Writer writer;
    private Reader reader;

    public HbaseDriver (String tableName, int batchPutSize) throws IOException {
        if (connection.isClosed()){
            new Connector();
        }
        writer = new Writer(tableName, batchPutSize);
        reader = new Reader(tableName);
    }

    @Override
    public void sendToStorage(Object element) {
        writer.addToPutList((PageData) element);
    }

    @Override
    public boolean exists(String url) throws IOException {
        return reader.exists(url);
    }

    @Override
    public List<String> removeExisting(Object[] messages) throws IOException {
        return reader.removeExisting(messages);
    }
}
