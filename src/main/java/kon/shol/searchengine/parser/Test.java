package kon.shol.searchengine.parser;

import com.google.common.net.InternetDomainName;
import kon.shol.searchengine.crawler.Fetcher;
import kon.shol.searchengine.hbase.Connector;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class Test {

    public static void main(String[] args) throws IOException {
        URL url = new URL("http://google.com/;jadjass;lkljdfgo/ausdh/aidsia?kir=khar");
        System.out.println(InternetDomainName.from(url.getHost()).topPrivateDomain());
        //
//  System.out.println(parser.getPageData().getLinks());
    }
}
