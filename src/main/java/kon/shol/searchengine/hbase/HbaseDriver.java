package kon.shol.searchengine.hbase;

import kon.shol.searchengine.crawler.Storage;
import kon.shol.searchengine.kafka.HbaseQueue;
import kon.shol.searchengine.parser.PageData;
import kon.shol.searchengine.parser.Parser;
import kon.shol.searchengine.serializer.Deserializer;

import java.io.IOException;

import static kon.shol.searchengine.hbase.Connector.connection;

public class HbaseDriver implements Storage {
    private Writer writer;
    private Reader reader;
    private HbaseQueue hbaseQueue;

    public HbaseDriver (String tableName) throws IOException {
        if (connection.isClosed()){
            new Connector();
        }
        writer = new Writer(tableName);
        reader = new Reader(tableName);
        hbaseQueue = new HbaseQueue();
    }
    @Override
    public void sendToStorage(Object element) {
        //TODO: Call Kafka
    }

    @Override
    public boolean exists(String url) throws IOException {
        return reader.exists(url);
    }


}
