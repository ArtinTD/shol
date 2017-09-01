package kon.shol.searchengine.hbase;

import kon.shol.searchengine.crawler.Fetcher;
import kon.shol.searchengine.parser.Parser;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        Fetcher fetcher = new Fetcher();
        Connector connector = new Connector();
        Writer writer = new Writer("testdb");
//        Connection connection = connector.getConnection();
        Document document = fetcher.fetch("https://spring.io/guides/gs/consuming-rest/");
        parser.parse(document);
        writer.putPageData(parser.reverseDomain(parser.getPageData().getUrl()), parser.getPageData());

    }
}
