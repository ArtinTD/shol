package kon.shol;

import kon.shol.HBase;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import scala.Byte;

import java.io.IOException;
import java.util.ArrayList;

public class HBaseTest {
    public static void main(String[] args) {
        HBase hBase = null;
        try {
            hBase = new HBase("188.165.230.122:2181", "sites");
            Table table = hBase.getTable();
            Scan scan = new Scan();
            Get get = new Get(Bytes.toBytes("http://instagram.com"));
//            get.addColumn(Bytes.toBytes("data"),Bytes.toBytes("links"));
/*
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result r : resultScanner) {
                System.out.println(Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("title"))));
            }
            System.out.println(hBase.scanPageData());
*/
            System.out.println(hBase.getArrayList("https://www.1and1.it", "data", "links"));
            /*Scan scan = new Scan();
            ResultScanner scanner = table.getScanner(scan);
            for (Result r: scanner){
                System.out.println(Bytes.toString(r.getRow()));
                System.out.println(Bytes.toString(r.getValue(Bytes.toBytes("Name"), Bytes.toBytes("firstName"))));
                System.out.println(Bytes.toString(r.getValue(Bytes.toBytes("Name"), Bytes.toBytes("lastName"))));
            }*/
       /*     System.out.println(hBase.getArrayList("myRow99", "Name", "friends"));
            System.out.println(hBase.exists("myRow99"));
            System.out.println(hBase.getConnection().isClosed());*/
            hBase.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
