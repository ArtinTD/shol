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

public class Test {
    public static void main(String[] args) {


        Header[] headers = { new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
                new BasicHeader("Role", "Read") };
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        RestClient restClient = RestClient.builder(new HttpHost("188.165.235.136", 9200))
                .setDefaultHeaders(headers)
                .setHttpClientConfigCallback(arg0 -> arg0.setDefaultCredentialsProvider(credentialsProvider))
                .build();

        HttpEntity entity1 = new NStringEntity(
                "{\n" +
                        "\"size\" : 10,"
                        +
                        "  \"query\": {\n" +
                        "    \"multi_match\" : {\n" +
                        "      \"query\":    \"hello world\", \n" +
                        "      \"fields\": [ \"text\", \"description\", \"url\" ] \n" +
                        "    }\n" +
                        "  }\n" +
                        "}", ContentType.APPLICATION_JSON);

        Response response = null;
        try {
            response = restClient.performRequest("GET", "/shol/_search",
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
            System.out.println(arrayHits.get(i).toString());
            JSONObject jsonObjectResult = new JSONObject(arrayHits.get(i).toString());
            String score = Double.toString((double) jsonObjectResult.get("_score"));
            System.out.println("score "+Integer.toString(i) +"  :  "+ score);

//            JSONObject data = new JSONObject(jsonObjectResult.get("_source"));
//            System.out.println(data.toString());

        }
    }
}