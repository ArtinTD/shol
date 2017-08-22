package rankpage;

import kon.shol.HBase;

import java.io.IOException;

public class BulkRankpageTest {
    public static void main(String[] args) throws IOException {
        String[] a = {"B", "C", "D"};
        String[] b = {"A"};
        String[] c = {"A"};
        String[] d = {"A", "E", "F", "G", "H"};
        String[] e = {"A"};
        String[] f = {"A"};
        String[] g = {"A"};
        String[] h = {"A"};
       /* String[] i = {"A"};
        String[] j = {"G"};*/
        HBase hBase = new HBase("188.165.230.122:2181", "prtest");
        hBase.put("A", "data", "bulk", a);
        hBase.put("B", "data", "bulk", b);
        hBase.put("C", "data", "bulk", c);
        hBase.put("D", "data", "bulk", d);
        hBase.put("E", "data", "bulk", e);
        hBase.put("F", "data", "bulk", f);
        hBase.put("G", "data", "bulk", g);
        hBase.put("H", "data", "bulk", h);
        /*hBase.put("I", "data", "bulk", i);
        hBase.put("J", "data", "bulk", j);*/
        hBase.close();


    }
}
