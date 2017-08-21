package rankpage;

import kon.shol.HBase;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 8/14/2017.
 */
public class RankPageAlgorithmTest {

    public static void main(String[] args) {

        //Assume we get all pages!
        PageGraph pg = new PageGraph();
        HBase hBase = null;
        try {
            hBase = new HBase("188.165.230.122:2181", "sites");
            Table table = hBase.getTable();
            Scan scan = new Scan();
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result r : resultScanner) {
//                System.out.print(Bytes.toString(r.getRow()));

//                hBase.getArrayList(Bytes.toString(r.getRow()), "data", "links");
                pg.addPage( new Page( Bytes.toString(r.getRow() ),
                        hBase.getArrayList(Bytes.toString(r.getRow()), "data", "links")
                    )
                );
            }

            System.out.println("scan finished");

            for (int i = 0; i < 100; i++) { //repeat to Approximate PR
                pg.update();
            }

            for (Page page : pg.pages.values())
                System.out.println(page.url + " " + page.getPR());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}