package kon.shol.searchengine.kafka;

import kon.shol.searchengine.crawler.Queue;

import java.util.Properties;

import static org.apache.kafka.clients.consumer.ConsumerConfig.FETCH_MAX_BYTES_CONFIG;

public class ElasticQueue implements Queue {
   
   private String topic;
   private Producer producer;
   private Consumer consumer;
   
   public ElasticQueue(String topic, String groupId) {
      this.topic = topic;
      
      
      producer = new Producer();
      
      Properties properties = new Properties();
      properties.put(FETCH_MAX_BYTES_CONFIG, "10000");
      consumer = new Consumer(groupId, topic, properties);
      Thread elasticKafkaConsumingThread = new Thread(consumer);
      elasticKafkaConsumingThread.start();
   }
   
   @Override
   public Object get() throws InterruptedException {
      
      Object o = consumer.get();
      return o;
   }
   
   @Override
   public void send(Object element) {
      
      String toSend = (String) element;
      producer.send(toSend, topic);
   }
}