package kon.shol.searchengine.elasticsearch;

import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;

public class EsSearcherImpl implements EsSearcher {
  private final static Logger logger = Logger.getLogger(
      kon.shol.searchengine.elasticsearch.EsSearcherImpl.class);
  private final Gson jsonMaker = new Gson();
  private String endpoint;
  private RestClient restClient;
  
  public EsSearcherImpl(String[] hosts, String endpoint) {
    this(hosts, 9200, endpoint);
  }
  
  public EsSearcherImpl(String[] hosts, int port, String endpoint) {
    setEndpoint(endpoint);
    buildRestClient(hosts, port);
    
  }
  
  private void setEndpoint(String endpoint) {
    this.endpoint = (endpoint.charAt(0) == '/' ? "" : "/")
        + endpoint
        + (endpoint.charAt(endpoint.length() - 1) == '/' ? "_search" : "/_search");
  }
  
  private void buildRestClient(String[] hosts, int port) {
    HttpHost[] httpHosts = new HttpHost[hosts.length];
    for (int i = 0; i < hosts.length; i++) {
      httpHosts[i] = new HttpHost(hosts[i], port, "http");
    }
    restClient = RestClient.builder(httpHosts).setDefaultHeaders(new Header[]{
        new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
        new BasicHeader("Role", "Read")}
    ).build();
  }
  
  @Override
  public EsSearchResult[] search(String query) {
    return search(query, EsQueryConfig.defaultConfig(), 0, 20);
  }
  
  @Override
  public EsSearchResult[] search(String query, EsQueryConfig queryConfig, int from, int count) {
    HttpEntity queryJson = new StringEntity(
        jsonMaker.toJson(new SearchQuery(query, queryConfig)), ContentType.APPLICATION_JSON);
    Response response;
    String resultString;
    try {
      response = restClient.performRequest("GET", endpoint
          , Collections.singletonMap("pretty", "true"), queryJson);
      resultString = EntityUtils.toString(response.getEntity()).trim();
    } catch (IOException ex) {
      logger.error(ex.toString());
      return null;
    }
    JSONArray hits = new JSONObject(resultString)
        .getJSONObject("hits")
        .getJSONArray("hits");
    EsSearchResult[] searchResults = new EsSearchResult[hits.length()];
    for (int i = 0; i < hits.length(); i++) {
      JSONObject hit = hits.getJSONObject(i).getJSONObject("_source");
      searchResults[i] = new EsSearchResult(
          hit.getString("url")
          , hit.getString("title")
          , hit.getString("description"));
    }
    return searchResults;
  }
}
