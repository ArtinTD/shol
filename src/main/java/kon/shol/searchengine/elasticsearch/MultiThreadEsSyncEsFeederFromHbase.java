package kon.shol.searchengine.elasticsearch;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static kon.shol.searchengine.elasticsearch.WebpageMaker.makeWebpage;

public class MultiThreadEsSyncEsFeederFromHbase {
   
   
   public static void main(String[] args) {
   
   }
   
   private final static Logger logger = Logger.getLogger(
         kon.shol.searchengine.elasticsearch.SingleScanEsFeederFromHbase.class);
   
   private final static int THREAD_COUNT = 24;
   
   private final EsIndexer[] indexers = new SingleThreadSyncEsBulkIndexer[THREAD_COUNT];
   private ExecutorService executor;
   private Table table;
   private Connection connection;
   private boolean foundAnythingYet = false;
   private byte emptyCyclesCount = 0; // TODO rename.
   private int indexerNumber = 0;
   
   private void init() throws IOException {
      
      init(new String[]{"188.165.230.122", "188.165.235.136"}, "shol",
            "webpagestest4", "188.165.230.122:2181", "demodb");
   }
   
   private void init(String[] hosts, String index, String type,
                     String zookeeperAddress, String tablename) throws IOException {
      
      Configuration conf = HBaseConfiguration.create();
      conf.set("hbase.zookeeper.quorum", zookeeperAddress);
      connection = ConnectionFactory.createConnection(conf);
      table = connection.getTable(TableName.valueOf(tablename));
      executor = Executors.newFixedThreadPool(THREAD_COUNT);
      
      for (int i = 0; i < THREAD_COUNT; i++) {
         indexers[i] = new SingleThreadSyncEsBulkIndexer(hosts, index, type);
      }
   }
   
   private void index() {
      
      long max = 1503732600000L;
      do {
         long min = max - 20000;
         
         PartialFeeder partialFeeder;
         try {
            partialFeeder = new PartialFeeder(min, max,
                  (indexerNumber = indexerNumber + 1 % THREAD_COUNT));
         } catch (IOException ex) {
//            logger.error(ex.toString());
            continue;
         } finally {
            max = min;
         }
         
         executor.execute(partialFeeder);
         
      } while (true); // FIXME: figure out some kind of stopping mechanism. /Edit: Fuck it?
      
   }
   
   private void close() {
      
      try {
         table.close();
         connection.close();
      } catch (IOException ignored) {
      }
   }
   
   
   private class PartialFeeder implements Runnable {
      private ResultScanner results;
      private SingleThreadSyncEsBulkIndexer indexer;
      
      public PartialFeeder(long minStamp, long maxStamp, int indexerNumber) throws IOException {
         Scan scan = new Scan();
         scan.setTimeRange(minStamp, maxStamp);
         results = table.getScanner(scan);
         indexer = (SingleThreadSyncEsBulkIndexer) indexers[indexerNumber];
      }
      
      
      @Override
      public void run() {
         
         boolean foundAnythingNow = false;
         for (Result result : results) {
            foundAnythingYet = true;
            foundAnythingNow = true;
            emptyCyclesCount = 0;
            try {
               indexer.add(makeWebpage(result));
            } catch (Exception ex) {
//               logger.error(ex.toString());
            }
         }
         
         if (foundAnythingYet & !foundAnythingNow) {
            emptyCyclesCount++;
         }
         
         
         if (emptyCyclesCount > 11) {
            try {
               Thread.sleep(60000);
            } catch (InterruptedException ignored) {
            }
            close();
            System.exit(0);
         }
      }
      
   }
}