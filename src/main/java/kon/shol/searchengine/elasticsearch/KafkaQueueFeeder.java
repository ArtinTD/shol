package kon.shol.searchengine.elasticsearch;

import kon.shol.searchengine.kafka.Producer;

public class KafkaQueueFeeder {
   static final String TOPIC_NAME = "elasticQueue";
   static long seed = 1503730220000L;
   static Producer feeder = new Producer();
   
   public static void main(String[] args) {
      for (int i = 0; i < 1000; i++) {
         System.out.println(i + " : " + seed);
         feeder.send(String.valueOf(seed), TOPIC_NAME);
         seed -= 600000L;
      }
   }
}