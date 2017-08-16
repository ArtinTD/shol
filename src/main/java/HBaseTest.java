import kon.shol.HBase;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HBaseTest {
    public static void main(String[] args) throws IOException {
        HBase hBase = new HBase("188.165.230.122:2181");
        Connection connection = hBase.getConnection();
        hBase.setTable("sitesData");
        Table table = hBase.getTable();

        table.close();
        connection.close();
    }
}
