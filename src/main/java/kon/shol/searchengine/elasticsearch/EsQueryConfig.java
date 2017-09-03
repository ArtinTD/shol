package kon.shol.searchengine.elasticsearch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EsQueryConfig {
   
   public static final EsQueryConfig DEFAULT_CONFIG;
   
   private static final String[] DEFAULT_TO_STRING =
         new String[]{"title"};
   
   static {
      DEFAULT_CONFIG = new EsQueryConfig()
            .addField("title", 2F)
            .addField("description", 1.7F)
            .addField("text", 1F)
            .addField("h1h3", 1.7F)
            .addField("h4h6", 1.4F)
            .addField("imagesAlt", 1.7F)
            .addField("anchorTexts", 1.6F);
   }
   
   private HashMap<String, Float> fields;
   private String[] toStringArray;
   private boolean toStringChanged = true;
   
   public EsQueryConfig(Map<String, Float> fields) {
      fields = new HashMap<String, Float>(fields);
   }
   
   public EsQueryConfig() {
      fields = new HashMap<String, Float>();
   }
   
   public EsQueryConfig addField(String field, Float weight) {
      
      if (!fields.containsKey(field)) {
         fields.put(field, weight);
      } else {
         fields.replace(field, weight);
      }
      toStringChanged = true;
      return this;
   }
   
   public String[] toStringArray() {
      
      if (!toStringChanged) {
         return toStringArray;
      }
      
      if (fields.isEmpty()) {
         return DEFAULT_TO_STRING;
      }
      
      String[] result = new String[fields.size()];
      Iterator<String> fieldNames = fields.keySet().iterator();
      
      for (int i = 0; i < fields.size(); i++) {
         String field = fieldNames.next();
         Float weight = fields.get(field);
         result[i] = field +
               (!weight.equals(1F) ? ("^" + weight.toString()) : "");
      }
      
      toStringArray = result;
      toStringChanged = false;
      return toStringArray;
   }
}
