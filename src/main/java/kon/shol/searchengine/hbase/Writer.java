package kon.shol.searchengine.hbase;

import kon.shol.searchengine.parser.PageData;
import kon.shol.searchengine.parser.Parser;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.spark_project.jetty.util.BlockingArrayQueue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static kon.shol.searchengine.hbase.Connector.connection;

public class Writer {

    private Table table;
    private List<Put> putList;
    private Parser parser;

    private int MAX_BATCH_PUT_SIZE = 20;
    private final static Logger logger = Logger.getLogger("custom");
    private final byte[] DATA_CF = Bytes.toBytes("data");
    private final byte[] LINKS_CF = Bytes.toBytes("links");
    private final byte[] ANCHORS_CF = Bytes.toBytes("anchors");

    public Writer(String tableNameStr, int batchPutSize) throws IOException {
        if (connection.isClosed()) {
            new Connector();
        }
        MAX_BATCH_PUT_SIZE = batchPutSize;
        TableName tableName = TableName.valueOf(tableNameStr);
        putList = new ArrayList<>();
        parser = new Parser();
        table = connection.getTable(tableName);
    }

    private Put returnPutPageData(String url, PageData pageData) {
        Put put = new Put(Bytes.toBytes(url));
        put.addColumn(DATA_CF, Bytes.toBytes("title"),
                Bytes.toBytes(pageData.getTitle()));
        put.addColumn(DATA_CF, Bytes.toBytes("description"),
                Bytes.toBytes(pageData.getDescription()));
        put.addColumn(DATA_CF, Bytes.toBytes("text"),
                Bytes.toBytes(pageData.getText()));
        put.addColumn(DATA_CF, Bytes.toBytes("url"),
                Bytes.toBytes(pageData.getUrl()));
        put.addColumn(DATA_CF, Bytes.toBytes("h1h3"),
                Bytes.toBytes(pageData.getH1h3()));
        put.addColumn(DATA_CF, Bytes.toBytes("h4h6"),
                Bytes.toBytes(pageData.getH4h6()));
        put.addColumn(DATA_CF, Bytes.toBytes("alt"),
                Bytes.toBytes(pageData.getImagesAlt()));
        pageData.getAnchors().forEach((key, value) -> {
            String reversedUrl = parser.reverseDomain(key);
            if (reversedUrl != null)
                put.addColumn(ANCHORS_CF, Bytes.toBytes(reversedUrl), Bytes.toBytes(value));
        });
        return put;
    }

    void addToPutList(PageData pageData) {
        String url = parser.reverseDomain(pageData.getUrl());
        Put put = returnPutPageData(url, pageData);
        putList.add(put);
        if (putList.size() > MAX_BATCH_PUT_SIZE) {
            batchPut();
        }
    }

    public void putPageData(PageData pageData) {
        String url = parser.reverseDomain(pageData.getUrl());
        Put put = returnPutPageData(url, pageData);
        try {
            table.put(put);
            System.out.println("Put " + url + " to Hbase Was Successful");
        } catch (IOException e) {
            e.printStackTrace();
            logger.fatal("Can't put to HBase");
        }
    }

    private void batchPut() {
        if (connection.isClosed()) {
            try {
                new Connector();
            } catch (IOException ioe) {
                logger.fatal("Cannot Establish Connection to Hbase");
            }
        } else {
            try {
                logger.debug("Thread: " + Thread.currentThread().getName() + " | Put " + MAX_BATCH_PUT_SIZE + " was Successful!");
                table.put(putList);
                putList = new ArrayList<>();
            } catch (IOException e) {
                logger.fatal("Couldn't Put in HBase");

            }
        }
    }
}

