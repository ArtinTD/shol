package kon.shol;

import kafka.api.FetchResponse;
import org.apache.kafka.clients.consumer.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.kafka.common.TopicPartition;

public class Consumer {

    public static ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(1000000);

    public void getLink() throws InterruptedException {

        String topicName = "arash";
        String groupId = "0";

        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "188.165.230.122:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
        kafkaConsumer.subscribe(Arrays.asList(topicName));
        System.out.println("consumer run");

        while (true){
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.MAX_VALUE);
            for (ConsumerRecord<String, String> record : records) {
                arrayBlockingQueue.put(record.value());
            }
        }
    }
}
