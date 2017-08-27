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
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

public class SingleThreadSyncEsIndexer implements EsIndexer {
  
  final static Logger logger = Logger.getLogger(
      kon.shol.searchengine.elasticsearch.SingleThreadSyncEsIndexer.class);
  private LinkedBlockingQueue<WebPage> indexQueue = new LinkedBlockingQueue<WebPage>();
  private String host;
  private String index;
  private String type;
  private int port;
  private Sender indexer;
  
  public SingleThreadSyncEsIndexer(String host, String index, String type) {
    this(host, 9200, index, type);
  }
  
  public SingleThreadSyncEsIndexer(String host, int port, String index, String type) {
    this.host = host;
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
    } catch (InterruptedException expected) {
    }
  }
  
  private class Sender extends Thread {
    private final RestClient restClient = RestClient.builder(new HttpHost(host, port, "http")).build();
    private final String endpoint = "/" + index + "/" + type + "/";
    private final Gson jsonMaker = new Gson();
    private long count = 0;
    
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
          logger.error("indexing done!");
        } finally {
          logger.info(new Date().toString() + " : index operation over @" + endpoint);
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
