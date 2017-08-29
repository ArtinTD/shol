package kon.shol.searchengine.hbase;

import kon.shol.searchengine.serializer.Deserializer;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.apache.log4j.Logger;

import java.io.IOException;

import static kon.shol.searchengine.hbase.Connector.connection;

public class Reader {
    private Table table;
    private Deserializer deserializer = new Deserializer();
    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.hbase.Reader.class);

    public Reader(String tableNameStr) throws IOException {
        TableName tableName = TableName.valueOf(tableNameStr);
        table = connection.getTable(tableName);
    }

}
