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
    private HbaseQueue hbaseQueue;
    private Parser parser;
    private final int MAX_BATCH_PUT_SIZE = 40;
    private final static Logger logger = Logger.getLogger(kon.shol.searchengine.hbase.Writer.class);

    public Writer(String tableNameStr) throws IOException {
        if (connection.isClosed()){
            new Connector();
        }
        TableName tableName = TableName.valueOf(tableNameStr);
        putList = new ArrayList<>();
        parser = new Parser();
        hbaseQueue = new HbaseQueue();
        table = connection.getTable(tableName);
    }


    private Put returnPutPageData(String url, PageData pageData) {
        Put put = new Put(Bytes.toBytes(url));
        byte[] dataColumnFamily = Bytes.toBytes("data");
        byte[] linksColumnFamily = Bytes.toBytes("links");
        byte[] anchorsColumnFamily = Bytes.toBytes("anchors");
        put.addColumn(dataColumnFamily, Bytes.toBytes("title"),
                Bytes.toBytes(pageData.getTitle()));
        put.addColumn(dataColumnFamily, Bytes.toBytes("description"),
                Bytes.toBytes(pageData.getDescription()));
        put.addColumn(dataColumnFamily, Bytes.toBytes("text"),
                Bytes.toBytes(pageData.getText()));
        put.addColumn(dataColumnFamily, Bytes.toBytes("url"),
                Bytes.toBytes(pageData.getUrl()));
        put.addColumn(dataColumnFamily, Bytes.toBytes("h1h3"),
                Bytes.toBytes(pageData.getH1h3()));
        put.addColumn(dataColumnFamily, Bytes.toBytes("h4h6"),
                Bytes.toBytes(pageData.getH4h6()));
        put.addColumn(dataColumnFamily, Bytes.toBytes("alt"),
                Bytes.toBytes(pageData.getImagesAlt()));
        put.addColumn(linksColumnFamily, Bytes.toBytes("links"),
                Bytes.toBytes(parser.serialize(pageData.getLinks())));
        put.addColumn(anchorsColumnFamily, Bytes.toBytes("anchors"),
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
