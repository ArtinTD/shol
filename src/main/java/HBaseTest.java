import kon.shol.HBase;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;

public class HBaseTest {
    public static void main(String[] args) {
        HBase hBase = null;
        try {
            hBase = new HBase("188.165.230.122:2181");
            Connection connection = hBase.getConnection();
            hBase.setTable("myTable");
            Table table = hBase.getTable();
        /*    hBase.put("myRow99","Name", "firstName","Peter");
            hBase.put("myRow99","Name", "lastName","PeterNezhad");
            ArrayList<String> stringArrayList = new ArrayList<>();
            stringArrayList.add("Steve");
            stringArrayList.add("James");
            stringArrayList.add("Ross");
            hBase.put("myRow99", "Name", "friends", stringArrayList);*/

            Get get = new Get(Bytes.toBytes("myRow99"));
            get.addColumn(Bytes.toBytes("Name"), Bytes.toBytes("friends"));
            Result result = table.get(get);
            System.out.println(hBase.castResultToStringArrayList(result,"Name", "friends").get(1));

            System.out.println(hBase.getConnection().isClosed());
            table.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
