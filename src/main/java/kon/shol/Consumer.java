package kon.shol;

import kafka.api.FetchResponse;
import org.apache.kafka.clients.consumer.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.kafka.common.TopicPartition;

public class Consumer {

    private static ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(10000000);
    private static KafkaConsumer<String, String> kafkaConsumer;

    Consumer(){

        Properties configProperties = new Properties();
        configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "188.165.230.122:9092");
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "0");
        configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, "simple");
    }

    public String kafkaGetLink1(String topicName, String groupId) throws InterruptedException {

        kafkaConsumer.subscribe(Arrays.asList("arash"));
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Long.MAX_VALUE);
            for (ConsumerRecord<String, String> record : records) {
                arrayBlockingQueue.put(record.value());
            }
        return arrayBlockingQueue.poll().toString();
    }
}
