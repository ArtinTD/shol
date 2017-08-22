package kon.shol;

import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class SimpleElastecIndexSearcher implements ElasticIndexSearcher {
    private String host, index, type;
    private int port;

    public SimpleElastecIndexSearcher(String host, String index, String type) {
        this(host, 9200, index, type);
    }

    public SimpleElastecIndexSearcher(String host, int port, String index, String type) {
        this.host = host;
        this.port = port;
        this.index = index;
        this.type = type;
    }

    public static void main(String[] args) {
        SimpleElastecIndexSearcher seis =
                new SimpleElastecIndexSearcher("188.165.230.122", 9200, "test", "testt");
        Scanner s = new Scanner(System.in);
        for (int i = 0; i < 5; i++)
            System.out.println(seis.search(s.nextLine()));
        s.close();
    }

    @Override
    public String search(String query) {
        CountDownLatch latch = new CountDownLatch(1);
        final String[] result = new String[1];
        RestClient restClient = RestClient.builder(
                new HttpHost(host, port, "http")).build();

        try {

            StringEntity en = new StringEntity("{ \"query\": { \"match\": { \"text\": \"" + query + "\" } } }", ContentType.APPLICATION_JSON);


            restClient.performRequestAsync(
                    "GET",
                    "/" + index + "/" + type + "/_search",
                    Collections.<String, String>emptyMap(),
                    en,
                    new ResponseListener() {
                        @Override
                        public void onSuccess(Response response) {
                            System.out.println(new Date().toString()
                                    + " : search successful @/" + index + "/" + type);
                            try {
                                result[0] = EntityUtils.toString(response.getEntity());
                            } catch (IOException e) {
                                result[0] = "Failed to read response.";
                                e.printStackTrace();
                            }
                            latch.countDown();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            System.err.println(new Date().toString()
                                    + " : search failed @/" + index + "/" + type);
                            exception.printStackTrace();
                            result[0] = "Failed.";
                            latch.countDown();
                        }
                    }
            );

            latch.await();
            return result[0];


        } catch (InterruptedException e) {
            e.printStackTrace();
            return "An error occurred... sorry:(";
        } finally {
            try {
                restClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
