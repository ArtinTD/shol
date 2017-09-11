package kon.shol.searchengine.hbase;

import kon.shol.searchengine.parser.Parser;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static kon.shol.searchengine.hbase.Connector.connection;

public class Reader {
    private Table table;
    private Parser parser;
    private final static Logger logger = Logger.getLogger("custom");

    public Reader(String tableNameStr) throws IOException {
        if (connection.isClosed()) {
            new Connector();
        }
        TableName tableName = TableName.valueOf(tableNameStr);
        table = connection.getTable(tableName);
        parser = new Parser();
    }

    boolean exists(String rowKey) throws IOException {
        return table.exists(new Get(Bytes.toBytes(parser.reverseDomain(rowKey))));
    }

    ArrayList<String> removeExisting(Object[] rowKeys) throws IOException {

        ArrayList<Get> getsList = new ArrayList<>();
        for (Object rowKey : rowKeys) {
            getsList.add(new Get(Bytes.toBytes(parser.reverseDomain((String) rowKey))));
        }
        boolean[] existenceList = table.existsAll(getsList);
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < existenceList.length; i++) {
            if (!existenceList[i]) {
                result.add((String) rowKeys[i]);
            }
        }
        return result;
    }
}
