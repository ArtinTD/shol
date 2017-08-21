package kon.shol;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleElasticIndexer implements ElasticIndexer {
    private Sender sender;
    private LinkedBlockingQueue<WebPage> indexQueue = new LinkedBlockingQueue<WebPage>();
    private String index, type;
    private String host;
    private int port;

    public SimpleElasticIndexer(String host, String index, String type) {
        this(host, 9200, index, type);
    }

    public SimpleElasticIndexer(String host, int port, String index, String type) {
        this.host = host;
        this.port = port;
        this.index = index;
        this.type = type;
        sender = new Sender();
        sender.setName("pageIndexerThread");
        sender.start();
    }

    //for test
    public static void main(String[] args) {
        ElasticIndexer ei = new SimpleElasticIndexer("188.165.230.122", "test", "testt");
        Scanner s = new Scanner(System.in);
        for (int i = 0; i < 5; i++)
            ei.add(s.nextLine(), s.nextLine());
        s.close();
    }

    public void close() { sender.interrupt(); }

    public boolean isWorking() { return !indexQueue.isEmpty(); }

    @Override
    public void add(String url, String webPage) {
        try { indexQueue.put(new WebPage(url, webPage)); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    class Sender extends Thread {
        @Override
        public void run() {
            RestClient restClient = RestClient.builder(
                    new HttpHost(host, port, "http")).build();
            Gson gson = new Gson();
            try {
                while (true) { // TODO: add some kind of safe stopping mechanism.
                    WebPage newWP = indexQueue.take();
                    HttpEntity en;
                    en = new StringEntity(gson.toJson(newWP), ContentType.APPLICATION_JSON);
                    restClient.performRequestAsync(
                            "POST",
                            "/" + index + "/" + type + "/",
                            Collections.<String, String>emptyMap(),
                            en,
                            new ResponseListener() {
                                @Override
                                public void onSuccess(Response response) {
                                    System.out.println(new Date().toString()
                                            + " : Index successful @/" + index + "/" + type);
                                }

                                @Override
                                public void onFailure(Exception exception) {
                                    System.err.println(new Date().toString()
                                            + " : Index failed @/" + index + "/" + type);
                                    exception.printStackTrace();
                                }
                            }
                    );
                }
            }
            catch (InterruptedException e) { e.printStackTrace(); }
            finally {
                try { restClient.close(); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    private class WebPage {
        String url;
        String text;

        public WebPage(String url, String text) {
            this.url = url;
            this.text = text;
        }
    }
}