package kon.shol.searchengine.parser;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;

public class Test {
    private final static Logger logger = Logger.getLogger("custom");

    public static void main(String[] args) throws IOException {
        /*Parser parser = new Parser();
        logger.info(parser.getDomain("https://www.wikipedia.org"));
        logger.fatal(parser.reverseDomain("https://www.wikipedia.org"));*/
        Put put = new Put(Bytes.toBytes("data"));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("url"), Bytes.toBytes("http://www.google.com"));
        System.out.println(Bytes.toString(put.get(Bytes.toBytes("data"),
                Bytes.toBytes("url")).get(0).getValue()));
    }
}
