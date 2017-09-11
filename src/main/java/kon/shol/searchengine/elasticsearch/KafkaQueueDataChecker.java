package kon.shol.searchengine.elasticsearch;

import kon.shol.searchengine.kafka.ElasticQueue;

public class KafkaQueueDataChecker {
   
   public static void main(String[] args) throws InterruptedException {
      
      ElasticQueue queue = new ElasticQueue("ElasticQueueT3", "tone");
      String s = (String) queue.get();
      System.out.println(s);
      int i = 0;
      while (true) {
         System.out.println(i++ + " : " + s);
         s = (String) queue.get();
      }
   }
   
}
