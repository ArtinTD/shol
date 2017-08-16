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
import java.util.List;
import java.util.NavigableMap;

public class HBase {
    private static Connection connection;
    private List<Put> putList;
    private Table table;

    // Connect to DB
    private Connection connect(String zooKeeperIp) throws IOException {
        System.out.println("Connecting to Hbase");
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", zooKeeperIp);
        connection = ConnectionFactory.createConnection(configuration);
        System.out.println("Connection to Hbase established");
        return connection;
    }

    // Constructor
    public HBase(String zooKeeperIp) throws IOException {
        putList = new ArrayList<>();
        try {
            if (connection.isClosed()) {
                connection = connect(zooKeeperIp);
            }else{
                System.out.println("There is a valid connection");
            }
        } catch (NullPointerException e) {
            System.out.println("Initiating Hbase Connection");
            connection = connect(zooKeeperIp);
        }
    }

    // Getter And Setter
    public Connection getConnection() {
        return connection;
    }

    public void setConnection(String zooKeeperIp) throws IOException {
        if (connection.isClosed())
            connection = connect(zooKeeperIp);
        else {
            System.out.println("There is an open connection now!");
        }
    }

    public Table getTable() {
        return table;
    }

    public void setTable(String tableNameStr) throws IOException {
        TableName tableName = TableName.valueOf(tableNameStr);
        table = connection.getTable(tableName);
    }

    // Put Methods
    public void put(String rowKey, String cf, String iden, String val) throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(iden), Bytes.toBytes(val));
        table.put(put);
        System.out.println("Put in row : " + rowKey );
    }

    public void put(String rowKey, String cf, String iden, ArrayList<String> stringArrayList) throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(iden), WritableUtils.toByteArray(toWritable(stringArrayList)));
        table.put(put);
        System.out.println("Put in row : " + rowKey );
    }

    public void batchPut(String rowKey, String cf, String iden, String val) throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(iden), Bytes.toBytes(val));
        putList.add(put);
        if (putList.size() > 50) {
            table.put(putList);
            putList.clear();
        }
    }

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
    private Writable toWritable(ArrayList<String> list) {
        Writable[] content = new Writable[list.size()];
        for (int i = 0; i < content.length; i++) {
            content[i] = new Text(list.get(i));
        }
        return new ArrayWritable(Text.class, content);
    }

    private ArrayList<String> fromWritable(ArrayWritable writable) {
        Writable[] writables = ((ArrayWritable) writable).get();
        ArrayList<String> list = new ArrayList<String>(writables.length);
        for (Writable wrt : writables) {
            list.add(((Text) wrt).toString());
        }
        return list;
    }

    public ArrayList<String> castResultToStringArrayList(Result r, String columnFamily, String cell) throws IOException {
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
    private static String[] castResultToStringArray(Result r, String columnFamily, String cell) {
        String DBOutput = Bytes.toString(r.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(cell)));
        Object[] DBOutputArrays = Arrays.stream(DBOutput.substring(1, DBOutput.length() - 1).split(","))
                .map(String::trim).toArray();
        return Arrays.copyOf(DBOutputArrays, DBOutputArrays.length, String[].class);
    }
}
