package kon.shol;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

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
    public Table table;

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
            } else {
                System.out.println("There is a valid connection");
            }
        } catch (NullPointerException e) {
            System.out.println("Initiating Hbase Connection");
            connection = connect(zooKeeperIp);
        }
    }

    public HBase(String zooKeeperIp, String tableName) throws IOException {
        putList = new ArrayList<>();
        try {
            if (connection.isClosed()) {
                connection = connect(zooKeeperIp);
            } else {
                System.out.println("There is a valid connection");
            }
        } catch (NullPointerException e) {
            System.out.println("Initiating Hbase Connection");
            connection = connect(zooKeeperIp);
        } finally {
            setTable(tableName);
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
    // TODO: Specialize APIs as Mentioned in Structure
    public void put(String rowKey, String cf, String iden, String val) throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(iden), Bytes.toBytes(val));
        table.put(put);
        System.out.println("Put in row : " + rowKey);
    }

    public void put(String rowKey, String cf, String iden, ArrayList<String> stringArrayList) throws IOException {
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(iden), arrayListToByte(stringArrayList));
        table.put(put);
        System.out.println("Put in row : " + rowKey);
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

    public void putPageData(String url, PageData pageData) throws IOException {
        Put put = new Put(Bytes.toBytes(url));
        byte[] cfb = Bytes.toBytes("data");
        put.addColumn(cfb, Bytes.toBytes("title"), Bytes.toBytes(pageData.title));
        put.addColumn(cfb, Bytes.toBytes("description"), Bytes.toBytes(pageData.description));
        put.addColumn(cfb, Bytes.toBytes("text"), Bytes.toBytes(pageData.text));
        put.addColumn(cfb, Bytes.toBytes("h1h3"), arrayListToByte(pageData.h1h3));
        put.addColumn(cfb, Bytes.toBytes("h4h6"), arrayListToByte(pageData.h4h6));
        put.addColumn(cfb, Bytes.toBytes("links"), arrayListToByte(pageData.links));
        table.put(put);
    }

    // Get Methods
    // Just Implemented Get for ArrayList
    // TODO: Implement All get functions
    public ArrayList<String> getArrayList(String rowKey, String cf, String iden) throws IOException {
        return castResultToStringArrayList(table.get(new Get(Bytes.toBytes(rowKey))), cf, iden);
    }

    // Check if a row Exists
    public boolean exists(String rowKey) throws IOException {
        return table.exists(new Get(Bytes.toBytes(rowKey)));
    }

    // Close Hbase Session
    public void close() throws IOException {
        connection.close();
        table.close();
    }

    // Scan Table For PageData
    public ArrayList<PageData> scanPageDatas() throws IOException {
        ArrayList<PageData> pageDataArrayList = new ArrayList<>();
        Scan scan = new Scan();
        byte[] cfb = Bytes.toBytes("data");
        ResultScanner resultScanner = table.getScanner(scan);
        for (Result r : resultScanner) {
            PageData pageData = new PageData();
            pageData.title = Bytes.toString(r.getValue(cfb, Bytes.toBytes("title")));
            pageData.text = Bytes.toString(r.getValue(cfb, Bytes.toBytes("text")));
            pageData.description = Bytes.toString(r.getValue(cfb, Bytes.toBytes("description")));
            pageData.links = castResultToStringArrayList(r,"data","links");
            pageData.h1h3 = castResultToStringArrayList(r,"data","h1h3");
            pageData.h4h6 = castResultToStringArrayList(r,"data","h4h6");
        }
        return pageDataArrayList;
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

    private byte[] arrayListToByte(ArrayList<String> stringArrayList) {
        return WritableUtils.toByteArray(toWritable(stringArrayList));
    }

    private ArrayList<String> fromWritable(ArrayWritable writable) {
        Writable[] writables = ((ArrayWritable) writable).get();
        ArrayList<String> list = new ArrayList<String>(writables.length);
        for (Writable wrt : writables) {
            list.add(((Text) wrt).toString());
        }
        return list;
    }

    private ArrayList<String> castResultToStringArrayList(Result r, String columnFamily, String cell) throws IOException {
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

    //    ITS NOT GOING TO BE USEFUL
    private static String[] getColumnsInColumnFamily(Result r, String ColumnFamily) {
        NavigableMap<byte[], byte[]> familyMap = r.getFamilyMap(Bytes.toBytes(ColumnFamily));
        String[] Quantifers = new String[familyMap.size()];

        int counter = 0;
        for (byte[] bQunitifer : familyMap.keySet()) {
            Quantifers[counter++] = Bytes.toString(bQunitifer);

        }

        return Quantifers;
    }
}
