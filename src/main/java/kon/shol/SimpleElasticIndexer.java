package kon.shol;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
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
//    public static void main(String[] args) {
//        ElasticIndexer ei = new SimpleElasticIndexer("188.165.230.122", "test", "testt");
//        Scanner s = new Scanner(System.in);
//        for (int i = 0; i < 5; i++)
//            ei.add(s.nextLine(), s.nextLine());
//        s.close();
//    }

    public void close() { sender.interrupt(); }

    public boolean isWorking() { return !indexQueue.isEmpty(); }

    @Override
    public void add(String url, String title, String text, String description, String h1h3, String h4h6, String imagesAlt, double pageRank) {
        try { indexQueue.put(new WebPage(url, title, text, description, h1h3, h4h6, imagesAlt, pageRank)); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }

    class Sender extends Thread {
        @Override
        public void run() {
            RestClient restClient = RestClient.builder(
                    new HttpHost(host, port, "http")).build();
            Gson gson = new Gson();
            try {
                long l = 0;
                while (true) { // TODO: add some kind of safe stopping mechanism.
                    WebPage newWP = indexQueue.take();
                    HttpEntity en = new StringEntity(gson.toJson(newWP), ContentType.APPLICATION_JSON);
                    try {
                        Response response = restClient.performRequest(//Async(
                                "POST",
                                "/" + index + "/" + type + "/",
                                Collections.<String, String>emptyMap(),
                                en//,
    //                            new ResponseListener() {
    //                                @Override
    //                                public void onSuccess(Response response) {
    //                                    System.out.println(new Date().toString()
    //                                            + " : Index successful @/" + index + "/" + type);
    //                                }
    //
    //                                @Override
    //                                public void onFailure(Exception exception) {
    //                                    System.err.println(new Date().toString()
    //                                            + " : Index failed @/" + index + "/" + type);
    //                                    exception.printStackTrace();
    //                                }
    //                            }
                        );
                        System.out.println("[INFO] index result: " + response.getStatusLine().getReasonPhrase() + " : " + ++l + " @/" + index + "/" + type);
                    }
                    catch (IOException e) { e.printStackTrace(); }
                }
            }
            catch (InterruptedException e) { System.out.println("[INFO]" + new Date().toString() + " : index operation completed @/" + index + "/" + type); }
            finally {
                try { restClient.close(); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    private class WebPage {
        String url, title, text, description, h1h3, h4h6, imagesAlt;
        double pageRank;

        public WebPage(String url, String title, String text, String description, String h1h3, String h4h6, String imagesAlt, double pageRank) {
            this.url = url;
            this.title = title;
            this.text = text;
            this.description = description;
            this.h1h3 = h1h3;
            this.h4h6 = h4h6;
            this.imagesAlt = imagesAlt;
            this.pageRank = pageRank;
        }
    }
}