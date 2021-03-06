package kon.shol;

import org.apache.kafka.clients.consumer.*;

import java.util.*;

import static kon.shol.Main.queue;

class Consumer implements Runnable {

    private KafkaConsumer<String, String> consumer;

    Consumer(String groupID, String topic) {

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "188.165.235.136:9092,188.165.230.122:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("acks", "all");
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    private void getLink() throws InterruptedException {
        while (true) {
            ConsumerRecords<String, String> records;
            do {
                records = consumer.poll(1000);
            } while (records.isEmpty());
            for (ConsumerRecord<String, String> record : records) {
                queue.add(record.value());
            }
        }
    }

    @Override
    public void run() {
        try {
            getLink();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
