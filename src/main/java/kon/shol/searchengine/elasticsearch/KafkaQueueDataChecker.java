package kon.shol.searchengine.elasticsearch;

import kon.shol.searchengine.kafka.Consumer;

import java.util.Properties;

public class KafkaQueueDataChecker {
   static final String TOPIC_NAME = "elasticQueue";
   
   public static void main(String[] args) throws InterruptedException {
      
      Properties properties = new Properties();
      Consumer consumer = new Consumer("1",
            TOPIC_NAME, properties);
      String s = consumer.get();
      int i = 0;
      while (true) {
         System.out.println(i + " : " + s);
         s = consumer.get();
      }
   }
}
