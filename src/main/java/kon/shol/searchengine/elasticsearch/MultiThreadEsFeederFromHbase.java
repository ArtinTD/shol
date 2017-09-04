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

public class MultiThreadEsFeederFromHbase {
   
   
   private final static Logger logger = Logger.getLogger(
         kon.shol.searchengine.elasticsearch.SingleScanEsFeederFromHbase.class);
   private final static int THREAD_COUNT = 16;
   private final SingleThreadSyncEsBulkIndexer[] indexers =
         new SingleThreadSyncEsBulkIndexer[THREAD_COUNT];
   private ExecutorService executor;
   private Table table;
   private Connection connection;
   private boolean foundAnythingYet = false;
   private byte emptyCyclesCount = 0; // TODO rename.
   private int indexerNumber = 0;
   
   public static void main(String[] args) {
      
      MultiThreadEsFeederFromHbase feeder =
            new MultiThreadEsFeederFromHbase();
      
      try {
         feeder.init();
      } catch (IOException ex) {
         logger.error("Could not initialize MultiThreadEsFeederFromHbase;\n"
               + "details: " + ex.toString());
         System.exit(1);
      }
      feeder.index();
      feeder.close();
   }
   
   private void init() throws IOException {
      
      init(new String[]{"188.165.230.122", "188.165.235.136"}, "shol",
            "webpagestest5", "188.165.230.122:2181", "demodb");
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
      
      long max = 1503730220000L;
      do {
         long min = max - 10000;
         
         PartialFeeder partialFeeder;
         try {
            partialFeeder = new PartialFeeder(min, max,
                  (indexerNumber = (indexerNumber + 1) % THREAD_COUNT));
         } catch (IOException ex) {
            logger.error(ex.toString());
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
         for (SingleThreadSyncEsBulkIndexer indexer : indexers) {
            indexer.end();
         }
      } catch (IOException ignored) {
      }
   }
   
   
   private class PartialFeeder implements Runnable {
      private ResultScanner results;
      private SingleThreadSyncEsBulkIndexer indexer;
      private long minStamp;
      private long maxStamp;
      
      public PartialFeeder(long minStamp, long maxStamp, int indexerNumber) throws IOException {
         this.minStamp = minStamp;
         this.maxStamp = maxStamp;
         
         Scan scan = new Scan();
         scan.setTimeRange(minStamp, maxStamp);
         results = table.getScanner(scan);
         indexer = indexers[indexerNumber];
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
               logger.error(ex.toString());
            }
         }
         
         
         if (foundAnythingYet) {
            if (foundAnythingNow) {
               System.out.println("[info] Cycle: +YET +NOW: " + minStamp + " : " + maxStamp);
            } else {
               System.out.println("[info] Cycle: +YET -NOW: " + minStamp + " : " + maxStamp);
               emptyCyclesCount++;
            }
         } else {
            if (!foundAnythingNow) {
               System.out.println("[info] Cycle: -YET -NOW: " + minStamp + " : " + maxStamp);
            }
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