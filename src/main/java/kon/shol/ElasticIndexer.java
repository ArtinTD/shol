package kon.shol;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

public class ElasticIndexer implements HBaseToElasticIndex {
    private Sender sender;
    private LinkedBlockingQueue<String> indexQueue = new LinkedBlockingQueue<String>();
    private String index, type;
    private String host;
    private int port;

    public ElasticIndexer(String host, String index, String type) {
        this(host, 9200, index, type);
    }

    public ElasticIndexer(String host, int port, String index, String type) {
        this.host = host;
        this.port = port;
        this.index = index;
        this.type = type;
        sender = new Sender();
        sender.setName("pageIndexerThread");
        sender.start();
    }

    @Override
    public void add(String newIndex) {
        try {
            indexQueue.put(newIndex);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Sender extends Thread {
        boolean run = true;

        @Override
        public void run() {
            RestClient restClient = RestClient.builder(
                    new HttpHost(host, port, "http")).build();

            try {
                while (run) { // TODO: add some kind of safe stopping mechanism.
                    try {
                        HttpEntity en;
                        try {
                            en = new StringEntity("{ " + indexQueue.take() + " }");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            continue;
                        }
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
                                    }
                                }
                        );


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                try {
                    restClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
