package rankpage;

import kon.shol.HBase;
import org.apache.hadoop.hbase.client.*;
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
        System.out.println(System.currentTimeMillis());
        HBase hBase = null;
        try {
            hBase = new HBase("188.165.230.122:2181", "prtest2");
            Table table = hBase.getTable();
            Scan scan = new Scan();
            ResultScanner resultScanner = table.getScanner(scan);
            for (Result r : resultScanner) {
                System.out.print(Bytes.toString(r.getRow()));
                System.out.println(Bytes.toDouble(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("pagerank"))));


//                hBase.getArrayList(Bytes.toString(r.getRow()), "data", "links");
//                pg.addPage( new Page( Bytes.toString(r.getRow() ),
//                        hBase.getArrayList(Bytes.toString(r.getRow()), "data", "bulk")
//                    )
//                );
            }

            System.exit(0);

            System.out.println("scan finished");

            for (int i = 0; i < 100; i++) { //repeat to Approximate PR
                pg.update();
            }

            for (Page page : pg.pages.values()){
                System.out.println(page.url + " " + page.getPR());
                Put put = new Put(Bytes.toBytes(page.url));
                put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("pagerank"), Bytes.toBytes(page.getPR()));
                table.put(put);
            }

            System.out.println(System.currentTimeMillis());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}