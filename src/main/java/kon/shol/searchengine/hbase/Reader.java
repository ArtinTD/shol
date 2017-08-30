package kon.shol.searchengine.hbase;

import kon.shol.searchengine.parser.Parser;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;

import static kon.shol.searchengine.hbase.Connector.connection;

public class Reader {
    private Table table;
    private Parser parser;
    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.hbase.Reader.class);

    public Reader(String tableNameStr) throws IOException {
        if (connection.isClosed()) {
            new Connector();
        }
        TableName tableName = TableName.valueOf(tableNameStr);
        table = connection.getTable(tableName);
        parser = new Parser();
    }

    public boolean exists(String rowKey) throws IOException {
        return table.exists(new Get(Bytes.toBytes(parser.reverseDomain(rowKey))));
    }

}
