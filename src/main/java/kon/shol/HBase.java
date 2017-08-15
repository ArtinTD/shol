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

//    // Refer to table metadata names by byte array in the HBase API
//    private static final byte[] TABLE_NAME = Bytes.toBytes("Hello-Bigtable");
//    private static final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("cf1");
//    private static final byte[] COLUMN_NAME = Bytes.toBytes("greeting");
//
//    // Write some friendly greetings to Cloud Bigtable
//    private static final String[] GREETINGS =
//            { "Hello World!", "Hello Cloud Bigtable!", "Hello HBase!" };
//
//    /**
//     * Connects to Cloud Bigtable, runs some basic operations and prints the results.
//     */
//    private static void doHelloWorld(String projectId, String instanceId) {
//
//        // [START connecting_to_bigtable]
//        // Create the Bigtable connection, use try-with-resources to make sure it gets closed
//        try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
//
//            // The admin API lets us create, manage and delete tables
//            Admin admin = connection.getAdmin();
//            // [END connecting_to_bigtable]
//
//            // [START creating_a_table]
//            // Create a table with a single column family
//            HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
//            descriptor.addFamily(new HColumnDescriptor(COLUMN_FAMILY_NAME));
//
//            print("Create table " + descriptor.getNameAsString());
//            admin.createTable(descriptor);
//            // [END creating_a_table]
//
//            // [START writing_rows]
//            // Retrieve the table we just created so we can do some reads and writes
//            Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
//
//            // Write some rows to the table
//            print("Write some greetings to the table");
//            for (int i = 0; i < GREETINGS.length; i++) {
//                // Each row has a unique row key.
//                //
//                // Note: This example uses sequential numeric IDs for simplicity, but
//                // this can result in poor performance in a production application.
//                // Since rows are stored in sorted order by key, sequential keys can
//                // result in poor distribution of operations across nodes.
//                //
//                // For more information about how to design a Bigtable schema for the
//                // best performance, see the documentation:
//                //
//                //     https://cloud.google.com/bigtable/docs/schema-design
//                String rowKey = "greeting" + i;
//
//                // Put a single row into the table. We could also pass a list of Puts to write a batch.
//                Put put = new Put(Bytes.toBytes(rowKey));
//                put.addColumn(COLUMN_FAMILY_NAME, COLUMN_NAME, Bytes.toBytes(GREETINGS[i]));
//                table.put(put);
//            }
//            // [END writing_rows]
//
//            // [START getting_a_row]
//            // Get the first greeting by row key
//            String rowKey = "greeting0";
//            Result getResult = table.get(new Get(Bytes.toBytes(rowKey)));
//            String greeting = Bytes.toString(getResult.getValue(COLUMN_FAMILY_NAME, COLUMN_NAME));
//            System.out.println("Get a single greeting by row key");
//            System.out.printf("\t%s = %s\n", rowKey, greeting);
//            // [END getting_a_row]
//
//            // [START scanning_all_rows]
//            // Now scan across all rows.
//            Scan scan = new Scan();
//
//            print("Scan for all greetings:");
//            ResultScanner scanner = table.getScanner(scan);
//            for (Result row : scanner) {
//                byte[] valueBytes = row.getValue(COLUMN_FAMILY_NAME, COLUMN_NAME);
//                System.out.println('\t' + Bytes.toString(valueBytes));
//            }
//            // [END scanning_all_rows]
//
//            // [START deleting_a_table]
//            // Clean up by disabling and then deleting the table
//            print("Delete the table");
//            admin.disableTable(table.getName());
//            admin.deleteTable(table.getName());
//            // [END deleting_a_table]
//
//        } catch (IOException e) {
//            System.err.println("Exception while running HelloWorld: " + e.getMessage());
//            e.printStackTrace();
//            System.exit(1);
//        }
//
//        System.exit(0);
//    }
//
//    private static void print(String msg) {
//        System.out.println("HelloWorld: " + msg);
//    }
//
//    public static void main(String[] args) {
//        // Consult system properties to get project/instance
//        String projectId = requiredProperty("bigtable.projectID");
//        String instanceId = requiredProperty("bigtable.instanceID");
//
//        doHelloWorld(projectId, instanceId);
//    }
//
//    private static String requiredProperty(String prop) {
//        String value = System.getProperty(prop);
//        if (value == null) {
//            throw new IllegalArgumentException("Missing required system property: " + prop);
//        }
//        return value;
//    }


//    COMMANDS FOR CREATING TABLE


    public static String[] getColumnsInColumnFamily(Result r, String ColumnFamily) {

        NavigableMap<byte[], byte[]> familyMap = r.getFamilyMap(Bytes.toBytes(ColumnFamily));
        String[] Quantifers = new String[familyMap.size()];

        int counter = 0;
        for (byte[] bQunitifer : familyMap.keySet()) {
            Quantifers[counter++] = Bytes.toString(bQunitifer);

        }

        return Quantifers;
    }

    public static void main(String[] args) throws IOException {
        TableName tableName = TableName.valueOf("myTable");
     /*   HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
        hTableDescriptor.addFamily(new HColumnDescriptor("Id"));
        hTableDescriptor.addFamily(new HColumnDescriptor("Name"));
*/
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "188.165.230.122:2181");
        Connection connection = ConnectionFactory.createConnection(configuration);
        Admin admin = connection.getAdmin();
//        admin.createTable(hTableDescriptor);
        System.out.println("Connecting...");

        Table table = connection.getTable(tableName);
        Put put = new Put(Bytes.toBytes("myRow2"));
        put.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("firstName"), Bytes.toBytes("Kambiz"));
        put.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("lastName"), Bytes.toBytes("Gholami"));
//        table.put(put);
        Get get = new Get(Bytes.toBytes("myRow6"));
        System.out.println(table.exists(get));
//        Scan scan = new Scan();
        get.setMaxVersions(1);
        get.addFamily(Bytes.toBytes("Name"));
        get.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("lastName"));

        Result result = table.get(get);
        System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("Name"),Bytes.toBytes("lastName"))));
//        System.out.println(result);
        System.out.println("Done!");
        byte[] databytes = Bytes.toBytes("Name");
//        System.out.println(getColumnsInColumnFamily(result, "Name")[0]);
        table.close();
        connection.close();
    }
}
