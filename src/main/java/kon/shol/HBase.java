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
import java.util.Arrays;
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
    private static String[] castResultToStringArray(Result r, String columnFamily, String cell){
        String DBOutput = Bytes.toString(r.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(cell)));
        Object[] DBOutputArrays = Arrays.stream(DBOutput.substring(1, DBOutput.length() - 1).split(","))
                .map(String::trim).toArray();
        return Arrays.copyOf(DBOutputArrays, DBOutputArrays.length, String[].class);
    }

     /*   HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
        hTableDescriptor.addFamily(new HColumnDescriptor("Id"));
        hTableDescriptor.addFamily(new HColumnDescriptor("Name"));
        Admin admin = connection.getAdmin();
        admin.createTable(hTableDescriptor);*/

    public static void main(String[] args) throws IOException {
        TableName tableName = TableName.valueOf("myTable");
        String[] arr = {"sajjad", "asghar", "ahmad", "kazem"};
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "188.165.230.122:2181");
        Connection connection = ConnectionFactory.createConnection(configuration);
        System.out.println("Connecting...");

        Table table = connection.getTable(tableName);
        Put put = new Put(Bytes.toBytes("myRow4"));
        put.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("firstName"), Bytes.toBytes("Kambiz"));
        put.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("lastName"), Bytes.toBytes("Gholami"));
        put.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("friends"), Bytes.toBytes(Arrays.toString(arr)));
//        table.put(put);
        Get get = new Get(Bytes.toBytes("myRow4"));
        System.out.println(table.exists(get));
//        Scan scan = new Scan();
        get.setMaxVersions(1);
        get.addFamily(Bytes.toBytes("Name"));

        get.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("friends"));

        Result result = table.get(get);
        String[] strArr = castResultToStringArray(result, "Name", "friends");
        for(String s: strArr){
            System.out.println(s);
        }
        System.out.println("Done!");

        byte[] databytes = Bytes.toBytes("Name");
//        System.out.println(getColumnsInColumnFamily(result, "Name")[0]);

        table.close();
        connection.close();
    }
}
