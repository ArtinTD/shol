package kon.shol;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class SimpleElasticIndexFeederFromHbase {
    public static void main(String[] args) {
        try {
            SimpleElasticIndexer ei = new SimpleElasticIndexer("188.165.235.136", "shol", "webpagestest3");
            HBase hBase = new HBase("188.165.230.122:2181", "db ");
            Table table = hBase.getTable();
            Scan scan = new Scan();
            ResultScanner resultScanner = table.getScanner(scan);

            for (Result r : resultScanner) {
                try {
                    double pageRank;
                    try { pageRank = Bytes.toDouble(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("pagerank"))); }
                    catch ( Exception e ) { pageRank = 0.15d; }
                    ei.add(Bytes.toString(r.getRow()),//URL
                            Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("title"))),
                            Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("text"))),
                            Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("description"))),
                            Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("h1h3"))),
                            Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("h4h6"))),
                            Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("alt"))),
                            pageRank
                    );
                } catch (Exception e) { e.printStackTrace(); }
            }
            System.out.println("HBase connection closed.");
            hBase.close();

            while (ei.isWorking()) {
                try { Thread.sleep(3000); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
            try { Thread.sleep(3000); }
            catch (InterruptedException e) { e.printStackTrace(); }
            ei.close();
            System.exit(0);
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}
