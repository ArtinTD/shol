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
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
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

//    THE COMMON WAY TO SERIALIZE LIST AND STORE TO HBASE
//    TODO: LEARN WRITABLE INTERFACE
    public static Writable toWritable(ArrayList<String> list) {
        Writable[] content = new Writable[list.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = new Text(list.get(i));
        }
        return new ArrayWritable(Text.class, content);
    }
    public static ArrayList<String> fromWritable(ArrayWritable writable) {
        Writable[] writables = ((ArrayWritable) writable).get();
        ArrayList<String> list = new ArrayList<String>(writables.length);
        for (Writable wrt : writables) {
            list.add(((Text)wrt).toString());
        }
        return list;
    }
    public static ArrayList<String> castResultToStringArrayList (Result r, String columnFamily, String cell) throws IOException {
        ArrayWritable arrayWritable = new ArrayWritable(Text.class);
        arrayWritable.readFields(
                new DataInputStream(
                        new ByteArrayInputStream(
                                r.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(cell))
                        )
                )
        );
        return fromWritable(arrayWritable);
    }

//    A ZAKHAR ALTERNATIVE FOR STORING STRING ARRAY
    private static String[] castResultToStringArray(Result r, String columnFamily, String cell){
        String DBOutput = Bytes.toString(r.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(cell)));
        Object[] DBOutputArrays = Arrays.stream(DBOutput.substring(1, DBOutput.length() - 1).split(","))
                .map(String::trim).toArray();
        return Arrays.copyOf(DBOutputArrays, DBOutputArrays.length, String[].class);
    }




    public static void main(String[] args) throws IOException {
        TableName tableName = TableName.valueOf("myTable");
        String[] arr = {"sajjad", "asghar", "ahmad", "kazem"};
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "188.165.230.122:2181");
        Connection connection = ConnectionFactory.createConnection(configuration);
        System.out.println("Connecting...");

        Table table = connection.getTable(tableName);
        Put put = new Put(Bytes.toBytes("myRow9"));
        put.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("firstName"), Bytes.toBytes("Steve"));
        put.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("lastName"), Bytes.toBytes("StevePoor"));
        put.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("friends"), WritableUtils.toByteArray(toWritable(new ArrayList<>(Arrays.asList(arr)))));
//        table.put(put);
        Get get = new Get(Bytes.toBytes("myRow9"));
//        CHECK IF A ROW EXISTS
        System.out.println(table.exists(get));
//        Scan scan = new Scan();
        get.setMaxVersions(1);
        get.addFamily(Bytes.toBytes("Name"));
        get.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("friends"));
        System.out.println("Done!");
        Result result = table.get(get);
        System.out.println(castResultToStringArrayList(result, "Name", "friends").get(0));
        table.close();
        connection.close();
    }
}
