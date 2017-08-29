package kon.shol.searchengine.elasticsearch;

public class SearchQuery {
  private QueryObject query;
  
  public SearchQuery(String query) {
    this(query, EsQueryConfig.defaultConfig());
  }
  
  public SearchQuery(String query, EsQueryConfig config) {
    this.query = new QueryObject(query, config);
  }
  
  public QueryObject getQuery() {
    return query;
  }
  
  private class QueryObject {
    private MultiMatchObject multi_match;
    
    public QueryObject(String query, EsQueryConfig config) {
      multi_match = new MultiMatchObject(query, config);
    }
    
    public MultiMatchObject getMulti_match() {
      return multi_match;
    }
    
    private class MultiMatchObject {
      private String query;
      private String fields;
      
      public MultiMatchObject(String query, EsQueryConfig config) {
        this.query = query;
        this.fields = config.toString();
      }
      
      public String getQuery() {
        return query;
      }
      
      public String getFields() {
        return fields;
      }
    }
  }
}
