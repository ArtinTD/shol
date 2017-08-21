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
            SimpleElasticIndexer ei = new SimpleElasticIndexer("188.165.235.136", "shol", "webpagestest1");
            HBase hBase = new HBase("188.165.230.122:2181", "sites");
            Table table = hBase.getTable();
            Scan scan = new Scan();
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result r : resultScanner)
                ei.add( Bytes.toString(r.getRow()),//URL
                        Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("text"))) );
            hBase.close();
            while (ei.isWorking()) {
                try { Thread.sleep(300); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
            try { Thread.sleep(300); }
            catch (InterruptedException e) { e.printStackTrace(); }
            ei.close();
        }
        catch (IOException e) { e.printStackTrace(); }
    }
}
