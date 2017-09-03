package kon.shol.searchengine.elasticsearch;

import com.google.gson.annotations.Expose;

public class SearchQuery {
   @Expose private BaseQueryObject query;
   
   public SearchQuery(String query) { this (query, EsQueryConfig.DEFAULT_CONFIG); }
   public SearchQuery(String query, EsQueryConfig config) { this.query = new BaseQueryObject(query, config); }
   
   public BaseQueryObject getQuery() { return query; }
   
   
   private class BaseQueryObject {
      @Expose private FunctionScoreObject function_score;
      
      public FunctionScoreObject getFunction_score() { return function_score; }
      
      private BaseQueryObject(String query, EsQueryConfig config) { function_score = new FunctionScoreObject(query, config); }
      
      private class FunctionScoreObject {
         @Expose private QueryObject query;
         @Expose private FieldValueFactor field_value_factor =
               new FieldValueFactor("pageRank");
         
         public QueryObject getQuery() { return query; }
         public FieldValueFactor getField_value_factor() { return field_value_factor; }
         
         private FunctionScoreObject(String query, EsQueryConfig config) { this.query = new QueryObject(query, config); }
         
         
         
         private class QueryObject {
            
            @Expose private MultiMatchObject multi_match;
            
            private QueryObject(String query, EsQueryConfig config) { multi_match = new MultiMatchObject(query, config); }
            
            public MultiMatchObject getMulti_match() { return multi_match; }
            
            private class MultiMatchObject {
               
               @Expose private String query;
               @Expose private final String type = "cross_fields";
               @Expose private String[] fields;
               
               public MultiMatchObject(String query, EsQueryConfig config) { this.query = query; this.fields = config.toStringArray(); }
               
               public String getQuery() { return query; }
               public String getType() { return type; }
               public String[] getFields() { return fields; }
            }
         }
         
         private class FieldValueFactor {
            private String field;
            
            private FieldValueFactor(String field) { this.field = field; }
            
            public String getField() { return field; }
         }
      }
   }
   
}