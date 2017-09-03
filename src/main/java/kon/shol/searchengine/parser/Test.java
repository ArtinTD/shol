package kon.shol.searchengine.parser;

import kon.shol.searchengine.crawler.Fetcher;
import kon.shol.searchengine.hbase.Connector;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Test {
    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.parser.Test.class);

    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        Fetcher fetcher = new Fetcher();
        System.out.println(parser.getDomain("https://www.wikipedia.org"));
        System.out.println(parser.reverseDomain("https://www.wikipedia.org"));
    }
}
