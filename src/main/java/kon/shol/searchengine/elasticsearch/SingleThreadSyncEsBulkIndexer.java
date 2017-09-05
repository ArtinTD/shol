package kon.shol.searchengine.elasticsearch;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class SingleThreadSyncEsBulkIndexer implements EsIndexer {
   
   private final static Logger logger = Logger.getLogger(
         kon.shol.searchengine.elasticsearch.SingleThreadSyncEsIndexer.class);
   
   private Semaphore lock = new Semaphore(1);
   private static LinkedBlockingQueue<WebPage> indexQueue = new LinkedBlockingQueue<>();
   private String[] hosts;
   private String index;
   private String type;
   private Sender indexer;
   private int port;
   
   public SingleThreadSyncEsBulkIndexer(String[] hosts, String index, String type) {
      this(hosts, 9300, index, type);
   }
   
   public SingleThreadSyncEsBulkIndexer(String[] hosts, int port, String index, String type) {
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
         
         Settings settings = Settings.builder().put("cluster.name", "sholastic").build();
         transportClient = new PreBuiltTransportClient(settings)
               .addTransportAddresses(addresses);
         
         
         initBulkProcessor();
         
         
      }
      
      private void initBulkProcessor() {
         bulkProcessor = BulkProcessor.builder(transportClient,
               new BulkProcessor.Listener() {
                  @Override
                  public void beforeBulk(long executionId, BulkRequest request) {
                     logger.info("[info] gonna bulk!");
                  }
                  
                  @Override
                  public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                     logger.info("[info] bulked!");
                  }
                  
                  @Override
                  public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                     logger.debug(failure.toString());
                     logger.error("[info] bulk failed!");
                     failure.printStackTrace();
                  }
               })
               .setBulkActions(-1)
               .setBulkSize(new ByteSizeValue(-1, ByteSizeUnit.MB))
               .setFlushInterval(TimeValue.timeValueSeconds(60))
               .build();
      }
      
      private InetSocketTransportAddress makeTransportAddress(String host) {
         
         try {
            return new InetSocketTransportAddress(
                  new InetSocketAddress(InetAddress.getByName(host), port));
         } catch (UnknownHostException ex) {
            logger.error(ex.toString());
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
               bulkProcessor.add(
                     new IndexRequest(index, type,
                           url.length() < 512 ? url : String.valueOf(url.hashCode()))
                           .source(jsonMaker.toJson(newWebPage)));
            } catch (InterruptedException ex) {
               flush();
               logger.info("indexing done!");
               transportClient.close();
            } catch (Exception ex) {
               logger.error(ex.toString());
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