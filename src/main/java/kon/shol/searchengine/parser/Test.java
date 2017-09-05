package kon.shol.searchengine.parser;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class Test {

    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        URL url = new URL("http://wikipedia.org");

        long startTime = System.currentTimeMillis();
        String page = IOUtils.toString(url.openConnection().getInputStream());
        long endTime = System.currentTimeMillis();

        System.out.println("new time : "+ (endTime - startTime));

        startTime = System.currentTimeMillis();
        Document document =Jsoup.connect("http://wikipedia.org").get();
        endTime = System.currentTimeMillis();

        System.out.println("old time : " + (endTime - startTime));

    }
}
