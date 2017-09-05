package kon.shol.searchengine.elasticsearch;

import kon.shol.searchengine.kafka.ElasticQueue;

public class KafkaQueueFeeder {
   static final String TOPIC_NAME = "elasticQueue";
   static long seed = 1503730220000L;
   static ElasticQueue queue = new ElasticQueue();
   
   public static void main(String[] args) {
      for (int i = 0; i < 1000; i++) {
         System.out.println(i + " : " + seed);
         queue.send(String.valueOf(seed));
         seed -= 600000L;
      }
   }
}