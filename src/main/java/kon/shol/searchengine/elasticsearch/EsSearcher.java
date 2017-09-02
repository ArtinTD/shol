package kon.shol.searchengine.elasticsearch;

public interface EsSearcher {
   public EsSearchResult[] search(String query);
   
   public EsSearchResult[] search(String query, EsQueryConfig queryConfig, int from, int count);
}
