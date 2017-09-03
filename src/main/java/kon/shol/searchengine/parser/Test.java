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
    private final static Logger logger = Logger.getLogger("custom");

    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        logger.info(parser.getDomain("https://www.wikipedia.org"));
        logger.fatal(parser.reverseDomain("https://www.wikipedia.org"));
    }
}
