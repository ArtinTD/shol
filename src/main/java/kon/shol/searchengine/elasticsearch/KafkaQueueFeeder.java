package kon.shol.searchengine.elasticsearch;

import kon.shol.searchengine.kafka.ElasticQueue;

public class KafkaQueueFeeder {
   static final int periodLength = 60000;
   static long seed = 1503730220000L;
   static ElasticQueue queue = new ElasticQueue("std");
   
   public static void main(String[] args) {
      while (true) {
         if (seed + periodLength < System.currentTimeMillis()) {
            queue.send(String.valueOf(seed));
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