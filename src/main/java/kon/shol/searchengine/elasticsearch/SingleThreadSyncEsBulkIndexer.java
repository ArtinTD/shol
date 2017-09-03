package kon.shol.searchengine.elasticsearch;

import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class SingleThreadSyncEsBulkIndexer implements EsIndexer {
   
   private final static Logger logger = Logger.getLogger("custom");
   
   private String indexActionLine;
   private LinkedBlockingQueue<WebPage> indexQueue = new LinkedBlockingQueue<>();
   private String[] hosts;
   private String index;
   private String type;
   private Sender indexer;
   private int port;
   
   public SingleThreadSyncEsBulkIndexer(String[] hosts, String index, String type) {
      this(hosts, 9200, index, type);
   }
   
   public SingleThreadSyncEsBulkIndexer(String[] hosts, int port, String index, String type) {
      this.hosts = hosts;
      this.port = port;
      this.index = index;
      this.type = type;
      indexActionLine =
            "{ \"index\": {}}\n";
      
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
      
      private final Semaphore lock = new Semaphore(1);
      private final int hostCount = hosts.length;
      private final String endpoint = "/" + index + "/" + type + "/_bulk";
      private final Gson jsonMaker = new Gson();
      private RestClient restClient;
      private long count = 0;
      private StringBuilder body = new StringBuilder();
      
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
               lock.acquire();
               body.append(indexActionLine);
               body.append(jsonMaker.toJson(newWebPage));
               body.append("\n");
               count++;
               if (count > 128) {
                  flush();
               }
               lock.release();
            } catch (InterruptedException ex) {
               lock.release(); // CHECK: is necessary?
               flush();
               logger.info("indexing done!");
            } catch (Exception ex) {
               logger.error(ex.toString());
            } finally {
               closeClient();
            }
         }
      }
      
      private void closeClient() {
         try {
            restClient.close();
         } catch (IOException ex) {
            logger.error(ex.toString());
         }
      }
      
      
      public void flush() {
         
         try {
            lock.acquire();
         } catch (InterruptedException ex) {
            logger.error(ex.toString());
            lock.release();
            return;
         }
         
         try {
            bulkSend(new StringEntity(body.toString(), ContentType.APPLICATION_JSON));
         } catch (IOException e) {
            logger.error(e.toString());
         }
         
         body = new StringBuilder();
         count = 0;
         lock.release();
         
      }
      
      // TODO: read response and check for errors.
      // TODO: Ideally use Java API instead of REST API.
      private void bulkSend(StringEntity body) throws IOException {
         
         Response response = restClient.performRequest("POST",
               endpoint, Collections.emptyMap(), body);
         String resultString = EntityUtils.toString(response.getEntity()).trim();
         System.out.println("[info] " + new Date().toString() + " : errors: "
               + new JSONObject(resultString)
               .getString("errors"));
      }
      
   }
   
}