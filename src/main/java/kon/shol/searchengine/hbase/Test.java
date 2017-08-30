package kon.shol.searchengine.hbase;

import kon.shol.searchengine.parser.Parser;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        Connector connector = new Connector();
        Connection connection = connector.getConnection();
        Table table = connection.getTable(TableName.valueOf("demodb"));
        System.out.println("demodb");
        Scan scan = new Scan();
        ResultScanner resultScanner = table.getScanner(scan);
        for (Result r : resultScanner) {
            try {
                System.out.println(Bytes.toString(r.getRow()));
                System.out.println(parser.reverseDomain(Bytes.toString(r.getRow())));
            } catch (Exception e) {
                System.out.println("THERE IS AN EXCEPTION");
                e.printStackTrace();
            }
        }
    }
}
