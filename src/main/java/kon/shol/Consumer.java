package kon.shol;

import kafka.api.FetchResponse;
import org.apache.kafka.clients.consumer.*;

import org.apache.kafka.clients.consumer.ConsumerConfig.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.kafka.common.TopicPartition;

public class Consumer {

    KafkaConsumer<String, String> consumer;

    public Consumer(int groupID, String topic) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "188.165.235.136:9092,188.165.230.122:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, String.valueOf(groupID));
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        props.put("zookeeper.connect", "188.165.230.122:2181");
        props.put("zookeeper.connection.timeout.ms", "6000");
        props.put("consumer.timeout.ms", "5000");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public void getLink() {

        System.out.println("ab");
        ConsumerRecords<String, String> records = consumer.poll(1000);
        System.out.println("khiar");
        if (records.isEmpty()) {
            System.out.println("empty");
        }
        for (ConsumerRecord<String, String> record : records)
        {
            System.out.println("xx");
            System.out.println(record.value());
        }
    }
}
