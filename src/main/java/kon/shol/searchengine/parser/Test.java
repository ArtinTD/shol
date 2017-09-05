package kon.shol.searchengine.parser;

import kon.shol.searchengine.crawler.Fetcher;
import kon.shol.searchengine.hbase.Connector;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();

        long startTime = System.currentTimeMillis();
        Document document = Jsoup.connect("http://wikipedia.org").get();
        long endTime = System.currentTimeMillis();

        System.out.println("Fetch time : "+ (endTime - startTime));

        startTime = System.currentTimeMillis();
        parser.parse(document);
        endTime = System.currentTimeMillis();

        System.out.println("Parse time : " + (endTime - startTime));
    }
}
