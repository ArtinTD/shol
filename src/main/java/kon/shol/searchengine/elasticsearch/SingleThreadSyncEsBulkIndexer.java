package kon.shol.searchengine.elasticsearch;

import com.google.gson.Gson;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

public class SingleThreadSyncEsBulkIndexer implements EsIndexer {
   
   //   private static final Logger logger = Logger.getLogger(
//         kon.shol.searchengine.elasticsearch.SingleThreadSyncEsIndexer.class);
   private static final ArrayBlockingQueue<WebPage> indexQueue;
   private static Counter counter;
   private static Timer timer;
   private static long count;
   
   static {
      indexQueue = new ArrayBlockingQueue<WebPage>(16384);
      count = 0;
      counter = new Counter();
      timer = new Timer();
      timer.schedule(counter, 30000, 30000);
   }
   
   private Semaphore lock = new Semaphore(1);
   private String elasticClusterName;
   private String[] hosts;
   private String index;
   private String type;
   private Sender indexer;
   private int port;
   
   public SingleThreadSyncEsBulkIndexer(String elasticClusterName,
                                        String[] hosts, String index, String type) {
      
      this(elasticClusterName, hosts, 9300, index, type);
   }
   
   public SingleThreadSyncEsBulkIndexer(String elasticClusterName, String[] hosts, int port, String index, String type) {
      this.elasticClusterName = elasticClusterName;
      this.hosts = hosts;
      this.port = port;
      this.index = index;
      this.type = type;
      
      indexer = new Sender();
      indexer.setName("SingleThreadSyncEsBulkIndexer-indexerThread");
      indexer.start();
   }
   
   public boolean isDone() {
      return indexQueue.isEmpty();
   }
   
   public void end() {
      indexer.interrupt();
   }
   
   public void flush() {
      indexer.flush();
   }
   
   @Override
   public void add(WebPage newWebPage) {
      try {
         indexQueue.put(newWebPage);
      } catch (InterruptedException ignored) {
      }
   }
   
   private static class Counter extends TimerTask {
      @Override
      public void run() {
         System.out.println("[info] index count in prev 30s: " + count);
         count = 0L;
      }
   }
   
   private class Sender extends Thread {
      
      private final int hostCount = hosts.length;
      private final Gson jsonMaker = new Gson();
      private TransportClient transportClient;
      private BulkProcessor bulkProcessor;
      
      private Sender() {
         
         InetSocketTransportAddress[] addresses = new InetSocketTransportAddress[hostCount];
         for (int i = 0; i < hostCount; i++) {
            addresses[i] = makeTransportAddress(hosts[i]);
         }
         
         Settings settings = Settings.builder().put("cluster.name", elasticClusterName).build();
         transportClient = new PreBuiltTransportClient(settings)
               .addTransportAddresses(addresses);
         
         
         initBulkProcessor();
         
         
      }
      
      private void initBulkProcessor() {
         bulkProcessor = BulkProcessor.builder(transportClient,
               new BulkProcessor.Listener() {
                  @Override
                  public void beforeBulk(long executionId, BulkRequest request) {
                  }
                  
                  @Override
                  public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                     count += request.requests().size();
                  }
                  
                  @Override
                  public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
//                     logger.debug(failure.toString());
//                     logger.error("[info] bulk failed!");
                     System.out.println(failure.toString());
                     System.out.println("[info] bulk failed!");
//                     failure.printStackTrace();
                  }
               })
               .setBulkActions(2048)
               .setBulkSize(new ByteSizeValue(128, ByteSizeUnit.MB))
               .setFlushInterval(TimeValue.timeValueSeconds(30))
               .build();
      }
      
      private InetSocketTransportAddress makeTransportAddress(String host) {
         
         try {
            return new InetSocketTransportAddress(
                  new InetSocketAddress(InetAddress.getByName(host), port));
         } catch (UnknownHostException ex) {
//            logger.error(ex.toString());
            System.out.println(ex.toString());
         }
         
         return null;
      }
      
      @Override
      public void run() {
         while (true) {
            try {
               WebPage newWebPage = indexQueue.take();
               lock.acquire();
               String url = newWebPage.getUrl();
               if (url == null || url.length() == 0) {
                  continue;
               }
               bulkProcessor.add(
                     new IndexRequest(index, type,
                           url.length() < 512 ? url : String.valueOf(url.hashCode()))
                           .source(jsonMaker.toJson(newWebPage)));
            } catch (InterruptedException ex) {
               flush();
               System.out.println("[info] indexing done!");
               transportClient.close();
            } catch (Exception ex) {
//               logger.error(ex.toString());
               System.out.println(ex.toString());
            } finally {
               lock.release();
            }
         }
      }
      
      public void flush() {
         try {
            lock.acquire();
            bulkProcessor.flush();
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         } finally {
            lock.release();
         }
      }
   }
   
}
   
