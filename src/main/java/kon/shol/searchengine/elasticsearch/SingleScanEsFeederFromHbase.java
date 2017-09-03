package kon.shol.searchengine.elasticsearch;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.log4j.Logger;

import java.io.IOException;

import static kon.shol.searchengine.elasticsearch.WebpageMaker.makeWebpage;

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
         feeder.init();
      } catch (IOException ex) {
         logger.error("Could not initialize SingleScanEsFeederFromHbase;\n"
               + "details: " + ex.toString());
         System.exit(1);
      }
      feeder.index();
      feeder.close();
   }
   
   private void init() throws IOException {
      init(new String[]{"188.165.230.122", "188.165.235.136"}, "shol",
            "webpagestest3", "188.165.230.122:2181", "db");
   }
   
   private void init(String[] hosts, String index, String type,
                     String zookeeperAddress, String tablename) throws IOException {
      
      indexer = new SingleThreadSyncEsIndexer(hosts, index, type);
      Configuration configuration = HBaseConfiguration.create();
      configuration.set("hbase.zookeeper.quorum", zookeeperAddress);
      connection = ConnectionFactory.createConnection(configuration);
      table = connection.getTable(TableName.valueOf(tablename));
      resultScanner = table.getScanner(new Scan());
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