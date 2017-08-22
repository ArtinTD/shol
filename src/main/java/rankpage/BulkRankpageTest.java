package rankpage;

import kon.shol.HBase;

import java.io.IOException;

public class BulkRankpageTest {
    public static void main(String[] args) throws IOException {
        String[] a = {"B", "I"};
        String[] b = {"A", "C"};
        String[] c = {"A", "B", "F"};
        String[] d = {"H"};
        String[] e = {"D", "C"};
        String[] f = {"A", "I", "J"};
        String[] g = {"F"};
        String[] h = {"G", "J"};
        String[] i = {"A"};
        String[] j = {"G"};
        HBase hBase = new HBase("188.165.230.122:2181", "prtest");
        hBase.put("A", "data", "bulk", a);
        hBase.put("B", "data", "bulk", b);
        hBase.put("C", "data", "bulk", c);
        hBase.put("D", "data", "bulk", d);
        hBase.put("E", "data", "bulk", e);
        hBase.put("F", "data", "bulk", f);
        hBase.put("G", "data", "bulk", g);
        hBase.put("H", "data", "bulk", h);
        hBase.put("I", "data", "bulk", i);
        hBase.put("J", "data", "bulk", j);
        hBase.close();


    }
}
