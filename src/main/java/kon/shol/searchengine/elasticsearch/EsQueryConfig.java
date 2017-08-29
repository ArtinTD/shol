package kon.shol.searchengine.elasticsearch;

import java.util.HashMap;
import java.util.Map;

public class EsQueryConfig {
   
   public static final EsQueryConfig DEFAULT_CONFIG;
   
   static {
      DEFAULT_CONFIG = new EsQueryConfig()
            .addField("title", 2F)
            .addField("description", 1.7F)
            .addField("txt", 1F)
            .addField("h1h3", 1.7F)
            .addField("h4h6", 1.4F)
            .addField("imagesAlt", 1.7F)
            .addField("anchorTexts", 1.6F);
   }
   
   private HashMap<String, Float> fields;
   private String toString;
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
   
   @Override
   public String toString() {
      
      if (!toStringChanged) {
         return toString;
      }
      
      if (fields.isEmpty()) {
         return "[\"text\"]";
      }
      
      StringBuilder result = new StringBuilder("[");
      boolean newResult = true;
      
      for (String field : fields.keySet()) {
         if (!newResult) {
            result.append(", ");
         }
         result.append("\"");
         result.append(field);
         Float weight = fields.get(field);
         if (!weight.equals(1F)) {
            result.append("^");
            result.append(weight.toString());
         }
         result.append("\"");
         newResult = false;
      }
      result.append("]");
      
      toString = result.toString();
      toStringChanged = false;
      return toString;
   }
}
