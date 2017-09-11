package kon.shol.searchengine.elasticsearch;

import kon.shol.searchengine.kafka.ElasticQueue;

public class KafkaQueueFeeder {
   static final int periodLength = 60000;
   static long seed = 1504323000000L;
   static ElasticQueue queue = new ElasticQueue("ElasticQueueT3","std");
   
   public static void main(String[] args) {
      while (true) {
         if (seed + periodLength < System.currentTimeMillis()) {
            queue.send(String.valueOf(seed));
            System.out.println(seed);
            seed += periodLength;
         } else {
            try {
               Thread.sleep(15000);
            } catch (InterruptedException ignored) {
            }
         }
      }
      
   }
}