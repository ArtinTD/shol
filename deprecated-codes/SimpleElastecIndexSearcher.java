package kon.shol;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;

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



    @Override
    public void search(int numberOfResults, String query) {
        Header[] headers = { new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
                new BasicHeader("Role", "Read") };
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        RestClient restClient = RestClient.builder(new HttpHost(this.host, this.port))
                .setDefaultHeaders(headers)
                .setHttpClientConfigCallback(arg0 -> arg0.setDefaultCredentialsProvider(credentialsProvider))
                .build();


        HttpEntity entity1 = new NStringEntity(
                "{\n" +
                        "\"size\" : "+ Integer.toString(numberOfResults) + ","
                        +
                        "  \"query\": {\n" +
                        "    \"multi_match\" : {\n" +
                        "      \"query\":    \"" + query + "\", \n" +
                        "      \"fields\": [ \"text\", \"description\", \"url\" ] \n" +
                        "    }\n" +
                        "  }\n" +
                        "}", ContentType.APPLICATION_JSON);

        Response response = null;
        try {
            response = restClient.performRequest("GET", "/"+ this.index +"/_search",
                    Collections.singletonMap("pretty", "true"),
                    entity1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String s = EntityUtils.toString(response.getEntity()).trim();
//            System.out.println(s);
            searchParser(s);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void searchParser(String jsonString){
        System.out.println("######### BEST RESULTS  ##########\n");
        JSONObject object = new JSONObject(jsonString);
        JSONObject hits = object.getJSONObject("hits");
        JSONArray arrayHits = hits.getJSONArray("hits");
        for (int i = 0; i < arrayHits.length(); i++) {
            System.out.println( "NUMBER " +Integer.toString(i+1) + "\n" );
            JSONObject jsonObjectResult = new JSONObject(arrayHits.get(i).toString());

            String score = Double.toString((Double) jsonObjectResult.get("_score"));
            System.out.println("    score " + Integer.toString(i)+ "  :  "+ score);

            JSONObject source = jsonObjectResult.getJSONObject("_source");

            String url = source.getString("url");
            System.out.println("    url " + Integer.toString(i)+ "  :  " + url);

            String title = source.getString("title");
            System.out.println("    title " + Integer.toString(i)+ "  :  " + title);

            String text = source.getString("text");
            System.out.println("    text " + Integer.toString(i)+ "  :  " + text);

            String description = source.getString("description");
            System.out.println("    description " + Integer.toString(i)+ "  :  " + description);

            String h1h3 = source.getString("h1h3");
            System.out.println("    h1h3 " + Integer.toString(i)+ "  :  " + h1h3);

            String h4h6 = source.getString("h4h6");
            System.out.println("    h4h6 " + Integer.toString(i)+ "  :  " + h4h6);

            String imagesAlt = source.getString("imagesAlt");
            System.out.println("    imagesAlt " + Integer.toString(i)+ "  :  " + imagesAlt);

            String pageRank = Double.toString((Double) source.get("pageRank"));
            System.out.println("    pageRank " + Integer.toString(i)+ "  :  " + pageRank);

        }
    }
}
