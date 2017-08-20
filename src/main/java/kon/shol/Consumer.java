package kon.shol;

import org.apache.kafka.clients.consumer.*;

import java.util.*;

import static kon.shol.Main.consumerQueue;

public class Consumer implements Runnable {

    KafkaConsumer<String, String> consumer;

    public Consumer(String groupID, String topic) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "188.165.235.136:9092,188.165.230.122:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        props.put("zookeeper.connect", "188.165.230.122:2181");
        props.put("zookeeper.connection.timeout.ms", "6000");
        props.put("consumer.timeout.ms", "5000");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public void getLink() throws InterruptedException {
        ConsumerRecords<String, String> records = consumer.poll(100);
        for (ConsumerRecord<String, String> record : records) {
            consumerQueue.put(record.value());
        }
    }

    public void run() {
        while (true) {
            try {
                getLink();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
