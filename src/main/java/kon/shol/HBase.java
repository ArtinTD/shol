package kon.shol;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;

import java.io.IOException;
import java.util.NavigableMap;

public class HBase {

//    COMMANDS FOR CREATING TABLE

//        HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
 /*       hTableDescriptor.addFamily(new HColumnDescriptor("Id"));
        hTableDescriptor.addFamily(new HColumnDescriptor("Name"));*/
//        admin.createTable(hTableDescriptor);

    private static String[] getColumnsInColumnFamily(Result r, String ColumnFamily) {

        NavigableMap<byte[], byte[]> familyMap = r.getFamilyMap(Bytes.toBytes(ColumnFamily));
        String[] Quantifers = new String[familyMap.size()];

        int counter = 0;
        for (byte[] bQunitifer : familyMap.keySet()) {
            Quantifers[counter++] = Bytes.toString(bQunitifer);

        }

        return Quantifers;
    }

    public static void main(String[] args) throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(configuration);
        Admin admin = connection.getAdmin();
        System.out.println("Connecting...");
        TableName tableName = TableName.valueOf("myTable");
        Table table = connection.getTable(tableName);
        Put put = new Put(Bytes.toBytes("myRow3"));
        put.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("Aida"), Bytes.toBytes(102900));
        Get get = new Get(Bytes.toBytes("myRow2"));
        Scan scan = new Scan();
        System.out.println(table.get(get));
        System.out.println(table.getScanner(scan).toString());
        System.out.println("Creating Table...");
        Result result = table.get(get);
        System.out.println(result);
//        table.put(put);
        System.out.println("Done!");
        System.out.println(getColumnsInColumnFamily(result, "Name")[0]);
        table.close();
        connection.close();
    }
}
