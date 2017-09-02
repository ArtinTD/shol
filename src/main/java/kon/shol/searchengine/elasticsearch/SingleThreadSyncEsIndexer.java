package kon.shol.searchengine.elasticsearch;

import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleThreadSyncEsIndexer implements EsIndexer {
   
   private final static Logger logger = Logger.getLogger(
         kon.shol.searchengine.elasticsearch.SingleThreadSyncEsIndexer.class);
   
   private LinkedBlockingQueue<WebPage> indexQueue = new LinkedBlockingQueue<>();
   private String[] hosts;
   private String index;
   private String type;
   private Sender indexer;
   private int port;
   
   public SingleThreadSyncEsIndexer(String[] hosts, String index, String type) {
      this(hosts, 9200, index, type);
   }
   
   public SingleThreadSyncEsIndexer(String[] hosts, int port, String index, String type) {
      this.hosts = hosts;
      this.port = port;
      this.index = index;
      this.type = type;
      
      indexer = new Sender();
      indexer.setName("SingleThreadSyncEsIndexer-indexerThread");
      indexer.start();
   }
   
   public boolean isDone() {
      return indexQueue.isEmpty();
   }
   
   public void end() {
      indexer.interrupt();
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
      private final String endpoint = "/" + index + "/" + type + "/";
      private final Gson jsonMaker = new Gson();
      private RestClient restClient;
      private long count = 0;
      
      private Sender() {
         
         HttpHost[] httpHosts = new HttpHost[hostCount];
         for (int i = 0; i < hostCount; i++) {
            httpHosts[i] = new HttpHost(hosts[i], port, "http");
         }
         restClient = RestClient.builder(httpHosts).build();
      }
      
      @Override
      public void run() {
         while (true) {
            try {
               WebPage newWebPage = indexQueue.take();
               Response response = performRequest(newWebPage); // throws IOException
               log(response);
            } catch (IOException ex) {
               logger.error("index error: " + ex.toString());
            } catch (InterruptedException ex) {
               logger.info("indexing done!");
            } finally {
               logger.info("index operation over @" + endpoint);
               closeClient();
            }
         }
      }
      
      private Response performRequest(WebPage newWebPage) throws IOException {
         return restClient.performRequest(
               "POST",
               endpoint,
               Collections.emptyMap(),
               new StringEntity(jsonMaker.toJson(newWebPage), ContentType.APPLICATION_JSON)
         );
      }
      
      private void log(Response response) {
         logger.info(
               "index result: " + response.getStatusLine().getReasonPhrase()
                     + " : " + ++count
                     + " @" + endpoint
         );
      }
      
      private void closeClient() {
         try {
            restClient.close();
         } catch (IOException ex) {
            logger.error(ex.toString());
         }
      }
   }
}
