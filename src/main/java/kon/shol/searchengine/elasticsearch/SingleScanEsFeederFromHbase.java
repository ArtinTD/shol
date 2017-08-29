package kon.shol.searchengine.elasticsearch;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;

public class SingleScanEsFeederFromHbase {
   
   private final static Logger logger = Logger.getLogger(
         kon.shol.searchengine.elasticsearch.SingleScanEsFeederFromHbase.class);
   
   private Table table;
   private Connection connection;
   private ResultScanner resultScanner;
   private SingleThreadSyncEsIndexer indexer;
   
   public static void main(String[] args) {
      
      SingleScanEsFeederFromHbase feeder = new SingleScanEsFeederFromHbase();
      
      try {
         feeder.init(new String[]{"188.165.230.122", "188.165.235.136"}, "shol" // todo make default vars
               , "webpagestest3", "188.165.230.122:2181", "db");
      } catch (IOException ex) {
         logger.error("Could not initialize SingleScanEsFeederFromHbase;\n"
               + "details: " + ex.toString());
         System.exit(1);
      }
      feeder.index();
      feeder.close();
   }
   
   private void init(String[] hosts, String index, String type
         , String zookeeperAddress, String tablename) throws IOException {
      
      indexer = new SingleThreadSyncEsIndexer(hosts, index, type);
      Configuration configuration = HBaseConfiguration.create();
      configuration.set("hbase.zookeeper.quorum", zookeeperAddress);
      connection = ConnectionFactory.createConnection(configuration);
      table = connection.getTable(TableName.valueOf(tablename));
      resultScanner = table.getScanner(new Scan());
   }
   
   private WebPage makeWebpage(Result r) {
      
      String url       = Bytes.toString(r.getRow());
      String title     = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("title")));
      String text      = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("text")));
      String desc      = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("description")));
      String h1h3      = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("h1h3")));
      String h4h6      = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("h4h6")));
      String imagesAlt = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("alt")));
      
      String anchorTexts;
      try {
         anchorTexts = Bytes.toString(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("anchors")));
      } catch (Exception expected) {
         anchorTexts = "";
      }
      
      double pageRank;
      try {
         pageRank = Bytes.toDouble(r.getValue(Bytes.toBytes("data"), Bytes.toBytes("pagerank")));
      } catch (Exception expected) {
         pageRank = 0.15d;
      }
      
      return new WebPage(url, title, text, desc, h1h3, h4h6, imagesAlt, pageRank, anchorTexts);
   }
   
   private void index() {
 
      for (Result r : resultScanner) {
         try {
            indexer.add(makeWebpage(r));
         } catch (Exception e) {
            logger.error(e.toString());
         }
      }
   }
   
   private void close() {
 
      try {
         table.close();
         connection.close();
      } catch (IOException ignored) {
      }
      
      while (!indexer.isDone()) {
         try {
            Thread.sleep(350);
         } catch (InterruptedException ignored) {
         }
         indexer.end();
      }
   }
}