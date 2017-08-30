package kon.shol.searchengine.hbase;

import kon.shol.searchengine.kafka.HbaseQueue;
import kon.shol.searchengine.parser.PageData;
import kon.shol.searchengine.parser.Parser;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static kon.shol.searchengine.hbase.Connector.connection;

public class Writer implements Runnable {

    private Table table;
    private List<Put> putList;
    private static HbaseQueue hbaseQueue = new HbaseQueue();
    private Parser parser;
    
    private final int  MAX_BATCH_PUT_SIZE = 40;
    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.hbase.Writer.class);
    private final byte[] DATA_CF = Bytes.toBytes("data");
    private final byte[] LINKS_CF = Bytes.toBytes("links");
    private final byte[] ANCHORS_CF = Bytes.toBytes("anchors");
    
    public Writer(String tableNameStr) throws IOException {
        if (connection.isClosed()){
            new Connector();
        }
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
        put.addColumn(LINKS_CF, Bytes.toBytes("links"),
                Bytes.toBytes(parser.serialize(pageData.getLinks())));
        put.addColumn(ANCHORS_CF, Bytes.toBytes("anchors"),
                Bytes.toBytes(parser.serialize(pageData.getAnchors())));
        return put;
    }

    public void putPageData(String url, PageData pageData) {
        Put put = returnPutPageData(url, pageData);
        try {
            table.put(put);
        } catch (IOException e) {
            logger.error("Can't put to HBase");
        }
    }

    private void batchPut(PageData pageData) {
        String url = parser.reverseDomain(pageData.getUrl());
        Put put = returnPutPageData(url, pageData);
        putList.add(put);
        logger.error("Added " + pageData.getUrl() + " to the Put List ... ");
        if (putList.size() > MAX_BATCH_PUT_SIZE) {
            if (connection.isClosed()) {
                try {
                    new Connector();
                } catch (IOException ioe) {
                    logger.error("Cannot Establish Connection to Hbase");
                } finally {
                    //TODO: fix this shit
                    batchPut(pageData);
                }
            } else {
                try {
                    logger.error("Put " + MAX_BATCH_PUT_SIZE + " was Successful!");
                    table.put(putList);
                } catch (IOException e) {
                    logger.error("Couldn't Put in HBase");

                }
            }
        }
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            PageData pendingPageData;
            try {
                pendingPageData = parser.deserialize(hbaseQueue.get(), PageData.class);
            } catch (InterruptedException e) {
                continue;
            }
            this.batchPut(pendingPageData);
        }
    }
}
