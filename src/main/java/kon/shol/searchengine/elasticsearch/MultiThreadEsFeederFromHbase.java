package kon.shol.searchengine.elasticsearch;

import kon.shol.searchengine.kafka.ElasticQueue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static kon.shol.searchengine.elasticsearch.WebpageMaker.makeWebpage;
import static org.apache.kafka.clients.consumer.ConsumerConfig.DEFAULT_FETCH_MAX_BYTES;
import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_CONFIG;

public class MultiThreadEsFeederFromHbase {
   
   
   private final static Logger logger = Logger.getLogger(
         kon.shol.searchengine.elasticsearch.SingleScanEsFeederFromHbase.class);
   private int threadCount;
   private SingleThreadSyncEsBulkIndexer[] indexers;
   private ElasticQueue timestampsQueue;
   private ExecutorService executor;
   private Table table;
   private Connection connection;
   private boolean foundAnythingYet = false;
   private byte emptyCyclesCount = 0;
   private int indexerNumber = 0;
   private String columnFamily;
   private ConcurrentHashMap<Long, Boolean> duplicateCheck = new ConcurrentHashMap<>();
   private int periodLength;
   private long seed;
   private Properties properties;
   private OutputStream propertiesO = null;
   private InputStream propertiesI = null;
   private String tableName;
   private String index;
   private String type;
   private String zookeeperAddress;
   private String[] elasticHosts;
   private String topic;
   private String groupId;
   private String elasticClusterName;
   private Thread kafkaFeederThread;
   private Thread seedWriterThread;
   
   
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
      
      Runtime.getRuntime().addShutdownHook(new Thread(feeder::close));
      
      feeder.index();
      feeder.close();
   }
   
   private void init() throws IOException {
      propLoad();
      if (zookeeperAddress == null || topic == null || elasticClusterName == null) {
         System.out.println("[info] Remaking properties.");
         propInit();
         propLoad();
      }
      
      indexers = new SingleThreadSyncEsBulkIndexer[threadCount];
      Configuration conf = HBaseConfiguration.create();
      conf.set("hbase.zookeeper.quorum", zookeeperAddress);
      connection = ConnectionFactory.createConnection(conf);
      table = connection.getTable(TableName.valueOf(tableName));
      executor = Executors.newFixedThreadPool(threadCount);
      
      Properties properties = new Properties();
      properties.put(FETCH_MAX_BYTES_CONFIG, DEFAULT_FETCH_MAX_BYTES);
      timestampsQueue = new ElasticQueue(topic, groupId);
      
      kafkaFeederThread = new Thread(() -> {
         while (true) {
            try {
               if (seed + periodLength < System.currentTimeMillis()) {
                  timestampsQueue.send(String.valueOf(seed));
                  seed += periodLength;
               } else {
                  Thread.sleep(15000);
               }
            } catch (InterruptedException ex) {
               break;
            }
         }
      });
      kafkaFeederThread.start();
      
      seedWriterThread = new Thread(() -> {
         while (true) {
            try {
               Thread.sleep(5000);
               properties.replace("seed", seed + "");
               propertiesO = new FileOutputStream("elasticIndexer.properties");
               properties.store(propertiesO, "elasticIndexer configurations");
            } catch (IOException ignored) {
            } catch (InterruptedException ex) {
               break;
            } finally {
               try {
                  propertiesO.close();
               } catch (IOException ignored) {
               }
            }
         }
      });
      seedWriterThread.start();
      
      for (int i = 0; i < threadCount; i++) {
         indexers[i] = new SingleThreadSyncEsBulkIndexer(elasticClusterName, elasticHosts, index, type);
      }
   }
   
   private void index() {
      System.out.println("indexing started");
      do {
         PartialFeeder partialFeeder = null;
         try {
            long min = Long.parseLong((String) timestampsQueue.get());
            if (duplicateCheck.containsKey(min)) {
               continue;
            } else {
               duplicateCheck.put(min, true);
            }
            partialFeeder = new PartialFeeder(min, min + 600000,
                  (indexerNumber = (indexerNumber + 1) % threadCount));
         } catch (IOException ex) {
            System.out.println(ex.toString());
            continue;
         } catch (InterruptedException ex) {
            System.out.println(ex.toString());
         } catch (NumberFormatException ignored) {
            continue;
         }
         
         executor.execute(partialFeeder);
         
      } while (true);
      
   }
   
   private void close() {
      kafkaFeederThread.interrupt();
      seedWriterThread.interrupt();
      try {
         table.close();
         connection.close();
         for (SingleThreadSyncEsBulkIndexer indexer : indexers) {
            indexer.end();
         }
         Thread.sleep(5000);
         System.exit(0);
      } catch (IOException | InterruptedException ignored) {
      }
   }
   
   private void propLoad() {
      try {
         propertiesI = new FileInputStream("elasticIndexer.properties");
         properties = new Properties();
         properties.load(propertiesI);
         threadCount = Integer.parseInt(properties.getProperty("threadCount", "8"));
         seed = Long.parseLong(properties.getProperty("seed"));
         periodLength = Integer.parseInt(properties.getProperty("periodLength", "60000"));
         tableName = properties.getProperty("tableName");
         columnFamily = properties.getProperty("columnFamily");
         index = properties.getProperty("index");
         type = properties.getProperty("type");
         zookeeperAddress = properties.getProperty("zookeeper");
         elasticHosts = properties.getProperty("elasticHosts").split("=");
         topic = properties.getProperty("topic");
         groupId = properties.getProperty("groupId");
         elasticClusterName = properties.getProperty("elasticClusterName");
      } catch (Exception ex) {
         System.out.println(ex.toString());
      } finally {
         try {
            propertiesI.close();
         } catch (Exception ignored) {
         }
      }
   }
   
   private void propInit() {
      try {
         propertiesO = new FileOutputStream("elasticIndexer.properties");
         properties = new Properties();
         properties.put("threadCount", "16");
         properties.put("seed", "1504323000000");
         properties.put("elasticClusterName", "sholastic");
         properties.put("periodLength", "60000");
         properties.put("tableName", "webpages");
         properties.put("columnFamily", "data");
         properties.put("index", "sholastic");
         properties.put("type", "webpagestest1");
         properties.put("topic", "ElasticQueueT2");
         properties.put("groupId", "shol");
         properties.put("zookeeper", "188.165.230.122:2181");
         properties.put("elasticHosts", "188.165.230.122=188.165.235.136");
         
         properties.store(propertiesO, "elasticIndexer configurations");
      } catch (Exception ex) {
         System.out.println(ex.toString());
      } finally {
         try {
            propertiesO.close();
         } catch (Exception ignored) {
         }
      }
   }
   
   private class PartialFeeder implements Runnable {
      
      private ResultScanner results;
      private SingleThreadSyncEsBulkIndexer indexer;
      private long minStamp;
      private long maxStamp;
      
      public PartialFeeder(long minStamp, long maxStamp,
                           int indexerNumber) throws IOException {
         
         this.minStamp = minStamp;
         this.maxStamp = maxStamp;
         
         Scan scan = new Scan();
         scan.addFamily(Bytes.toBytes(columnFamily));
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
               indexer.flush();
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